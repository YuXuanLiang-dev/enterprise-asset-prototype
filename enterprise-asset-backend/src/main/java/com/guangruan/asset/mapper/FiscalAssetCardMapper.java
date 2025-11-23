package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.FiscalAssetCard;

@Mapper
public interface FiscalAssetCardMapper {

    @Select("""
        SELECT id, code, name, category, spec, brand, quantity, use_dept AS useDept, user_name AS userName,
               manager_dept AS managerDept, manager_name AS managerName, original_value AS originalValue,
               acquisition_date AS acquisitionDate, accumulated_depreciation AS accumulatedDepreciation,
               posting_date AS postingDate, voucher_no AS voucherNo, depreciation_months AS depreciationMonths,
               remarks
        FROM fiscal_asset_cards
        WHERE enterprise_id = #{enterpriseId}
          AND (#{category} IS NULL OR #{category} = '' OR category LIKE CONCAT('%', #{category}, '%'))
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name LIKE CONCAT('%', #{keyword}, '%')
               OR code LIKE CONCAT('%', #{keyword}, '%') OR spec LIKE CONCAT('%', #{keyword}, '%')
               OR brand LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<FiscalAssetCard> findCards(@Param("enterpriseId") Long enterpriseId,
                                    @Param("category") String category,
                                    @Param("keyword") String keyword,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    @Select("""
        SELECT COUNT(1)
        FROM fiscal_asset_cards
        WHERE enterprise_id = #{enterpriseId}
          AND (#{category} IS NULL OR #{category} = '' OR category LIKE CONCAT('%', #{category}, '%'))
          AND (#{keyword} IS NULL OR #{keyword} = '' OR name LIKE CONCAT('%', #{keyword}, '%')
               OR code LIKE CONCAT('%', #{keyword}, '%') OR spec LIKE CONCAT('%', #{keyword}, '%')
               OR brand LIKE CONCAT('%', #{keyword}, '%'))
        """)
    long countCards(@Param("enterpriseId") Long enterpriseId,
                    @Param("category") String category,
                    @Param("keyword") String keyword);

    @org.apache.ibatis.annotations.Update("""
        UPDATE fiscal_asset_cards
        SET name = #{name}, category = #{category}, spec = #{spec}, brand = #{brand},
            quantity = #{quantity}, use_dept = #{useDept}, user_name = #{userName},
            manager_dept = #{managerDept}, manager_name = #{managerName},
            original_value = #{originalValue}, acquisition_date = #{acquisitionDate},
            accumulated_depreciation = #{accumulatedDepreciation},
            posting_date = #{postingDate}, voucher_no = #{voucherNo},
            depreciation_months = #{depreciationMonths}, remarks = #{remarks}
        WHERE id = #{id} AND enterprise_id = #{enterpriseId}
        """)
    int updateCard(FiscalAssetCard card);

    @org.apache.ibatis.annotations.Delete("""
        <script>
        DELETE FROM fiscal_asset_cards WHERE enterprise_id = #{enterpriseId} AND id IN
        <foreach item='item' collection='ids' open='(' separator=',' close=')'>
            #{item}
        </foreach>
        </script>
        """)
    int deleteCards(@Param("enterpriseId") Long enterpriseId, @Param("ids") List<Long> ids);
}
