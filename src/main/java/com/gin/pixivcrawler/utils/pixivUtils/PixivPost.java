package com.gin.pixivcrawler.utils.pixivUtils;

import com.alibaba.fastjson.JSONObject;
import com.gin.pixivcrawler.utils.requestUtils.GetRequest;

import static com.gin.pixivcrawler.utils.JsonUtil.printJson;

/**
 * Pixiv请求工具类
 *
 * @author bx002
 * @date 2021/2/1 17:43
 */
public class PixivPost {
    private static final String DOMAIN = "https://www.pixiv.net";
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PixivPost.class);
    /**
     * 作品详情接口
     */
    private final static String URL_ILLUST_DETAIL = "https://www.pixiv.net/ajax/illust/%d";


    public static String getIllustDetail(long pid,String cookie){
        return GetRequest.create().addCookie(cookie).get(String.format(URL_ILLUST_DETAIL,pid));
    }


    public static void main(String[] args) {
//        long pid = 87460135;
        long pid = 874601350;
        String detail = getIllustDetail(pid, null);
        printJson(JSONObject.parse(detail));
    }
}
