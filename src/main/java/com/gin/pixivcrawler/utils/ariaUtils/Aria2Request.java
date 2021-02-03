package com.gin.pixivcrawler.utils.ariaUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gin.pixivcrawler.utils.JsonUtil;
import com.gin.pixivcrawler.utils.requestUtils.PostRequest;
import lombok.Data;
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

    public Aria2Response send() {
        return JSONObject.parseObject((String) PostRequest.create()
                .addContentType(ContentType.APPLICATION_JSON)
                .setStringEntity(JSONObject.toJSONString(this))
                .post(rpcUrl), Aria2Response.class);
    }

    public static Aria2Request create(String id, Aria2Method method) {
        return new Aria2Request(id, method);
    }

    public static Aria2Response tellActive() {
        return create(null, Aria2Method.TELL_ACTIVE).addParam(PARAM_ARRAY_OF_FILED).send();
    }

    public static Aria2Response tellStopped() {
        return create(null, Aria2Method.TELL_STOPPED).addParam(-1).addParam(1000).addParam(PARAM_ARRAY_OF_FILED).send();
    }


    public static void main(String[] args) {

        JsonUtil.printJson(tellStopped());
    }


}
