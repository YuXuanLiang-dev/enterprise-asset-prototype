package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.Department;
import com.guangruan.asset.model.Personnel;

@Mapper
public interface DepartmentMapper {

    @Select("SELECT id, name, parent_id AS parentId FROM departments WHERE enterprise_id = #{enterpriseId} ORDER BY id ASC")
    List<Department> findAll(@Param("enterpriseId") Long enterpriseId);

    @Select("""
        SELECT p.id, p.name, p.dept_id AS deptId, p.is_leader AS isLeader
        FROM personnel p
        WHERE p.enterprise_id = #{enterpriseId}
          AND (#{keyword} IS NULL OR #{keyword} = '' OR p.name LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY p.id ASC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<Personnel> findPersonnel(@Param("enterpriseId") Long enterpriseId,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    @Select("""
        SELECT COUNT(1)
        FROM personnel p
        WHERE p.enterprise_id = #{enterpriseId}
          AND (#{keyword} IS NULL OR #{keyword} = '' OR p.name LIKE CONCAT('%', #{keyword}, '%'))
        """)
    long countPersonnel(@Param("enterpriseId") Long enterpriseId,
                        @Param("keyword") String keyword);
}
