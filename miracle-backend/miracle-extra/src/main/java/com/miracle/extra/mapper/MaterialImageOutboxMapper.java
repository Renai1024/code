package com.miracle.extra.mapper;
import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.ImageEvent;
import org.apache.ibatis.annotations.*;
/** 新增：Material 业务库 outbox；单条条件 UPDATE 原子认领，兼容 MySQL 5.7/8。 */
@Mapper @DataSource(DataSourceType.SLAVE2)
public interface MaterialImageOutboxMapper {
    @Insert("insert into image_event_outbox(event_id,event_type,image_id,status,retry_count,created_at,next_retry_at) values(NEWID(),#{type},#{id},'NEW',0,SYSUTCDATETIME(),SYSUTCDATETIME())")
    int insertEvent(@Param("type") String type,@Param("id") Long id);
    @Update(";WITH due AS (SELECT TOP (1) * FROM image_event_outbox WITH (UPDLOCK, READPAST, ROWLOCK) WHERE status IN ('NEW','RETRY') AND next_retry_at <= SYSUTCDATETIME() ORDER BY created_at,event_id) UPDATE due SET status='PROCESSING',claimed_at=SYSUTCDATETIME(),claim_token=#{token}") int claim(@Param("token") String token);
    @Select("select event_id as eventId,event_type as type,image_id as imageId,retry_count as retryCount,claim_token as claimToken from image_event_outbox where claim_token=#{token} and status='PROCESSING'")
    ImageEvent claimed(@Param("token") String token);
    @Update("update image_event_outbox set status='DONE',claimed_at=null,claim_token=null where event_id=#{id} and status='PROCESSING' and claim_token=#{token}")
    int complete(@Param("id") String id,@Param("token") String token);
    @Update("update image_event_outbox set status='RETRY',claimed_at=null,claim_token=null,retry_count=retry_count+1,next_retry_at=DATEADD(SECOND, #{delay}, SYSUTCDATETIME()),last_error=#{reason} where event_id=#{id} and status='PROCESSING' and claim_token=#{token}")
    int retry(@Param("id") String id,@Param("token") String token,@Param("delay") int delay,@Param("reason") String reason);
    @Update("update image_event_outbox set status='RETRY',claimed_at=null,claim_token=null,next_retry_at=SYSUTCDATETIME() where status='PROCESSING' and claimed_at < DATEADD(MINUTE, -10, SYSUTCDATETIME())")
    int recoverExpiredClaims();
    @Select("select count(1) from image_event_outbox where image_id=#{id} and event_type='CONFIRM' and status <> 'DONE'")
    int pendingConfirm(@Param("id") Long id);
}
