package com.gin.pixivcrawler.utils.ariaUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gin.pixivcrawler.utils.JsonUtil;
import com.gin.pixivcrawler.utils.StringUtils;
import com.gin.pixivcrawler.utils.requestUtils.PostRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aria2Json对象
 *
 * @author bx002
 * @date 2021/2/3 15:28
 */
@Data
@Slf4j
public class Aria2Request {
    private final static String[] PARAM_ARRAY_OF_FILED = new String[]{"totalLength", "completedLength", "files", "status", "errorCode", "gid"};

    @JsonInclude
    @JSONField(serialize = false)
    String rpcUrl = "http://localhost:6800/jsonrpc";

    @JSONField(serializeUsing = Aria2MethodSerializer.class)
    Aria2Method method;

    String id;
    String jsonrpc = "2.0";
    List<Object> params = new ArrayList<>();

    public Aria2Request addParam(Object obj) {
        params.add(obj);
        return this;
    }

    public Aria2Request(String id, Aria2Method method) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.method = method;
    }

    public <T extends Aria2Response> T send(Class<T> clazz) {
        String obj = JSONObject.toJSONString(this);
//        JsonUtil.printJson(obj);

        return JSONObject.parseObject((String) PostRequest.create()
                .addContentType(ContentType.APPLICATION_JSON)
                .setStringEntity(obj)
                .post(rpcUrl), clazz);
    }

    public static Aria2Request create(String id, Aria2Method method) {
        return new Aria2Request(id, method);
    }

    public static Aria2ResponseMessage addUri(String url, Aria2UriOption option) {
        if (StringUtils.isEmpty(option.getFileName())) {
            option.setFileName(url.substring(url.lastIndexOf("/") + 1));
        }
        Aria2ResponseMessage message = create(null, Aria2Method.ADD_URI)
                .addParam(new String[]{url})
                .addParam(option)
                .send(Aria2ResponseMessage.class);
        if (message.getError() == null) {
            log.debug("成功添加任务 {}", option.getFileName());
        }
        return message;
    }

    public static Aria2ResponseQuest tellActive() {
        return create(null, Aria2Method.TELL_ACTIVE).addParam(PARAM_ARRAY_OF_FILED).send(Aria2ResponseQuest.class);
    }

    public static Aria2ResponseQuest tellStopped() {
        return create(null, Aria2Method.TELL_STOPPED).addParam(-1).addParam(1000).addParam(PARAM_ARRAY_OF_FILED).send(Aria2ResponseQuest.class);
    }

    public static Aria2ResponseQuest tellWaiting() {
        return create(null, Aria2Method.TELL_WAITING).addParam(0).addParam(1000).addParam(PARAM_ARRAY_OF_FILED).send(Aria2ResponseQuest.class);
    }

    public static Aria2ResponseMessage removeDownloadResult(String gid) {
        return create(null, Aria2Method.REMOVE_DOWNLOAD_RESULT).addParam(gid).send(Aria2ResponseMessage.class);
    }


    public static void main(String[] args) {
//        Aria2UriOption option = new Aria2UriOption();
//        option.setDir("D:/").setFileName(null).setHttpsProxy("http://127.0.0.1:10809/");
//        JsonUtil.printJson(option);
//        String uri = "https://i.pximg.net/img-original/img/2021/02/03/10/09/40/87499432_p1.png";
//        Aria2ResponseMessage responseMessage = addUri(uri, option);
//        JsonUtil.printJson(responseMessage);

        JsonUtil.printJson(tellStopped());
    }


}
