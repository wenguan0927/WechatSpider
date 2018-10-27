package com.wechat.auto.spider;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word.WordInfo;
import com.wechat.auto.mapper.PostKeywordMapper;
import com.wechat.auto.model.PostKeyword;
import com.wechat.auto.util.Utils;
import org.apache.http.util.TextUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.util.*;

public class KeyWordProcessor implements PageProcessor {

    // 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    private static PostKeywordMapper mKeywordMapper;

    private HashMap<String,Integer> mKeyWordMap = new HashMap<String, Integer>();

    private PostKeywordMapper postKeywordMapper;

    private int mUrlType = -1;

    public void setUrlType(int type){
        this.mUrlType = type;
    }

    public void setPostMapper(PostKeywordMapper keywordMapper){
        postKeywordMapper = keywordMapper;
    }

    @Override
    public void process(Page page) {
        // TODO Auto-generated method stub
        String content = page.getHtml().xpath("//div[@id='js_content']").get();

        if(TextUtils.isEmpty(content)){
            System.out.println("文章已和谐！");
            return;
        }

        String contentTxt = Utils.stripHtml(content).trim();//纯文本内容

        List<WordInfo> keyWords = HanLP.extractWords(contentTxt, 30);

        WordInfo[] wordInfos = new WordInfo[keyWords.size()];
        for(int i=0;i< keyWords.size();i++){
            wordInfos[i] = keyWords.get(i);
        }
        if (keyWords != null && keyWords.size()> 0){
            int size = keyWords.size();
            WordInfo word;
//            StringBuilder keyWordSb = new StringBuilder();
            Arrays.sort(wordInfos, new Comparator<WordInfo>() {

                @Override
                public int compare(WordInfo o1, WordInfo o2) {
                    return -(o1.frequency - o2.frequency);
                }
            });

            for(int i=0;i<size;i++){
                word = wordInfos[i];

                if(!TextUtils.isEmpty(word.text)){
                    System.out.println("==================word text : " + word.text);
                    PostKeyword wordItem = postKeywordMapper.selectKeyWord(word.text);
                    if (wordItem == null) {
                        wordItem = new PostKeyword(null, word.text, word.frequency, mUrlType);
                        postKeywordMapper.insertSelective(wordItem);
                    } else if(wordItem != null && wordItem.getWordtype()== mUrlType){
                        wordItem.setWeight(wordItem.getWordfrequency() + word.frequency);
                        postKeywordMapper.updateByPrimaryKeySelective(wordItem);
                    }
                }

//                if(mKeyWordMap.containsKey(word.text) && !TextUtils.isEmpty(word.text)){
//                    int mapValue = mKeyWordMap.get(word.text);
//                    mKeyWordMap.put(word.text, mapValue + word.frequency);
//                } else if(!TextUtils.isEmpty(word.text)){
//                    mKeyWordMap.put(word.text, word.frequency);
//                }

//                keyWordSb.append(word.text + ":" + word.frequency + " : " + word.entropy + " : " + word.aggregation + ",");
            }

//            for(String key: mKeyWordMap.keySet()){
//                keyWordSb.append(key + " : " + mKeyWordMap.get(key) + " ; ");
//            }
//            keyWordSb.deleteCharAt(keyWordSb.toString().length() -1);
           // System.out.println("=============keyword : " + keyWordSb.toString());
        }

    }

    @Override
    public Site getSite() {
        // TODO Auto-generated method stub
        return this.site;
    }


    public static void startSpider(PostKeywordMapper postKeywordMapper, int type, String... urls){//PostMapper myPostMapper
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        KeyWordProcessor spiderModel = new KeyWordProcessor();
        spiderModel.setUrlType(type);
        spiderModel.setPostMapper(postKeywordMapper);
        Spider mySpider = Spider.create(spiderModel).addUrl(urls);
        mySpider.setDownloader(httpClientDownloader);
        try {
            SpiderMonitor.instance().register(mySpider);
            mySpider.thread(1).run();
        } catch (JMException e) {
            e.printStackTrace();
        }
    }
}
