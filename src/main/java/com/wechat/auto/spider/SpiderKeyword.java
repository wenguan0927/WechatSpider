package com.wechat.auto.spider;

import com.wechat.auto.mapper.PostKeywordMapper;
import com.wechat.auto.model.PostKeyword;
import com.wechat.auto.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Component
public class SpiderKeyword {

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

    @Autowired
    private PostKeywordMapper postKeywordMapper;

    @Test
    public void test(){
        KeyWordProcessor.startSpider(postKeywordMapper, Utils.KEYWORD_TYPE_ANDROID, androidUrls);
//
//        List<PostKeyword> listdata = postKeywordMapper.selectAllKeyWord(Utils.KEYWORD_TYPE_ANDROID);

        //PostKeyword word = postKeywordMapper.selectByPrimaryKey(10);

        //System.out.println("================word text : " + word.getWordtext() + word.getWordfrequency());


//        System.out.println("================result size: " + listdata.size());
//
//        StringBuilder sb = new StringBuilder();
//        PostKeyword tempData;
//        for (int i=0;i< listdata.size();i++){
//            tempData = listdata.get(i);
//            sb.append(tempData.getWordtext() + " : " + tempData.getWordfrequency() + " , ");
//        }
//
//        System.out.println("================result : " + sb.toString());

//        int deleteResult = postKeywordMapper.deleteAll();
//        System.out.println("======================delete result : " + deleteResult);
    }


}
