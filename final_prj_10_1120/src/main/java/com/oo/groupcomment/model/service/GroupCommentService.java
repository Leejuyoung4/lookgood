package com.oo.groupcomment.model.service;

import java.util.List;

import com.oo.groupcomment.model.dto.GroupComment;

public interface GroupCommentService {

	// 모임게시판 댓글 목록 조회
	public List<GroupComment> getCommentsBygBoardNo(int gBoardNo);
	
	// 댓글 등록
	public void addComment(GroupComment groupComment);
	
	// 댓글 삭제
	public boolean deleteComment(int gBoardCommentNo);

	// 댓글 수정
	public void updateGroupComment(GroupComment groupComment);

	// 댓글 좋아요
	public boolean addLike(int gBoardCommentNo, int userNo);
	
	// 댓글 좋아요 취소
	boolean removeLike(int commentNo, int userNo);
	



	
}
