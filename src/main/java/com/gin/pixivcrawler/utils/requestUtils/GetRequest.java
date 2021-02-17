package com.gin.pixivcrawler.utils.requestUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * get方法
 *
 * @author bx002
 * @date 2020/12/2 10:37
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class GetRequest implements RequestBase<GetRequest> {
    private HttpGet method;
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> params = new HashMap<>();
    private MultipartEntityBuilder entityBuilder = null;
    private String decodeEnc = StandardCharsets.UTF_8.toString();
    private String encodeEnc = StandardCharsets.UTF_8.toString();

    private String proxyHost = null;
    private int proxyPort = 10809;


    public static GetRequest create() {
        return new GetRequest();
    }

    public String get(String url) {
        try {
            method = createMethod(HttpGet.class, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) execute();
    }
}
