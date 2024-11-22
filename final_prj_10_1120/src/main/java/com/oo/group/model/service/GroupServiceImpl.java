package com.oo.group.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oo.group.model.dao.GroupDao;
import com.oo.group.model.dto.Group;

@Service
public class GroupServiceImpl implements GroupService {
	private final GroupDao groupDao;

	public GroupServiceImpl(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	// 전체 게시글 조회
	@Override
	public List<Group> getGroupList() {
		return groupDao.selectAll();
	}

	// 상세 게시글 조회
	@Override
	public Group readGroup(int gBoardNo) {
		return groupDao.selectOne(gBoardNo);
	}
	
	// 게시글 등록
	@Override
	public void createGroup(Group group) {
		groupDao.insertGroup(group);
	}
	
	// 게시글 삭제
	@Override
	public void deleteGroup(int gBoardNo) {
		groupDao.removeGroup(gBoardNo);
	}

	// 게시글 수정
	@Override
	public void updateGroup(Group group) {
		groupDao.modifyGroup(group);
	}


}
