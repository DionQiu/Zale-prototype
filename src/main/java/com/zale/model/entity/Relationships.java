package com.zale.model.entity;

import com.blade.jdbc.annotation.Table;
import com.blade.jdbc.core.ActiveRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据关系
 *
 * @author biezhi
 */
@Data
@Table(value = "t_relationships", pk = "mid")
@EqualsAndHashCode(callSuper = false)
public class Relationships extends ActiveRecord {

    // 内容主键
    private Integer cid;

    // 项目主键
    private Integer mid;

}