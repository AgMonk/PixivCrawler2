package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * nga返回对象
 *
 * @author bx002
 * @date 2021/1/14 11:13
 */
@SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
@Data
public class NgaResBase implements Serializable {
    @JSONField(alternateNames = "__MESSAGE")
    HashMap<String, String> message;
    @JSONField(alternateNames = "__ROWS")
    Long rows;
    @JSONField(alternateNames = "__R__ROWS_PAGE")
    Integer replyRowsPage;
}
