package com.specsense.mapper;

import com.specsense.model.entity.CompanyInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CompanyInfoMapper {

    @Select("SELECT * FROM company_info WHERE id = 1")
    CompanyInfo find();

    @Update("UPDATE company_info SET image_url = #{imageUrl}, about_en = #{aboutEn}, about_zh = #{aboutZh}, " +
            "description_en = #{descriptionEn}, description_zh = #{descriptionZh}, " +
            "mission_en = #{missionEn}, mission_zh = #{missionZh}, " +
            "email = #{email}, phone = #{phone}, address_en = #{addressEn}, address_zh = #{addressZh}, " +
            "values_json = #{valuesJson}, timeline_json = #{timelineJson}, " +
            "qualifications_json = #{qualificationsJson}, partners_stats_json = #{partnersStatsJson}, " +
            "stats_json = #{statsJson} WHERE id = 1")
    int update(CompanyInfo companyInfo);
}
