package com.zale.model.entity;

import com.blade.jdbc.annotation.Table;
import com.blade.jdbc.core.ActiveRecord;
import com.blade.kit.DateKit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 日志记录
 *
 * @author qyw
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("t_logs")
@EqualsAndHashCode(callSuper = false)
public class Logs extends ActiveRecord {

    // 项目主键
    private Integer id;

    // 产生的动作
    private String action;

    // 产生的数据
    private String data;

    // 发生人id
    private Integer author_id;

    // 日志产生的ip
    private String ip;

    // 日志创建时间
    private Integer created;

    public Logs(String action, String data, String ip, Integer uid) {
        this.action = action;
        this.data = data;
        this.ip = ip;
        this.author_id = uid;
        this.created = DateKit.nowUnix();
    }

}