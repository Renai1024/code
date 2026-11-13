package com.miracle.extra.service.impl
import com.miracle.extra.mapper.OrderImageOutboxMapper
import com.miracle.extra.domain.ImageEvent
import org.springframework.stereotype.Service
/** 新增：事件写入沿用业务事务；工作线程认领、完成与退避各自持久化。 */
@Service
class OrderImageOutbox(private val mapper: OrderImageOutboxMapper) {
    fun enqueueConfirm(id: Long) { check(mapper.insertEvent("CONFIRM", id) == 1) }
    fun enqueueDeleteIfUnused(id: Long) { check(mapper.insertEvent("DELETE_IF_UNUSED", id) == 1) }
    fun claimNext(): ImageEvent? {
        val token = java.util.UUID.randomUUID().toString()
        return if (mapper.claim(token) == 1) mapper.claimed(token) else null
    }
    fun complete(event: ImageEvent) { mapper.complete(event.eventId, event.claimToken) }
    fun retry(event: ImageEvent, reason: String) {
        val delay = minOf(3600, 5 * (1 shl minOf(event.retryCount, 10)))
        mapper.retry(event.eventId, event.claimToken, delay, reason.take(1000))
    }
    fun recoverExpiredClaims() { mapper.recoverExpiredClaims() }
    fun pendingConfirm(id: Long) = mapper.pendingConfirm(id) > 0
}
