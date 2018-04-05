package com.tale.model.entity;

import com.blade.jdbc.annotation.Table;
import com.blade.jdbc.core.ActiveRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 附件
 * <p>
 * Created by biezhi on 2017/2/23.
 */
@Data
@Table("t_attach")
@EqualsAndHashCode(callSuper = false)
public class Attach extends ActiveRecord {

    private Integer id;
    private String fname;
    private String ftype;
    private String fkey;
    private Integer author_id;
    private Integer created;

}
