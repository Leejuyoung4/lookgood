package com.oo.groupcomment.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oo.groupcomment.model.dto.GroupComment;

@Mapper
public interface GroupCommentDao {

	// 모임게시판 댓글 목록 조회
	public List<GroupComment> findBygBoardNo(@Param("gBoardNo") int gBoardNo);

	// 댓글 삭제
	public boolean removeComment(int gBoardCommentNo);

	// 댓글 수정
	public void modifyComment(GroupComment groupComment);


}
