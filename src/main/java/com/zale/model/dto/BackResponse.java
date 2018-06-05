package com.zale.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by qyw on 2017/9/25.
 */
@Data
public class BackResponse implements Serializable {

    private String attach_path;
    private String theme_path;
    private String sql_path;

}
