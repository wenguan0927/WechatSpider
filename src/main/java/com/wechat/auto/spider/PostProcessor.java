package com.wechat.auto.spider;

import com.wechat.auto.mapper.WechatPostMapper;
import com.wechat.auto.model.WechatPost;
import com.wechat.auto.util.Utils;
import org.apache.http.util.TextUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.math.BigInteger;
import java.util.Date;

public class PostProcessor implements PageProcessor {

    // 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    private WechatPostMapper wechatPostMapper;

    private BigInteger mAndroidSimHash;

    private BigInteger mExtendSimHash;

    public void setPostMapper(WechatPostMapper postMapper) {
        this.wechatPostMapper = postMapper;
    }

    public void setKeyWordMap(BigInteger androidSimHash, BigInteger extendSimHash){
        this.mAndroidSimHash = androidSimHash;
        this.mExtendSimHash = extendSimHash;
    }

    @Override
    public void process(Page page) {
        //System.out.println("============page : " + page.getHtml().toString());

        // TODO Auto-generated method stub
        String content = page.getHtml().xpath("//div[@id='js_content']").get();
        String requestUrl = page.getUrl().toString();
        System.out.println("===========request Url : "+ requestUrl);
        if(TextUtils.isEmpty(content)){
            System.out.println("文章已和谐！");
            return;
        }

        String contentTxt = Utils.stripHtml(content).trim();//纯文本内容
        System.out.println("===========content : "+ contentTxt);
        String htmlStr = page.getHtml().toString();

        String biz = Utils.stripVarValue(htmlStr, Utils.VAR_BIZ);
        String msglink = Utils.stripVarValue(htmlStr, Utils.VAR_MSG_LINK);
        String msgId = Utils.stripVarValue(msglink, Utils.MSG_ID);
        String title= Utils.stripVarValue(htmlStr, Utils.VAR_TITLE);
        String digest = Utils.stripVarValue(htmlStr, Utils.VAR_DIGEST);
        String contentDesc = Utils.stripDesc(contentTxt);
        String time = Utils.stripVarValue(htmlStr, Utils.VAR_TIME);

        Date dateTime = Utils.strToDate(time);

        String nickName = Utils.stripVarValue(htmlStr, Utils.VAR_NICKNAME);
        String coverUrl = Utils.stripVarValue(htmlStr, Utils.VAR_COVER_URL);
        String msgSourceUrl = Utils.stripVarValue(htmlStr, Utils.VAR_MSG_SOURCE_URL);
        String author = Utils.stripVarValue(htmlStr, Utils.AUTHOR);

        String articleType = Utils.stripVarValue(htmlStr, Utils.VAR_ARTICLE_TYPE);
        if(TextUtils.isEmpty(digest)){
            digest = contentDesc;
        }

        System.out.println("===========contentTxt length: "+ contentTxt.length());
        System.out.println("===========biz : "+biz);
        System.out.println("===========msglink : "+msglink);
        System.out.println("===========msgid : "+msgId);
        System.out.println("===========title : "+title);
        System.out.println("===========desc : "+digest);
        System.out.println("===========time : "+time + " ; date time : " + dateTime.toString());
        System.out.println("===========nickName : "+nickName);
        System.out.println("===========coverUrl : "+coverUrl);
        System.out.println("===========msgSourceUrl : "+msgSourceUrl);
        System.out.println("===========autor : "+author);
        System.out.println("===========article : "+articleType);

        int postType = Utils.analysisType(nickName, title, contentTxt);
        int weight = Utils.analysisWeight(nickName, title, contentTxt);
        System.out.println("===========postType : "+postType);
        System.out.println("===========weight : "+weight);

        WechatPost post = wechatPostMapper.getPostByUrl(requestUrl);
        System.out.println("===========get post by url : "+post);
        post.setBiz(biz);
        post.setAppmsgid(msgId);
        post.setTitle(title);
        post.setDigest(digest);System.out.println("===========request Url : "+ requestUrl);
        post.setSourceurl(msgSourceUrl);
        post.setConver(coverUrl);
        post.setDatetime(dateTime);
        post.setIsspider(1);
        post.setAuthor(author);
        post.setNickname(nickName);
        post.setWeight(weight);
        post.setPosttype(postType);
        post.setContent(contentTxt);
        try{
            int updateResult = wechatPostMapper.updateByPrimaryKeySelective(post);
            System.out.println("===========update result : "+updateResult);
        }catch (Exception e){
            post.setContent("");
            int updateResult = wechatPostMapper.updateByPrimaryKeySelective(post);
            System.out.println("===========update result : "+updateResult);
        }


    }

    @Override
    public Site getSite() {
        // TODO Auto-generated method stub
        return this.site;
    }

    public static void startSpider(WechatPostMapper wechatPostMapper,
                                   String... urls){//PostMapper myPostMapper
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        PostProcessor spiderModel = new PostProcessor();
        spiderModel.setPostMapper(wechatPostMapper);
        Spider mySpider = Spider.create(spiderModel).addUrl(urls);
        mySpider.setDownloader(httpClientDownloader);
        try {
            SpiderMonitor.instance().register(mySpider);
            mySpider.thread(1).run();
        } catch (JMException e) {
            e.printStackTrace();
        }
    }

    private void simHashTest(){
        /*List<WordInfo> keyWords = HanLP.extractWords(contentTxt, 30);

        HashMap<String, Integer> postMap = new HashMap<String, Integer>();

        WordInfo tempInfo;
        for(int i=0;i<keyWords.size();i++){
            tempInfo = keyWords.get(i);
            postMap.put(tempInfo.text, tempInfo.frequency);
            System.out.println("===================post text : " + tempInfo.text +" ; frequency : " + tempInfo.frequency);
        }

        SimHash postHash = new SimHash(postMap);

        int simDistance = SimHash.hammingDistance(mAndroidSimHash, postHash.getSimHashValue());
        System.out.println("===================sim distance : " + simDistance);
       */
    }
}
