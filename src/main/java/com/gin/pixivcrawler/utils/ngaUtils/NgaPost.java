package com.gin.pixivcrawler.utils.ngaUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gin.pixivcrawler.utils.ngaUtils.entity.*;
import com.gin.pixivcrawler.utils.ngaUtils.entity.NgaThread;
import com.gin.pixivcrawler.utils.requestUtils.GetRequest;
import com.gin.pixivcrawler.utils.requestUtils.PostRequest;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * nga请求类
 *
 * @author bx002
 * @date 2021/1/14 10:50
 */
@SuppressWarnings("SpellCheckingInspection")
@Slf4j
public class NgaPost {
    private static final List<String> ACTIONS = Arrays.asList("new", "reply", "quote", "modify");
    private static final Pattern REPLY_SUCCESS_PATTERN = Pattern.compile("pid\\d+Anchor");
    private static final Pattern REPLY_SUCCESS_PATTERN_2 = Pattern.compile("pid=\\d+");
    public static final String DOMAIN = "https://bbs.nga.cn/";
    public static final Charset GBK = Charset.forName("gbk");

    /**
     * 获取帖子列表
     *
     * @param fid         版面id
     * @param page        页码
     * @param cookie      cookie
     * @param otherParams 其他参数
     * @return com.gin.pixivcrawler.utils.ngaUtils.entity.NgaResThreadCollection
     * @author bx002
     * @date 2021/1/15 9:12
     */
    public static <T extends NgaResThreadCollection> T findThreadCollection(long fid,
                                                                            int page,
                                                                            String cookie,
                                                                            HashMap<String, Object> otherParams,
                                                                            Class<T> clazz) {
        HashMap<String, Object> map = new HashMap<>(otherParams);
        map.put("fid", fid);
        map.put("page", page);
        return getFindRequest(cookie, "thread.php", map, clazz);
    }

    /**
     * 查询精华区帖子
     *
     * @param fid    版面id
     * @param page   页码
     * @param cookie cookie
     * @return com.gin.pixivcrawler.utils.ngaUtils.entity.NgaResThreadCollection
     * @author bx002
     * @date 2021/1/15 9:30
     */
    public static NgaResThreadCollection findRecommendThreads(long fid, int page, String cookie) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("recommend", 1);
        map.put("admin", 1);
        NgaResThreadList threadList = findThreadCollection(fid, page, cookie, map, NgaResThreadList.class);
        if (threadList == null) {
            return null;
        }
        List<NgaThread> threads = threadList.getThreads().stream().filter(t -> t.getTid() != null).collect(Collectors.toList());
        if (threads.size() > 0) {
            return threadList;
        }
        return findThreadCollection(fid, page, cookie, map, NgaResThreadMap.class);
    }

    /**
     * 查询单个帖子数据
     *
     * @param tid      tid
     * @param page     页码
     * @param cookie   cookie
     * @param pid      pid
     * @param authorId 作者id
     * @return com.gin.pixivcrawler.utils.ngaUtils.entity.NgaResThread
     * @author bx002
     * @date 2021/1/15 9:30
     */
    public static NgaResThread findThreadDetails(Long tid, Integer page, String cookie, Long pid, String authorId) {
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("tid", tid);
        map.put("page", page);
        map.put("pid", pid);
        map.put("authorid", authorId);
        return getFindRequest(cookie, "read.php", map, NgaResThread.class);
    }

    private static <T extends NgaResBase> T getFindRequest(String cookie, String port, Map<String, Object> paramMap, Class<T> clazz) {
        GetRequest request = GetRequest.create().addCookie(cookie)
                .addParam("__output", 11);
        if (paramMap != null) {
            paramMap.forEach(request::addParam);
        }
        String resStr = request.get(DOMAIN + port);
        JSONObject json = JSONObject.parseObject(resStr);
        JSONArray error = json.getJSONArray("error");
        if (error != null) {
            System.err.println(error.getString(0));
            return null;
        }
        String data = json.getJSONObject("data").toJSONString();
        return JSONObject.parseObject(data, clazz);
    }

    /**
     * 发布主题、回复、编辑回复
     *
     * @param cookie           cookie
     * @param action           动作 :"new", "reply", "quote", "modify"
     * @param pid              pid 编辑用
     * @param tid              tid
     * @param fid              fid
     * @param subject          标题
     * @param content          正文
     * @param attachments      附件  https://bbs.nga.cn/read.php?tid=6406100
     * @param attachmentsCheck 附件check
     * @return java.lang.String
     * @author bx002
     * @date 2021/1/16 14:39
     */
    public static String postThread(String cookie,
                                    String action,
                                    Long pid,
                                    Long tid,
                                    Long fid,
                                    String subject,
                                    String content,
                                    String attachments,
                                    String attachmentsCheck) {
        if (!ACTIONS.contains(action)) {
            throw new RuntimeException("action非法 :" + action);
        }


        String post = (String) PostRequest.create()
//                .setDecodeEnc("gb2312").setEncodeEnc("gb2312")
                .setDecodeEnc("gbk").setEncodeEnc("gbk")
                .addCookie(cookie)
                .addParam("step", 2)
                .addParam("action", action)
                .addParam("fid", fid)
                .addParam("pid", pid)
                .addParam("tid", tid)
                .addParam("lite", "js")
                .addParam("step", "2")
                .addParam("post_subject", subject)
                .addEntityString("post_content", content)
//                .addParam("post_content", content)
//                .addParam("tpic_misc_bit1", "40")
                .addParam("attachments", attachments)
                .addParam("attachments_check", attachmentsCheck)
                .post(DOMAIN + "post.php");

        post = post.replace("window.script_muti_get_var_store=", "");

        Matcher matcher = REPLY_SUCCESS_PATTERN.matcher(post);
        if (matcher.find()) {
            String replyPid = matcher.group().replace("pid", "").replace("Anchor", "");
            String msg = String.format("发帖成功 %sread.php?pid=%s", DOMAIN, replyPid);
            log.info(msg);
            return msg;
        }
        Matcher matcher2 = REPLY_SUCCESS_PATTERN_2.matcher(post);
        if (matcher2.find()) {
            String msg = String.format("发帖成功 %sread.php?%s", DOMAIN, matcher2.group());
            log.info(msg);
            return msg;
        }
        JSONObject json = JSONObject.parseObject(post);
        String msg = json.getJSONObject("error").getString("0");
        log.warn(msg);
        return msg;
    }

    /**
     * 编辑回复
     *
     * @param cookie  cookie
     * @param pid     pid
     * @param tid     tid
     * @param subject 标题
     * @param content 正文
     * @return java.lang.String
     * @author bx002
     * @date 2021/1/28 14:03
     */
    public static String modify(String cookie, long pid, long tid, String subject, String content) {
        return postThread(cookie, "modify", pid, tid, null, subject, content, null, null);
    }

    /**
     * 占楼
     *
     * @param count 楼层数
     * @author bx002
     * @date 2021/1/28 14:03
     */
    private static void sendEmptyReplies(String cookie,int count) {
        for (int i = 0; i < count; i++) {
            postThread(cookie, "reply", null, 25237984L, null, null, "------------", null, null);
            try {
                Thread.sleep(18000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
    }

}
