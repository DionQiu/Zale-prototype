package com.zale.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by qyw on 2017/9/1.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginMenu {

    private String name;
    private String slug;
    private String icon;

}
