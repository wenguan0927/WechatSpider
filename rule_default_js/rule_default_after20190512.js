var utils      = require("./util"),
    bodyParser = require("body-parser"),
    path       = require("path"),
    fs         = require("fs"),
    img = fs.readFileSync("/Users/chenwenguan/.nvm/versions/node/v8.9.3/lib/node_modules/anyproxy/lib/one_pixel.png"),//代码绝对路径替换成自己的
    Promise    = require("promise");

var isRootCAFileExists = require("./certMgr.js").isRootCAFileExists(),
    interceptFlag      = false;

//e.g. [ { keyword: 'aaa', local: '/Users/Stella/061739.pdf' } ]
var mapConfig = [],
    configFile = "mapConfig.json";
function saveMapConfig(content,cb){
    new Promise(function(resolve,reject){
        var anyproxyHome = utils.getAnyProxyHome(),
            mapCfgPath   = path.join(anyproxyHome,configFile);

        if(typeof content == "object"){
            content = JSON.stringify(content);
        }
        resolve({
            path    :mapCfgPath,
            content :content
        });
    })
    .then(function(config){
        return new Promise(function(resolve,reject){
            fs.writeFile(config.path, config.content, function(e){
                if(e){
                    reject(e);
                }else{
                    resolve();
                }
            });
        });
    })
    .catch(function(e){
        cb && cb(e);
    })
    .done(function(){
        cb && cb();
    });
}
function getMapConfig(cb){
    var read = Promise.denodeify(fs.readFile);

    new Promise(function(resolve,reject){
        var anyproxyHome = utils.getAnyProxyHome(),
            mapCfgPath   = path.join(anyproxyHome,configFile);

        resolve(mapCfgPath);
    })
    .then(read)
    .then(function(content){
        return JSON.parse(content);
    })
    .catch(function(e){
        cb && cb(e);
    })
    .done(function(obj){
        cb && cb(null,obj);
    });
}

setTimeout(function(){
    //load saved config file
    getMapConfig(function(err,result){
        if(result){
            mapConfig = result;
        }
    });
},1000);

//wexin begin
function HttpPost(url, path, referer) {//将json发送到服务器，str为json内容，url为历史消息页面地址，path是接收程序的路径和文件名
    console.log("开始执行转发操作");
    // console.log("开始执行转发操作str"+str);
    console.log("开始执行转发操作url"+url);
    console.log("开始执行转发操作path"+path);
    try{
        var http = require('http');
        var data = {
            //str: encodeURIComponent(str),
            url: encodeURIComponent(url),
        };
        data = require('querystring').stringify(data);
        var options = {
            method: "POST",
            host: "localhost",//注意没有http://，这是服务器的域名。
            port: 8080,
            path: path,//接收程序的路径和文件名
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                "Content-Length": data.length
            }
        };
        var req = http.request(options, function (res) {
            res.setEncoding('utf8');
            res.on('data', function (chunk) {
                console.log('BODY-xs: ' + chunk);
            });
        });
        req.on('error', function (e) {
            console.log('problem with request: ' + e.message);
        });

        req.write(data);
        req.end();
    }catch(e){
        console.log("错误信息："+e);
    }
    console.log("转发操作结束-xs");
}
//wexin end

module.exports = {
    token: Date.now(),
    summary:function(){
        var tip = "the default rule for AnyProxy.";
        if(!isRootCAFileExists){
            tip += "\nRoot CA does not exist, will not intercept any https requests.";
        }
        return tip;
    },

    shouldUseLocalResponse : function(req,reqBody){
         console.log("====================shouldUseLocalResponse req url: "+req.url);
         console.log("====================shouldUseLocalResponse reqBody: "+reqBody);
         if(/r=0/i.test(reqBody) && /mp\/getappmsgext\?/i.test(req.url)){
            try {
                HttpPost(reqBody, "http://localhost:8080/WechatSpider/getData/getWxPost");
            }catch(e){
            
            }
        }
        if(/mmbiz\.qpic\.cn|wx\.qlogo\.cn/i.test(req.url)){
           req.replaceLocalFile = true;
           return true;
        }
        //intercept all options request
        var simpleUrl = (req.headers.host || "") + (req.url || "");
        mapConfig.map(function(item){
            var key = item.keyword;
            if(simpleUrl.indexOf(key) >= 0){
                req.anyproxy_map_local = item.local;
                return false;
            }
        });


        return !!req.anyproxy_map_local;
    },

    dealLocalResponse : function(req,reqBody,callback){
        if(req.replaceLocalFile){
           callback(200, {"content-type":"image/png"}, img);
        } else if(req.anyproxy_map_local){
            fs.readFile(req.anyproxy_map_local,function(err,buffer){
                if(err){
                    callback(200, {}, "[AnyProxy failed to load local file] " + err);
                }else{
                    var header = {
                        'Content-Type': utils.contentType(req.anyproxy_map_local)
                    };
                    callback(200, header, buffer);
                }
            });
        }
    },

    replaceRequestProtocol:function(req,protocol){
    },

    replaceRequestOption : function(req,option){
        var newOption = option;
        //这里面的正则可以替换成自己不希望访问的网址特征字符串，这里面的btrace是一个腾讯视频的域名，经过实践发现特别容易导致浏览器崩溃，所以加在里面了，继续添加可以使用|分割。
        if(/google|btrace/i.test(newOption.headers.host)){
           newOption.hostname = "127.0.0.1";//这个ip也可以替换成其他的
           newOption.port     = "80";
        }
        return newOption;
    },

    replaceRequestData: function(req,data){
    },

    replaceResponseStatusCode: function(req,res,statusCode){
    },

    replaceResponseHeader: function(req,res,header){
       
    },

    // Deprecated
    // replaceServerResData: function(req,res,serverResData){
    //     return serverResData;
    // },

    replaceServerResDataAsync: function(req,res,serverResData,callback){
        callback(serverResData);
    },

    pauseBeforeSendingResponse: function(req,res){
    },

    shouldInterceptHttpsReq:function(req){
        return interceptFlag;
    },

    //[beta]
    //fetch entire traffic data
    fetchTrafficData: function(id,info){},

    setInterceptFlag: function(flag){
        interceptFlag = flag && isRootCAFileExists;
    },

    _plugIntoWebinterface: function(app,cb){

        app.get("/filetree",function(req,res){
            try{
                var root = req.query.root || utils.getUserHome() || "/";
                utils.filewalker(root,function(err, info){
                    res.json(info);
                });
            }catch(e){
                res.end(e);
            }
        });

        app.use(bodyParser.json());
        app.get("/getMapConfig",function(req,res){
            res.json(mapConfig);
        });
        app.post("/setMapConfig",function(req,res){
            mapConfig = req.body;
            res.json(mapConfig);

            saveMapConfig(mapConfig);
        });

        cb();
    },

    _getCustomMenu : function(){
        return [
            // {
            //     name:"test",
            //     icon:"uk-icon-lemon-o",
            //     url :"http://anyproxy.io"
            // }
        ];
    }
};