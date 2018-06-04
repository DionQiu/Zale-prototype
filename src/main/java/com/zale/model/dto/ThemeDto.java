package com.zale.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by biezhi on 2017/9/15.
 */
@Data
public class ThemeDto implements Serializable {

    /**
     * 主题名称
     */
    private String name;

    /**
     * 是否有设置项
     */
    private boolean hasSetting;

    public ThemeDto(String name) {
        this.name = name;
    }

}
