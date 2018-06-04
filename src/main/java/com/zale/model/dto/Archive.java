package com.zale.model.dto;

import com.blade.jdbc.core.ActiveRecord;
import com.zale.model.entity.Contents;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 文章归档
 * <p>
 * Created by biezhi on 2017/9/23.
 */
@Data
public class Archive extends ActiveRecord {

    private String         date_str;
    private Date           date;
    private String         count;
    private List<Contents> articles;

}
