package com.wechat.auto.spider;

import com.wechat.auto.mapper.PostKeywordMapper;
import com.wechat.auto.mapper.WechatPostMapper;
import com.wechat.auto.model.PostKeyword;
import com.wechat.auto.model.WechatPost;
import com.wechat.auto.util.SimHash;
import com.wechat.auto.util.Utils;
import org.apache.http.util.TextUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Component
public class SpiderTimer {

    private final String HOST = "https://mp.weixin.qq.com";

    @Autowired
    private WechatPostMapper wechatPostMapper;

    @Autowired
    private PostKeywordMapper postKeywordMapper;

    final String testUrl = "http://mp.weixin.qq.com/s?__biz=MzI4MTY0NTk4MQ==&amp;mid=2247484110&amp;idx=1&amp;sn=fd08275aa190bddea9fcbb9a91232cf6&amp;chksm=eba7443cdcd0cd2aaf00f919fd0104eadd55b50f8120c4e9e22f40a3c74acbbd1a807a7d3b44#rd";

    final String secondTestUrl = "http://mp.weixin.qq.com/s?__biz=MzIwMzg1ODcwMw==&mid=2247488336&idx=1&sn=1d25c6e3485d0b7fba1f0b10b1f12ffe&chksm=96c9a530a1be2c268113b35fb2d53c2cfd98167fbb9b36c8dd7b4b1a8bedf0242e6088393b13&scene=0&ascene=7&devicetype=android-19&version=26060739&nettype=WIFI&abtest_cookie=AgABAAoACwACACOXHgBOmR4AAAA%3D&lang=zh_CN&pass_ticket=RYeEnVy%2FW8pxtsjG0t7gu%2FrWkuQsyC5o4gdh89ZCXgC2GOyPHAe8S5D%2Bah319vMi&wx_header=1";

    final String thirdUrl = "http://mp.weixin.qq.com/s?__biz=MjM5NjQ5MTI5OA==&mid=2651748649&idx=2&sn=3909ebe4e33eed50fe5d75b61a3d4203&chksm=bd12a0648a652972bf89f9bfc14cfbcaff5aee2fb617e89cc084bd2c1c0a58e5958f39cb32f2&scene=0&ascene=7&devicetype=android-19&version=26060739&nettype=WIFI&abtest_cookie=AwABAAoACwATAAMAI5ceAE6ZHgBgmR4AAAA%3D&lang=zh_CN&pass_ticket=yRcuyf%2B5SuA38nMbQuSiK1B2wFfy9fSISPlx56cyiVUBqMafBuEKKSfC8ZnnMKw9&wx_header=1 HTTP/1.1";

    final String fourthUrl = "https://mp.weixin.qq.com/s/nPag5F9O6ALtonXYFoe9mw";

    final String infoQUrl = "https://mp.weixin.qq.com/s/sNzFqsVt7MUelxF0t0H1xA";

    final String jiagouUrl = "https://mp.weixin.qq.com/s/xjkH_4gXqdH9tIruxXHZNA";

