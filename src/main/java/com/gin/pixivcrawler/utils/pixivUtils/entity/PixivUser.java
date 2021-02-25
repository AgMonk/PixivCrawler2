package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

import static com.gin.pixivcrawler.entity.ConstantValue.USERNAME_SUFFIX;

/**
 * Pixiv用户
 *
 * @author bx002
 * @date 2021/2/2 13:48
 */
@Data
@AllArgsConstructor
@TableName("t_pixiv_user")
public class PixivUser implements Serializable {
    Long id;
    String account;
    String name;

    public void setName(String name) {
        for (String suffix : USERNAME_SUFFIX) {
            if (name.contains(suffix)) {
                name = name.substring(0, name.indexOf(suffix));
            }
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PixivUser pixivUser = (PixivUser) o;

        if (!id.equals(pixivUser.id)) {
            return false;
        }
        if (!account.equals(pixivUser.account)) {
            return false;
        }
        return name.equals(pixivUser.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + account.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
