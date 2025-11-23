package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.Category;
import com.guangruan.asset.model.ParamItem;

@Mapper
public interface SettingMapper {

    @Select("""
        SELECT c.id, c.name, c.is_required AS isRequired,
               (SELECT COUNT(1) FROM category_params cp WHERE cp.category_id = c.id AND cp.enterprise_id = c.enterprise_id) AS paramCount
        FROM categories c
        WHERE c.enterprise_id = #{enterpriseId}
          AND (#{keyword} IS NULL OR #{keyword} = '' OR c.name LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY c.id ASC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<Category> findCategories(@Param("enterpriseId") Long enterpriseId,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    @Select("""
        SELECT COUNT(1)
        FROM categories c
        WHERE c.enterprise_id = #{enterpriseId}
          AND (#{keyword} IS NULL OR #{keyword} = '' OR c.name LIKE CONCAT('%', #{keyword}, '%'))
        """)
    long countCategories(@Param("enterpriseId") Long enterpriseId,
                         @Param("keyword") String keyword);

    @Select("""
        SELECT id, name, type,
               CASE type
                   WHEN 'text' THEN '文本'
                   WHEN 'select' THEN '选项项'
                   WHEN 'date' THEN '日期'
                   ELSE type
               END AS typeText
        FROM params
        WHERE enterprise_id = #{enterpriseId}
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY id ASC
        """)
    List<ParamItem> findParams(@Param("enterpriseId") Long enterpriseId,
                               @Param("keyword") String keyword);
}
