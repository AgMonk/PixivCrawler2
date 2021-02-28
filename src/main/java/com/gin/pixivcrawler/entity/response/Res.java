package com.gin.pixivcrawler.entity.response;

import lombok.Data;

/**
 * 标准响应对象
 */
@Data
public class Res<T> {
    int code;
    String message;
    T data;


    public Res() {
    }

    public Res(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Res(String message, T data) {
        this.code = 65535;
        this.message = message;
        this.data = data;
    }

    public Res(String message) {
        this.code = 65535;
        this.message = message;
    }

    public Res(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> Res<T> success(T data) {
        return new Res<>(2000, "成功", data);
    }

    public static Res<Void> success() {
        return success(null);
    }

    public static Res<Void> success(String message) {
        return new Res<>(2000, message, null);
    }

}
