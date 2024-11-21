package com.oo.event.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oo.event.model.dao.EventDao;
import com.oo.event.model.dto.Event;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired
	private EventDao eventDao;

	@Override
    public List<Event> getAllEvents() {
        return eventDao.findAll();
    }
}
