package com.wechat.auto.controller;

import com.wechat.auto.mapper.WechatPostMapper;
import com.wechat.auto.model.PostJSP;
import com.wechat.auto.model.WechatPost;
import com.wechat.auto.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/getData")
public class WechatController {

	@Autowired
	private WechatPostMapper wechatPostMapper;

	private final String HOST = "https://mp.weixin.qq.com";

	@RequestMapping("/getWxPost")
	public void getWxPost(HttpServletRequest request, HttpServletResponse response){
		String url = request.getParameter("url");
		try{
			url = URLDecoder.decode(url, "utf-8");
			System.out.println("=================url : "+ url);
			WechatPost post;
			post = wechatPostMapper.getPostByUrl(HOST + url);
			System.out.println("=================post : "+ post);
			if(post == null){
				post = new WechatPost();
				post.setContenturl(HOST + url);
				post.setIsspider(0);
				wechatPostMapper.insert(post);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@RequestMapping(value="/weekly_posts")
	public ModelAndView weeklyPosts(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, - 7);
		Date weekBefore = c.getTime();

		System.out.println("================weekbefore : " + weekBefore.getTime());
		List<WechatPost> newsPost = wechatPostMapper.getPostByTypeInWeek(Utils.KEYWORD_TYPE_NEWS, weekBefore);

		List<WechatPost> androidPost = wechatPostMapper.getPostByTypeInWeek(Utils.KEYWORD_TYPE_ANDROID, weekBefore);

		List<WechatPost> extendPost = wechatPostMapper.getPostByTypeInWeek(Utils.KEYWORD_TYPE_EXTEND, weekBefore);

		List<PostJSP> newsJSP = new ArrayList<PostJSP>();
        List<PostJSP> androidsJSP = new ArrayList<PostJSP>();
        List<PostJSP> extendsJSP = new ArrayList<PostJSP>();

        initJspData(newsJSP, newsPost);
        initJspData(androidsJSP, androidPost);
        initJspData(extendsJSP, extendPost);

		ModelAndView mdl = new ModelAndView();

		mdl.setViewName("weekly_posts");
		mdl.addObject("newspost", newsJSP);
		mdl.addObject("androidspost", androidsJSP);
		mdl.addObject("extendspost", extendsJSP);

		return mdl;
	}

	private void initJspData(List<PostJSP> jspArray, List<WechatPost> postArray){
        if(postArray != null && postArray.size() > 0){
            String title = null;
            WechatPost itemPost = null;
            PostJSP itemJsp = null;
            int arraySize = postArray.size();
            for (int i=0;i< arraySize;i++){
                itemPost = postArray.get(i);
                title = "###" + String.valueOf(i+1) + ".["+itemPost.getTitle() + "](" + itemPost.getContenturl() +")";
                itemJsp = new PostJSP();
                itemJsp.setTitle(title);
                itemJsp.setDigest(itemPost.getDigest());
                jspArray.add(i, itemJsp);
            }
        }
    }
}
