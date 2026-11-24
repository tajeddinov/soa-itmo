package com.itmo.soa.contract.error.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldError implements Serializable {

    public static final String PATH = "path";
    public static final String QUERY = "query";
    public static final String BODY = "body";

    private String location;
    private String field;
    private Object rejectedValue;
    private String message;

    public static FieldError path(String field, Object rejectedValue, String message) {
        return new FieldError(PATH, field, rejectedValue, message);
    }

    public static FieldError query(String field, Object rejectedValue, String message) {
        return new FieldError(QUERY, field, rejectedValue, message);
    }

    public static FieldError body(String field, Object rejectedValue, String message) {
        return new FieldError(BODY, field, rejectedValue, message);
    }
}
