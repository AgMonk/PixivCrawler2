package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.Getter;
import lombok.Setter;

/**
 * 对齐
 *
 * @author bx002
 */
@Getter
@Setter
public class Align extends NgaBbsTag {

    public Align(String text) {
        super(text);
    }

    public Align(String text, String value) {
        super(text, value);
    }
}
