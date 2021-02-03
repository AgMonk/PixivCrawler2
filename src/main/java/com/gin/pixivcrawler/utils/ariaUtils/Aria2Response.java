package com.gin.pixivcrawler.utils.ariaUtils;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 响应对象
 *
 * @author bx002
 * @date 2021/2/3 15:57
 */
@Data
public class Aria2Response implements Serializable {
    String id;
    String jsonrpc;
    HashMap<String, String> error;
    List<Aria2Quest> result;
}
