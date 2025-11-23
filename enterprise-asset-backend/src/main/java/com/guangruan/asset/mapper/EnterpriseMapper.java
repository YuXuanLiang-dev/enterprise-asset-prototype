package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.Enterprise;

@Mapper
public interface EnterpriseMapper {

    @Select("""
        SELECT e.id, e.code, e.name
        FROM enterprises e
        JOIN user_enterprises ue ON ue.enterprise_id = e.id
        WHERE ue.user_id = #{userId}
        ORDER BY e.id ASC
        """)
    List<Enterprise> findByUserId(Long userId);
}
