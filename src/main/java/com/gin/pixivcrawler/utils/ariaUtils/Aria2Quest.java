package com.gin.pixivcrawler.utils.ariaUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Aria2任务
 *
 * @author bx002
 * @date 2021/2/3 16:07
 */
@Data
public class Aria2Quest implements Serializable {
    String gid;
    Long completedLength;
    Long totalLength;
    String status;
    Integer errorCode;
    List<Aria2File> files;

    @JSONField(serialize = false)
    @JsonIgnore
    public boolean isCompleted() {
        return errorCode == 0 && completedLength.equals(totalLength) && "complete".equals(status);
    }
}
