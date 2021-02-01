package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 单个帖子数据
 *
 * @author bx002
 * @date 2021/1/14 11:13
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
@Data
public class NgaResThread extends NgaResBase implements Serializable {
    @JSONField(alternateNames = "__PAGE")
    Integer page;
    @JSONField(alternateNames = "__R__ROWS_PAGE")
    Integer replyRowsPage;
    @JSONField(alternateNames = "__T")
    NgaThread thread;
    @JSONField(alternateNames = "__R")
    List<Reply> replies;
    @JSONField(alternateNames = "__U")
    HashMap<String, NgaUser> users;


//    public List<Thread> getThreads() {
//        return threads != null ? threads : new ArrayList<>(threadsMap.values());
//    }
}
