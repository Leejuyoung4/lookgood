package com.oo.groupcomment.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oo.groupcomment.model.dao.GroupCommentDao;
import com.oo.groupcomment.model.dto.GroupComment;

@Service
public class GroupCommentServiceImpl implements GroupCommentService {
	private final GroupCommentDao groupCommentDao;

	public GroupCommentServiceImpl(GroupCommentDao groupCommentDao) {
		this.groupCommentDao = groupCommentDao;
	}

	// 모임게시판 댓글 목록 조회
	@Override
	public List<GroupComment> getCommentsBygBoardNo(int gBoardNo) {
		return groupCommentDao.findBygBoardNo(gBoardNo);
	}

	// 댓글 등록
	@Override
	public void addComment(GroupComment groupComment) {
		groupCommentDao.insertComment(groupComment);
	}
	
	// 댓글 삭제
	@Override
	public boolean deleteComment(int gBoardCommentNo) {
		return groupCommentDao.removeComment(gBoardCommentNo);
	}

	// 댓글 수정
	@Override
	public void updateGroupComment(GroupComment groupComment) {
		groupCommentDao.modifyComment(groupComment);
	}

	@Override
	public boolean addLike(int gBoardCommentNo, int userNo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeLike(int commentNo, int userNo) {
		// TODO Auto-generated method stub
		return false;
	}



	
	
}
