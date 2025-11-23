package com.guangruan.asset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.guangruan.asset.model.AssetRecord;

@Mapper
public interface AssetRecordMapper {

    @Select("""
        SELECT id, enterprise_id AS enterpriseId, type, asset_code AS assetCode, DATE_FORMAT(action_time, '%Y-%m-%d %H:%i:%s') AS actionTime, operator
        FROM asset_records
        WHERE enterprise_id = #{enterpriseId}
          AND (#{type} IS NULL OR #{type} = '' OR type = #{type})
          AND (#{keyword} IS NULL OR #{keyword} = '' OR asset_code LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY action_time DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    List<AssetRecord> findRecords(@Param("enterpriseId") Long enterpriseId,
                                  @Param("type") String type,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    @Select("""
        SELECT COUNT(1)
        FROM asset_records
        WHERE enterprise_id = #{enterpriseId}
          AND (#{type} IS NULL OR #{type} = '' OR type = #{type})
          AND (#{keyword} IS NULL OR #{keyword} = '' OR asset_code LIKE CONCAT('%', #{keyword}, '%'))
        """)
    long countRecords(@Param("enterpriseId") Long enterpriseId,
                      @Param("type") String type,
                      @Param("keyword") String keyword);

    @org.apache.ibatis.annotations.Insert("""
        INSERT INTO asset_records (enterprise_id, type, asset_code, action_time, operator)
        VALUES (#{enterpriseId}, #{type}, #{assetCode}, #{actionTime}, #{operator})
        """)
    int insertRecord(AssetRecord record);
}
