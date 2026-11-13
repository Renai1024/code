package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesApiDataRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE6)
@Mapper
public interface MesApiDataRecordMapper {
    List<MesApiDataRecord> selectMesApiDataRecordList(MesApiDataRecord record);
    
    List<MesApiDataRecord.MesApiDataBarCodeDetail> selectBarCodeListByDataTrackId(String dataTrackId);
    
    List<MesApiDataRecord.MesApiDataMaterialBatchDetail> selectMaterialBatchListByDataTrackId(String dataTrackId);
    
    List<MesApiDataRecord.MesApiDataMaterialBarCodeDetail> selectMaterialBarCodeListByDataTrackId(String dataTrackId);
    
    List<MesApiDataRecord.MesApiDataInfoDetail> selectDataInfoListByDataTrackId(String dataTrackId);
}