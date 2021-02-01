package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Uid extends NgaBbsTag {
    public Uid(String text) {
        super(text);
    }

    public Uid(String text, String value) {
        super(text, value);
    }
}
