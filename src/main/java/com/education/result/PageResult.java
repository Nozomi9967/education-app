package com.education.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "分页结果类")
public class PageResult<E> implements Serializable {
    private Long cursor;
    private List<E> records;
}
