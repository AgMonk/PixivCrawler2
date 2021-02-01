package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * quote
 *
 * @author bx002
 * @date 2021/1/16 16:00
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Quote extends NgaBbsTag {
    public Quote(String text) {
        super(text);
    }

    public Quote(String text, String value) {
        super(text, null);
    }

    @Override
    public String getText() {
        return super.getText() + "\n";
    }
}
