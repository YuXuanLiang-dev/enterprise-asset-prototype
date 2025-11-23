package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.Asset;
import com.guangruan.asset.model.CategoryStat;
import com.guangruan.asset.model.StatusStat;

@Mapper
public interface AssetMapper {

    @Select("""
        SELECT id, code, status, status_text AS statusText, name, category, spec, brand, location,
               price, purchase_date AS purchaseDate, original_value AS originalValue,
               acquisition_date AS acquisitionDate, accumulated_depreciation AS accumulatedDepreciation,
               posting_date AS postingDate, voucher_no AS voucherNo, depreciation_months AS depreciationMonths,
               remarks, use_dept AS useDept, user_name AS userName, manager_dept AS managerDept, manager_name AS managerName
        FROM assets
        WHERE enterprise_id = #{enterpriseId}
          AND (#{status} IS NULL OR #{status} = '' OR status = #{status})
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name LIKE CONCAT('%', #{keyword}, '%')
               OR code LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%')
               OR location LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<Asset> findAssets(@Param("enterpriseId") Long enterpriseId,
                           @Param("status") String status,
                           @Param("keyword") String keyword,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    @Select("""
        SELECT COUNT(1)
        FROM assets
        WHERE enterprise_id = #{enterpriseId}
          AND (#{status} IS NULL OR #{status} = '' OR status = #{status})
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name LIKE CONCAT('%', #{keyword}, '%')
               OR code LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%')
               OR location LIKE CONCAT('%', #{keyword}, '%'))
        """)
    long countAssets(@Param("enterpriseId") Long enterpriseId,
                     @Param("status") String status,
                     @Param("keyword") String keyword);

    @Select("""
        SELECT status, status_text AS statusText, COUNT(1) AS count
        FROM assets
        WHERE enterprise_id = #{enterpriseId}
        GROUP BY status, status_text
        """)
    List<StatusStat> countByStatus(@Param("enterpriseId") Long enterpriseId);

    @Select("""
        SELECT category, COUNT(1) AS count
        , COALESCE(SUM(original_value),0) AS totalValue
        FROM assets
        WHERE enterprise_id = #{enterpriseId}
        GROUP BY category
        ORDER BY count DESC
        """)
    List<CategoryStat> countByCategory(@Param("enterpriseId") Long enterpriseId);

    @Select("""
        SELECT status_text AS statusText, COALESCE(SUM(original_value),0) AS totalValue
        FROM assets
        WHERE enterprise_id = #{enterpriseId}
        GROUP BY status_text
        """)
    List<CategoryStat> sumValueByStatus(@Param("enterpriseId") Long enterpriseId);

    @Select("""
        SELECT location AS category, COALESCE(SUM(original_value),0) AS totalValue
        FROM assets
        WHERE enterprise_id = #{enterpriseId}
        GROUP BY location
        ORDER BY totalValue DESC
        LIMIT 10
        """)
    List<CategoryStat> sumValueByLocation(@Param("enterpriseId") Long enterpriseId);

    @org.apache.ibatis.annotations.Update("""
        UPDATE assets
        SET status = #{status}, status_text = #{statusText}, name = #{name}, category = #{category},
            spec = #{spec}, brand = #{brand}, location = #{location}, price = #{price},
            purchase_date = #{purchaseDate}, original_value = #{originalValue},
            acquisition_date = #{acquisitionDate}, accumulated_depreciation = #{accumulatedDepreciation},
            posting_date = #{postingDate}, voucher_no = #{voucherNo},
            depreciation_months = #{depreciationMonths}, remarks = #{remarks},
            use_dept = #{useDept}, user_name = #{userName}, manager_dept = #{managerDept}, manager_name = #{managerName}
        WHERE id = #{id} AND enterprise_id = #{enterpriseId}
        """)
    int updateAsset(Asset asset);

    @org.apache.ibatis.annotations.Insert("""
        INSERT INTO assets (enterprise_id, code, status, status_text, name, category, spec, brand, location,
                            price, purchase_date, original_value, acquisition_date, accumulated_depreciation,
                            posting_date, voucher_no, depreciation_months, remarks, use_dept, user_name, manager_dept, manager_name)
        VALUES (#{enterpriseId}, #{code}, #{status}, #{statusText}, #{name}, #{category}, #{spec}, #{brand}, #{location},
                #{price}, #{purchaseDate}, #{originalValue}, #{acquisitionDate}, #{accumulatedDepreciation},
                #{postingDate}, #{voucherNo}, #{depreciationMonths}, #{remarks}, #{useDept}, #{userName}, #{managerDept}, #{managerName})
        """)
    int insertAsset(Asset asset);

    @org.apache.ibatis.annotations.Delete("""
        <script>
        DELETE FROM assets WHERE enterprise_id = #{enterpriseId} AND id IN
        <foreach item='item' collection='ids' open='(' separator=',' close=')'>
            #{item}
        </foreach>
        </script>
        """)
    int deleteAssets(@Param("enterpriseId") Long enterpriseId, @Param("ids") List<Long> ids);
}
