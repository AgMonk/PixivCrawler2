package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * tr
 *
 * @author bx002
 * @date 2021/1/16 15:39
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Tr extends NgaBbsTag {
    @Override
    public String getPrefix() {
        return "\n";
    }

    public Tr() {
    }

    public Tr(String text) {
        super(text);
    }

    public Tr(String text, String value) {
        super(text, value);
    }

}
