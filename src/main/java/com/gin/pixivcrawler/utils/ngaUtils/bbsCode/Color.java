package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bx002
 * @date 2021/1/18 11:17
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter

public class Color extends NgaBbsTag {
    public static final String NEW = red("New");

    public static String red(String text) {
        return new Color(text, "red").toString();
    }

    public Color() {
    }

    public Color(String text) {
        super(text);
    }

    public Color(String text, String value) {
        super(text, value);
    }
}
