package com.github.liaomengge.service.base_framework.mysql.extend.special;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * Created by liaomengge on 2019/11/19.
 */
@RegisterMapper
public interface InsertListSelectiveMapper<T> {

    /**
     * 批量插入, 使用默认值
     *
     * @param recordList
     */
    @Options(useGeneratedKeys = true)
    @InsertProvider(type = InsertListSelectiveProvider.class, method = "dynamicSQL")
    int insertListSelective(List<? extends T> recordList);
}
