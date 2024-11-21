package com.oo.eventdetail.model.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.oo.eventdetail.model.dto.EventDetail;

@Mapper
public interface EventDetailDao {
	 EventDetail findEventDetailById(int id);


}
