package com.example.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.knowledge.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
} 