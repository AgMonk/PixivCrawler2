package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Tid extends NgaBbsTag {

    public Tid(String text) {
        super(text);
    }

    public Tid(String text, String value) {
        super(text, value);
    }
}
