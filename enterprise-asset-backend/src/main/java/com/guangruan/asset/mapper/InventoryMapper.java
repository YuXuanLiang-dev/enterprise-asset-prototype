package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.InventoryItem;
import com.guangruan.asset.model.StatusStat;

@Mapper
public interface InventoryMapper {

    @Select("""
        SELECT id, status, status_text AS statusText, tag_status AS tagStatus, tag_status_text AS tagStatusText,
               code_fiscal AS codeFiscal, name_fiscal AS nameFiscal,
               category, spec, brand, location, acquisition_date AS acquisitionDate, original_value AS originalValue,
               net_value AS netValue, accumulated_depreciation AS accumulatedDepreciation
        FROM inventory_items
        WHERE enterprise_id = #{enterpriseId}
          AND (#{status} IS NULL OR #{status} = '' OR status = #{status})
          AND (#{tagStatus} IS NULL OR #{tagStatus} = '' OR tag_status = #{tagStatus})
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name_fiscal LIKE CONCAT('%', #{keyword}, '%')
               OR code_fiscal LIKE CONCAT('%', #{keyword}, '%') OR brand LIKE CONCAT('%', #{keyword}, '%')
               OR category LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<InventoryItem> findItems(@Param("enterpriseId") Long enterpriseId,
                                  @Param("status") String status,
                                  @Param("tagStatus") String tagStatus,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    @Select("""
        SELECT COUNT(1)
        FROM inventory_items
        WHERE enterprise_id = #{enterpriseId}
          AND (#{status} IS NULL OR #{status} = '' OR status = #{status})
          AND (#{tagStatus} IS NULL OR #{tagStatus} = '' OR tag_status = #{tagStatus})
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name_fiscal LIKE CONCAT('%', #{keyword}, '%')
               OR code_fiscal LIKE CONCAT('%', #{keyword}, '%') OR brand LIKE CONCAT('%', #{keyword}, '%')
               OR category LIKE CONCAT('%', #{keyword}, '%'))
        """)
    long countItems(@Param("enterpriseId") Long enterpriseId,
                    @Param("status") String status,
                    @Param("tagStatus") String tagStatus,
                    @Param("keyword") String keyword);

    @Select("""
        SELECT status, status_text AS statusText, COUNT(1) AS count
        FROM inventory_items
        WHERE enterprise_id = #{enterpriseId}
        GROUP BY status, status_text
        """)
    List<StatusStat> countByStatus(@Param("enterpriseId") Long enterpriseId);

    @Select("""
        SELECT tag_status AS status, tag_status_text AS statusText, COUNT(1) AS count
        FROM inventory_items
        WHERE enterprise_id = #{enterpriseId}
        GROUP BY tag_status, tag_status_text
        """)
    List<StatusStat> countByTagStatus(@Param("enterpriseId") Long enterpriseId);
}
