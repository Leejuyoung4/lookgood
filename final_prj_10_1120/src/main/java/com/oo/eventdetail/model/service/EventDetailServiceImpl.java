package com.oo.eventdetail.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oo.eventdetail.model.dao.EventDetailDao;
import com.oo.eventdetail.model.dto.EventDetail;

@Service
public class EventDetailServiceImpl implements EventDetailService {
    
    private final EventDetailDao eventDetailDao;
    
    @Autowired
    public EventDetailServiceImpl(EventDetailDao eventDetailDao) {
        this.eventDetailDao = eventDetailDao;
    }
    
    @Override
    public EventDetail getEventDetailById(int id) {
        try {
            EventDetail eventDetail = eventDetailDao.findEventDetailById(id);
            if (eventDetail == null) {
                throw new Exception("해당 ID의 이벤트를 찾을 수 없습니다: " + id);
            }
            return eventDetail;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이벤트 상세 정보를 가져오는데 실패했습니다: " + e.getMessage());
        }
    }
    
}