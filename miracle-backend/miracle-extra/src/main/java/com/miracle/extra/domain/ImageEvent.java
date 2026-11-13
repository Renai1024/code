package com.miracle.extra.domain;
/** 新增：持久化事件与认领令牌；旧工作线程不得完成已被其他线程重新认领的事件。 */
public class ImageEvent {
    public String eventId;
    public String type;
    public Long imageId;
    public int retryCount;
    public String claimToken;
}
