package com.miracle.extra.service;
import com.miracle.extra.domain.TycoPacking.*;
import java.util.List;

/** 新增：内外箱装箱服务接口，所有写入均由服务端校验并在同库事务中完成。 */
public interface ITycoPackingService {
    Session execute(String action, Command command);
    Session current(String kind);
    List<Session> list(String kind);
    Session detail(String id);
    void expireDrafts();
}