    String[] androidUrls = new String[]{
    "https://mp.weixin.qq.com/s/EY_izhGNYw9xu9Tn-chgwA",
    "https://mp.weixin.qq.com/s/zr8om365lkjDimhB3ecD7A",
    "https://mp.weixin.qq.com/s/CMObbAxT8gRVuCiKvxo-cA",
    "https://mp.weixin.qq.com/s/eqL7VM6SqeeIMVaWLHg_6A",
    "https://mp.weixin.qq.com/s/9TzsJn6SDWkJA3XFxsJHwg",
    "https://mp.weixin.qq.com/s/MpABEoN-2G_3a2jVz41x3g",
    "https://mp.weixin.qq.com/s/XfBt_gTw0gN7tXzuyP4PTw",
    "https://mp.weixin.qq.com/s/woIJf1QIB5HBIVPLWR6AIQ",
    "https://mp.weixin.qq.com/s/_s88Xjti0YwO4rayKvF5Dg",
    "https://mp.weixin.qq.com/s/ep-Assy2j_EOUW8uWUQfSQ",
    "https://mp.weixin.qq.com/s/hIbiXnxtqocps-A6gN-UnQ",
    "https://mp.weixin.qq.com/s/Nx4h4boKTyHqfF8gqpFjqg",
    "https://mp.weixin.qq.com/s/AFfld0aFdPxxYPFC0eKeBw",
    "https://mp.weixin.qq.com/s/2Nw5UGbJeN_w3vTGSxd5XA",
    "https://mp.weixin.qq.com/s/2QxO1MQD6dXLDC5f8_akhw",
    "https://mp.weixin.qq.com/s/JHpH-Wb59aHOI8ZHyTI7Tw",
    "https://mp.weixin.qq.com/s/OWImTj_4Ml1nmpN2v9mRAw",
    "https://mp.weixin.qq.com/s/Rt2EV_hZ_CysUJ-fisj3ug",
    "https://mp.weixin.qq.com/s/v_aauFjx-f91WrpCAaNMVQ",
    "https://mp.weixin.qq.com/s/DDCPBfPJh7DVdAy9arMT3g",
    "https://mp.weixin.qq.com/s/iFMJepfn_c7KVvAg5pTelg",
    "https://mp.weixin.qq.com/s/GHYXOgbVL17d-UQwAy9-zg",
    "https://mp.weixin.qq.com/s/fatc5Q8z68p6ZOV70-OBQg",
    "https://mp.weixin.qq.com/s/trGUovRN2P0WcewHM7KfrQ",
    "https://mp.weixin.qq.com/s/jZ-i-9HWqhHeJAIYI06uFQ",
    "https://mp.weixin.qq.com/s/bNnWyqdUp984iyC9dtc6WA",
    "https://mp.weixin.qq.com/s/lonkZPoHwFeVnbrQTluA7Q",
    "https://mp.weixin.qq.com/s/M9_ggbxxGuvQj8inDSNTJw",
    "https://mp.weixin.qq.com/s/4uE-MNWzBShGw9OY-pTAKA",
    "https://mp.weixin.qq.com/s/2kMAtXtR_qqb02yEzQO9YA",
    "https://mp.weixin.qq.com/s/pKRi5qpZmol7xFIfeBbK_A",
    "https://mp.weixin.qq.com/s/twESi_-oymFN4FioUkhmBg",
    "https://mp.weixin.qq.com/s/w7tJthL0G1HyU-AfO21i_Q",
    "https://mp.weixin.qq.com/s/9GrZa4pGBIPDHmyveqhL1A",
    "https://mp.weixin.qq.com/s/7EkXNY50jFmgAeusM-reZw",
    "https://mp.weixin.qq.com/s/oSBUA7QKMWZURm1AHMyubA",
    "https://mp.weixin.qq.com/s/DPi0edk_7MAOm_N3X9Tz0w",
    "https://mp.weixin.qq.com/s/22ylXNbQAzBWoQR-iYZ9Yw",
    "https://mp.weixin.qq.com/s/xBoJs08UjhtrEihVACi7qA",
    "https://mp.weixin.qq.com/s/kIu-M_Fc-EGKZiPqxb_OjQ",
    "https://mp.weixin.qq.com/s/vqTZtNy45r8YrbdR7gIA0Q",
    "https://mp.weixin.qq.com/s/xQKePgJfaj2RjuOgzY8-0A",
    "https://mp.weixin.qq.com/s/7i_XEsdCs8UGJpJcLLOM2w",
    "https://mp.weixin.qq.com/s/tk0Cju0XEO8r0QtkFkLsBg",
    "https://mp.weixin.qq.com/s/UKgPtJyMF8CJ8ffUuwCCvg",
    "https://mp.weixin.qq.com/s/VNBwPPJeENyU9iyxJcN1qw",
    "https://mp.weixin.qq.com/s/fVcTm9Dk-2wCVqYIk7xnCw"
    };

    String[] techExtendUrls = new String[]{
    "https://mp.weixin.qq.com/s/7kKhAYtkIuvlBnZlaYATnw",
    "https://mp.weixin.qq.com/s/O3wHbI1fT9_AlP7Aq_Juog",
    "https://mp.weixin.qq.com/s/C6gBmwgYGAN7gsF40-JBgQ",
    "https://mp.weixin.qq.com/s/7eq2XTlJ2G15qv8icB70_Q",
    "https://mp.weixin.qq.com/s/IcNyite_i7duvRiZ7qVVRQ",
    "https://mp.weixin.qq.com/s/nPag5F9O6ALtonXYFoe9mw",
    "https://mp.weixin.qq.com/s/FP8dlLlNN55aFLMQ7TyAyw",
    "https://mp.weixin.qq.com/s/bTh8N45BLNQHSLUZAakkCA",
    "https://mp.weixin.qq.com/s/RBOUrm-jXyXkIDIg9Slo6w",
    "https://mp.weixin.qq.com/s/INMfRNHxzRODnbWJ4b0t7w",
    "https://mp.weixin.qq.com/s/AqynVMjVqKkULBb4_Vqz-Q",
    "https://mp.weixin.qq.com/s/emEuhftLbAQmdn1LQYOSBw",
    "https://mp.weixin.qq.com/s/H5wBNAm93uPJDvCQCg0_cg",
    "https://mp.weixin.qq.com/s/FWohPX6QTwlZKqTQMGz6BA",
    "https://mp.weixin.qq.com/s/tOhz-u-OVlXzZL7AnwFukA"
    };

