package com.wechat.auto.model;

/**
 * 用于JSP页面的文本显示，避免特殊字符串连接导致的显示问题
 */
public class PostJSP {

    private String title;

    private String digest;

    public void setTitle(String title){
        this.title = title;
    }

    public void setDigest(String digest){
        this.digest = digest;
    }

    public String getDigest() {
        return digest;
    }

    public String getTitle() {
        return title;
    }
}
