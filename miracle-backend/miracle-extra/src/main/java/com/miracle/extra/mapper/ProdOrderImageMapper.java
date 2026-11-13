package com.miracle.extra.mapper;
import org.apache.ibatis.annotations.*;
import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import java.util.List;
import com.miracle.extra.domain.ProdOrderlist;
/** 新增：工单图片关联在 MySQL 主库，与业务和 outbox 共享本地事务。 */
@Mapper @DataSource(DataSourceType.MASTER)
public interface ProdOrderImageMapper {
    @Select("select image_id from prod_order_image where production_order_no=#{no} order by sort_order")
    List<Long> selectImageIds(@Param("no") String no);
    @Select("<script>select production_order_no as productionOrderNo, image_id as image from prod_order_image where production_order_no in <foreach collection='nos' item='no' open='(' separator=',' close=')'>#{no}</foreach> order by production_order_no,sort_order</script>")
    List<ProdOrderlist> selectForOrders(@Param("nos") List<String> nos);
    @Delete("delete from prod_order_image where production_order_no=#{no}") int deleteByOrderNo(@Param("no") String no);
    @Insert("insert into prod_order_image(production_order_no,image_id,sort_order) values(#{no},#{id},#{sort})")
    int insert(@Param("no") String no,@Param("id") Long id,@Param("sort") int sort);
    @Select("select count(1) from prod_order_image where image_id=#{id}") int countImageReferences(@Param("id") Long id);
    /** 新增：迁移期间保守保护旧 URL 列中的引用，未导入关联的历史工单也不能误删。 */
    @Select("select count(1) from ods_v_mes_prododertobbpt_list where FIND_IN_SET(#{url}, `图片`) > 0")
    int countLegacyImageReferences(@Param("url") String url);
    /** 锁定业务行，串行化同一工单的图片替换。 */
    @Select("select `生产工单号` from ods_v_mes_prododertobbpt_list where `生产工单号`=#{no} for update")
    String lockOrder(@Param("no") String no);
}
