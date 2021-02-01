package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * url
 *
 * @author bx002
 * @date 2021/1/16 15:42
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Url extends NgaBbsTag {
    public Url(String text) {
        super(text);
    }

    public Url(String text, String value) {
        super(text, value);
    }
}
