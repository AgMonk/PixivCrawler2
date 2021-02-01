package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.gin.pixivcrawler.utils.StringUtils.isEmpty;

/**
 * nga论坛tag代码
 *
 * @author bx002
 * @date 2021/1/18 9:30
 */
@NoArgsConstructor
@Getter
@Setter
public class NgaBbsTag {
    StringBuilder text = new StringBuilder();
    String value;

    /**
     * 标签名称
     *
     * @return java.lang.String
     * @author bx002
     * @date 2021/1/18 9:31
     */
    public String getTagName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public void addText(String s) {
        text.append(s);
    }

    /**
     * 前缀
     *
     * @return 前缀
     */
    public String getPrefix() {
        return "";
    }

    /**
     * 后缀
     *
     * @return 后缀
     */
    public String getSuffix() {
        return "";
    }

    public NgaBbsTag(String text) {
        this.text = text == null ? new StringBuilder() : new StringBuilder(text);
    }

    public NgaBbsTag(String text, String value) {
        this.value = value;
        this.text = new StringBuilder(text);
    }

    public String getText() {
        return text.toString();
    }

    @Override
    public String toString() {
        String text = !isEmpty(getText()) ? getText() : this.text.toString();
        if (isEmpty(this.value)) {
            return String.format("%s[%s]%s[/%s]%s", getPrefix(), getTagName(), text, getTagName(), getSuffix());
        }
        return String.format("%s[%s=%s]%s[/%s]%s", getPrefix(), getTagName(), getValue(), text, getTagName(), getSuffix());
    }
}
