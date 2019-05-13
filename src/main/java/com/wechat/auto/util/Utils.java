package com.wechat.auto.util;

import org.apache.http.util.TextUtils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {

    public static final String VAR_BIZ = "var appuin = \"";
    public static final String VAR_TITLE = "var msg_title = \"";
    public static final String VAR_DIGEST = "var msg_desc = \"";
    public static final String VAR_TIME = "var publish_time = \"";
    public static final String VAR_NICKNAME = "var nickname = \"";
    public static final String VAR_COVER_URL = "var msg_cdn_url = \"";
    public static final String VAR_MSG_SOURCE_URL = "var msg_source_url = '";
    public static final String VAR_MSG_LINK = "var msg_link = \"";
    public static final String VAR_ARTICLE_TYPE = "var _ori_article_type = \"";
    public static final String MSG_ID = "mid=";
    public static final String AUTHOR = "<span class=\"rich_media_meta rich_media_meta_text\">";

    public static String[] tagArray = new String[]{"背景" , "前言"};

    public static String[] fieldNews = new String[]{"车云", "InfoQ"};

    public static String[] androidDevelop = new String[]{"Android", "View", "Binder", "C++", "APK", "App", "移动端",
            "Gradle", "Fragment", "Java", "JDK", "Gson", "JVM", "Handler"};

    public static String[] cppDevelop = new String[]{"C++"};

    public static String[] techExtend = new String[]{"Node.js", "架构", "算法", "MySQL", "优化实践", "高可用", "React",
            "人工智能", "Go", "机器学习", "优化实战", "区块链", "开源", "优化实战","深度学习", "框架"};

    public static final int KEYWORD_TYPE_NEWS = 1;

    public static final int KEYWORD_TYPE_ANDROID = 2;

    public static final int KEYWORD_TYPE_CPP = 3;

    public static final int KEYWORD_TYPE_EXTEND = 4;

    public static String[] ATA_NICK_LIST = new String[]{"机器之心","量子位", "AI前线", "新智元", "36氪", "雷锋网", "InfoQ", "CSDN"};

    /**
     * 解析文章类型
     *
     * @param nickName
     * @return 文章类型：1、行业新闻  2、Android开发  3、C++开发  4、技术扩展
     */
    public static int analysisType(String nickName, String title, String contentTxt){
        if("InfoQ".equals(nickName) && title.contains("Q新闻")){
            return 1;
        }
        if("AI前线".equals(nickName) && title.contains("一周热闻")){
            return 1;
        }
        if("车云".equals(nickName)){
            return 1;
        }

        for (int i=0;i<androidDevelop.length;i++){
            if(title.contains(androidDevelop[i])){
                return 2;
            }
        }

        for (int i=0;i<cppDevelop.length;i++){
            if(title.contains(cppDevelop[i])){
                return 3;
            }
        }

        for (int i=0;i<techExtend.length;i++){
            if(title.contains(techExtend[i])){
                return 4;
            }
        }

        for (int i=0;i<androidDevelop.length;i++){
            if(contentTxt.contains(androidDevelop[i])){
                return 2;
            }
        }

        for (int i=0;i<cppDevelop.length;i++){
            if(contentTxt.contains(cppDevelop[i])){
                return 3;
            }
        }

        for (int i=0;i<techExtend.length;i++){
            if(contentTxt.contains(techExtend[i])){
                return 4;
            }
        }

        return -1;
    }

    /**
     * 获取文章权重，一般根据文章的长度即可
     *
     * @param nickName
     * @param title
     * @param contentTxt
     * @return
     */
    public static int analysisWeight(String nickName,String title, String contentTxt){
        if("InfoQ".equals(nickName) && title.contains("Q新闻")){
            return Integer.MAX_VALUE;
        } else if("AI前线".equals(nickName) && title.contains("一周热闻")){
            return Integer.MAX_VALUE;
        }
        return contentTxt.length();
    }

    /**
     * 是否值得存储，对数据做过滤
     *
     * @param nickName
     * @param title
     * @return
     */
    public static boolean isWorthSave(String nickName, String title){
        if("InfoQ".equals(nickName) && !title.contains("Q新闻")){
            return false;
        }
        return true;
    }

    /**
     * 字符串转化为DATE 对象
     *
     * @param time
     * @return
     */
    public static Date strToDate(String time){
        time = time + " 01:00:00";

        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date strToDateReset(String time){
        time = time + " 00:00:00";

        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToStr(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(date.getTime());
        return time;
    }

    /**
     * 抽取Var定义的变量值
     *
     * @param originalHtml
     * @param varValue
     * @return
     */
    public static String stripVarValue(String originalHtml, String varValue){
        int bizFromIndex = originalHtml.indexOf(varValue) + varValue.length();
        String endChar = "\"";
        if(VAR_MSG_SOURCE_URL.equals(varValue)){
            endChar = "\'";
        } else if(AUTHOR.equals(varValue)){
            endChar = "<";
        } else if (MSG_ID.equals(varValue)){
            endChar = "&";
        }
        int bizEndIndex = originalHtml.indexOf(endChar, bizFromIndex);
        if(bizEndIndex <= bizFromIndex){
            return null;
        }
        return originalHtml.substring(bizFromIndex, bizEndIndex);
    }

    /**
     * 截取内容简介
     *
     * @param sourceContent
     * @return
     */
    public static String stripDesc(String sourceContent){
        int size = tagArray.length;
        String desc = null;
        for(int i = 0; i< size; i++){
            desc = searchTag(sourceContent, tagArray[i]);
            if (desc != null){
                return desc;
            }
        }
        return sourceContent.substring(0, 150).trim() + "...";
    }

    /**
     * 截取标签文本之后的内容说明
     *
     * @param sourceContent
     * @param tag
     * @return
     */
    public static String searchTag(String sourceContent, String tag){
        int fromIndex = sourceContent.indexOf(tag);
        if(fromIndex > 0){
            if(fromIndex -1 > 0){
                /**
                 * 避免在正文内容中出现的标签文本内容导致内容截取错误
                 */
                if(!TextUtils.isEmpty(sourceContent.substring(fromIndex-1, fromIndex))){
                    return null;
                }
            }
            return sourceContent.substring(fromIndex + tag.length(), getPreferDotIndex(sourceContent, fromIndex) + 1);
        }
        return null;
    }

    /**
     * 最多截取三句话
     *
     * @param sourceContent
     * @param fromIndex
     * @return
     */
    public static int getPreferDotIndex(String sourceContent, int fromIndex){
        int firstDotIndex = sourceContent.indexOf("。", fromIndex);
        int secondDotIndex = sourceContent.indexOf("。", firstDotIndex + 1);
        int thirdDotIndex = sourceContent.indexOf("。", secondDotIndex + 1);
        if(thirdDotIndex > 0){
            return thirdDotIndex;
        }
        if(secondDotIndex > 0){
            return secondDotIndex;
        }
        return firstDotIndex;
    }

    public static String stripHtml(String content) {
        if(content == null){
            return "";
        }
        //去掉script
        content = content.replaceAll("<script[^>]*?>[\\s\\S]*?<\\/script>", "");

        content = content.replaceAll("\\<.*?>", ""); // 去掉其它的<>之间的东西

        //去掉空格
        content = content.replaceAll("&nbsp;", "");

        return content;
    }

    /**
     * 字符集合转字符数组
     * @param resource
     * @return
     */
    public static String[] listToArray(List<String> resource){
        String[] strArray = new String[resource.size()];
        for(int i=0 ;i < resource.size() ;i++){
            strArray[i] = resource.get(i);
        }
        return strArray;
    }

}
