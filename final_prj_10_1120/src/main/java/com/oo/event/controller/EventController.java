package com.oo.event.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oo.event.model.dto.Event;
import com.oo.event.model.service.EventService;

@RestController
@RequestMapping("/api/event")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@GetMapping
    public ResponseEntity<?> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents(); // 모든 이벤트 데이터 반환
            return ResponseEntity.ok(events); // 성공적으로 데이터를 반환
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지와 HTTP 상태 코드 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이벤트 데이터를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
	
	
	
}
