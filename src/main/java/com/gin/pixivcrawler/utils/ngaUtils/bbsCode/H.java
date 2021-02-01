package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.Getter;
import lombok.Setter;

/**
 * h标题
 *
 * @author bx002
 * @date 2021/1/16 16:01
 */

@Getter
@Setter
public class H extends NgaBbsTag {

    public H(String text) {
        super(text);
    }

    public H(String text, String value) {
        super(text, value);
    }
}
