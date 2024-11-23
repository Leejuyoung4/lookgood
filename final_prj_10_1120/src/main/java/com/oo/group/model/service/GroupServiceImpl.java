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

	// 게시글 검색
	@Override
	public List<Group> searchPosts(String keyword) {
		return groupDao.searchPosts(keyword);
	}

	// 조회수 증가
	@Override
	public void incrementViewCount(int gBoardNo) {
		groupDao.incrementViewCount(gBoardNo);
	}

	// 좋아요수 증가
	@Override
	public boolean toggleLike(int gBoardNo, int userNo) {
	    boolean isLiked = groupDao.isUserLiked(gBoardNo, userNo);

	    if (isLiked) {
	        groupDao.deleteLike(gBoardNo, userNo); // 좋아요 취소
	    } else {
	        groupDao.addLike(gBoardNo, userNo); // 좋아요 추가
	    }

	    return !isLiked; // 좋아요 상태 반환
	}

	@Override
	public boolean isUserLiked(int gBoardNo, int userNo) {
		return groupDao.isUserLiked(gBoardNo, userNo);
	}
	
	

}
