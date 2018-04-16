package com.zale.model.entity;

import com.blade.jdbc.annotation.Table;
import com.blade.jdbc.core.ActiveRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配置选项
 *
 * @author biezhi
 */
@Data
@Table(value = "t_options", pk = "name")
@EqualsAndHashCode(callSuper = false)
public class Options extends ActiveRecord {

    // 配置名称
    private String name;

    // 配置值
    private String value;

    // 配置描述
    private String description;

}