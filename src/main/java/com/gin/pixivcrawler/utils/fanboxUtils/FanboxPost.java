package com.gin.pixivcrawler.utils.fanboxUtils;

import com.alibaba.fastjson.JSONObject;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxResponseBody;
import com.gin.pixivcrawler.utils.requestUtils.GetRequest;

/**
 * fanbox请求
 * @author bx002
 */
public class FanboxPost {
    /**
     * 按作者名获取作品信息
     */
    private final static String LIST_CREATOR = "https://api.fanbox.cc/post.listCreator?limit=300&creatorId=";
    private final static String LIST_SUPPORTING = "https://api.fanbox.cc/post.listSupporting?limit=";
    private final static String POST_ID = "https://api.fanbox.cc/post.info?postId=";
    private final static String REFERER = "https://www.fanbox.cc";

    public static FanboxResponseBody listCreator(String creatorId, String cookie) {
        return getArray(cookie, LIST_CREATOR + creatorId);
    }

    public static FanboxResponseBody listSupporting(String cookie, Integer limit) {
        return getArray(cookie, LIST_SUPPORTING + limit);
    }

    public static FanboxItem findItem(String cookie,long id){
        String result = GetRequest.create()
                .addCookie(cookie)
                .addReferer(REFERER)
                .addOrigin(REFERER)
                .get(POST_ID+id);
        if (result != null) {
            JSONObject json = JSONObject.parseObject(result);
            JSONObject body = json.getJSONObject("body");
            if (body != null) {
                return JSONObject.parseObject(body.toJSONString(), FanboxItem.class);
            }
        }
        return null;
    }

    private static FanboxResponseBody getArray(String cookie, String url) {
        String result = GetRequest.create()
                .addCookie(cookie)
                .addReferer(REFERER)
                .addOrigin(REFERER)
                .get(url);
        if (result != null) {
            JSONObject json = JSONObject.parseObject(result);
            JSONObject body = json.getJSONObject("body");
            if (body != null) {
                return JSONObject.parseObject(body.toJSONString(), FanboxResponseBody.class);
            }
        }
        return null;
    }

    public static void main(String[] args) {
    }
}
