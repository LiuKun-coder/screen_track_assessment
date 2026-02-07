package org.example.sc_backend.common.utils;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 统一响应体
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private String timestamp;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        r.setTimestamp(LocalDateTime.now().toString());
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setTimestamp(LocalDateTime.now().toString());
        return r;
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }
}
