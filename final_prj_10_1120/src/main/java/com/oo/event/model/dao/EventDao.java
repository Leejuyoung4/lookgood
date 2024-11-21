package com.oo.event.model.dao;

import java.util.List;

import com.oo.event.model.dto.Event;


public interface EventDao {

	public List<Event> findAll();

}
