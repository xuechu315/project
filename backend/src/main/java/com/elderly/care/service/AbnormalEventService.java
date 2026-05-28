// src/main/java/com/elderly/care/service/AbnormalEventService.java
package com.elderly.care.service;

import com.elderly.care.entity.AbnormalEvent;
import com.elderly.care.enums.Severity;
import com.elderly.care.mapper.AbnormalEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbnormalEventService {

    private final AbnormalEventMapper abnormalEventMapper;

    /**
     * 创建异常事件
     */
    public AbnormalEvent createAbnormalEvent(AbnormalEvent event) {
        log.info("创建异常事件: elderId={}, type={}, severity={}",
                event.getElderId(), event.getType(), event.getSeverity());
        event.setResolved(0);
        abnormalEventMapper.insert(event);
        return event;
    }

    /**
     * 获取未处理的紧急事件
     */
    public List<AbnormalEvent> getUnresolvedCriticalEvents() {
        return abnormalEventMapper.findUnresolvedCriticalEvents();
    }

    /**
     * 获取老人的异常事件列表
     */
    public List<AbnormalEvent> getEventsByElderId(Integer elderId) {
        return abnormalEventMapper.findByElderId(elderId);
    }

    /**
     * 标记事件已处理
     */
    public void resolveEvent(Integer eventId) {
        AbnormalEvent event = abnormalEventMapper.findById(eventId);
        if (event != null) {
            event.setResolved(1);
            abnormalEventMapper.update(event);
            log.info("事件已处理: eventId={}", eventId);
        }
    }
}