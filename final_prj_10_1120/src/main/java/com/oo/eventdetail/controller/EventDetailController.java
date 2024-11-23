package com.oo.eventdetail.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.oo.eventdetail.model.dto.EventDetail;
import com.oo.eventdetail.model.service.EventDetailService;

@RestController
@RequestMapping("/api/event-detail")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class EventDetailController {

    @Autowired
    private EventDetailService eventDetailService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventDetail(@PathVariable int id) {
        System.out.println("=====================================");
        System.out.println("이벤트 상세 API 호출됨");
        System.out.println("요청된 ID: " + id);
        
        try {
            EventDetail eventDetail = eventDetailService.getEventDetailById(id);
            
            if (eventDetail != null) {
                System.out.println("조회 성공!");
                System.out.println("데이터: " + eventDetail);
                return ResponseEntity.ok(eventDetail);
            } else {
                System.out.println("데이터 없음: ID = " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("에러 발생!");
            System.out.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
 // 관련 이벤트 포스터 조회 추가

    
    
}
