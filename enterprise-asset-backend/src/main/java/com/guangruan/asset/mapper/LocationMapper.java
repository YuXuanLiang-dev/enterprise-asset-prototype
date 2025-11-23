package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.LocationNode;

@Mapper
public interface LocationMapper {

    @Select("""
        SELECT id, name, parent_id AS parentId, level
        FROM locations
        WHERE enterprise_id = #{enterpriseId}
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY level ASC, id ASC
        """)
    List<LocationNode> findAll(@Param("enterpriseId") Long enterpriseId,
                               @Param("keyword") String keyword);
}
