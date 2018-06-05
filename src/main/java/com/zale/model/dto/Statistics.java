package com.zale.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 后台统计对象
 * <p>
 * Created by qyw on 2017/9/24.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistics implements Serializable {

    // 文章数
    private long articles;
    // 页面数
    private long pages;
    // 评论数
    private long comments;
    // 分类数
    private long categories;
    // 标签数
    private long tags;
    // 附件数
    private long attachs;

}
