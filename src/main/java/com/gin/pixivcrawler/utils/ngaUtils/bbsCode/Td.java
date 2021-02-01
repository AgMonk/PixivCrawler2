package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * td
 *
 * @author bx002
 * @date 2021/1/16 15:37
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Td extends NgaBbsTag {
    public Td(String text) {
        super(text);
    }

    public Td(String text, String value) {
        super(text, value);
    }
}