    private HashMap<String, Integer> mAndroidKeyMap;

    String[] testUrls = new String[]{
            "https://mp.weixin.qq.com/s/CGXf5DSH7Oa2J94cJVPMKA"
    };
    @Test
    public void test(){
        startWechatSpider();
        //String[] arrayUrls = new String[]{fourthUrl};
        //PostProcessor.startSpider(wechatPostMapper, arrayUrls);
        //clearData();
        //reSetContent();
    }

    private void clearData(){
        int deleteResult = wechatPostMapper.deleteAllData();
        System.out.println("====================deleteResult : " + deleteResult);
    }

    private void simHashTest(){
        List<PostKeyword> keywordList = postKeywordMapper.selectAllKeyWord(Utils.KEYWORD_TYPE_ANDROID);

        mAndroidKeyMap = new HashMap<String, Integer>();
        PostKeyword tempData;
        for (int i = 0; i < keywordList.size(); i++) {
            tempData = keywordList.get(i);
            mAndroidKeyMap.put(tempData.getWordtext(), tempData.getWordfrequency());
        }

        SimHash androidHash = new SimHash(mAndroidKeyMap);

        //PostProcessor.startSpider(null,null, wechatPostMapper, testUrls);

        List<WechatPost> posts = wechatPostMapper.getAllPost();
        System.out.println("=================posts size : " + posts.size());
        for(WechatPost post : posts){
            post.setIsspider(0);
            System.out.println("=================posts size : " + post.getContenturl());
            wechatPostMapper.updateByPrimaryKeySelective(post);
        }
    }

    private void preSeeData(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, - 7);
        Date weekBefore = c.getTime();


        List<WechatPost> newsPost = wechatPostMapper.getPostByTypeInWeek(Utils.KEYWORD_TYPE_NEWS, weekBefore);

        List<WechatPost> androidPost = wechatPostMapper.getPostByTypeInWeek(Utils.KEYWORD_TYPE_ANDROID, weekBefore);

        List<WechatPost> extendPost = wechatPostMapper.getPostByTypeInWeek(Utils.KEYWORD_TYPE_EXTEND, weekBefore);

        if(newsPost != null){
            System.out.println("==================new post size : " + newsPost.size());
        } else {
            System.out.println("==================new post is null ");
        }

        if(androidPost != null){
            System.out.println("==================android post size : " + androidPost.size());
        } else {
            System.out.println("==================android post is null ");
        }

        if(extendPost != null){
            System.out.println("==================extend post size : " + extendPost.size());
        } else {
            System.out.println("==================extend post is null ");
        }
    }

    private void reSetContent(){
        List<WechatPost> posts = wechatPostMapper.getAllPost();
        WechatPost tempPost = null;
        for(int i= 0;i<posts.size();i++){
            tempPost = posts.get(i);
            tempPost.setContent("");
            tempPost.setIsspider(0);
            int updateResult = wechatPostMapper.updateByPrimaryKeySelective(tempPost);
            System.out.println("================update Result : " + updateResult);
        }
    }

    public void startWechatSpider(){
        List<WechatPost> posts = wechatPostMapper.getAllUnSpiderPost(0);
        System.out.println("=================posts size : " + posts.size());
        System.out.println(posts.size());
        boolean isAgain = false;
        for(WechatPost post : posts){
            System.out.println("================post url : " + post.getContenturl());
            if(TextUtils.isEmpty(post.getContenturl())){
                wechatPostMapper.deleteByPrimaryKey(post.getId());
                System.out.println("链接为空，删除文章！！");
                isAgain = true;
            }
        }

        //如果有链接剔除，则重新读取
        if(isAgain){
            posts = wechatPostMapper.getAllUnSpiderPost(null);
        }

        if(posts != null && posts.size() >0){
            List<String> urls = new ArrayList<String>();
            for(WechatPost mypost : posts){
                if(mypost.getContenturl().startsWith(HOST)){
                    urls.add(mypost.getContenturl());
                } else {
                    urls.add(HOST + mypost.getContenturl());
                }
            }
            String[] arrayUrls = Utils.listToArray(urls);
            PostProcessor.startSpider(wechatPostMapper, arrayUrls);
        }
    }

}
