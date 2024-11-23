package com.oo.group.model.dao;

import java.util.List;

import com.oo.group.model.dto.Group;

public interface GroupDao {

	// 전체 게시글 조회
	public List<Group> selectAll();

	// 상세 게시글 조회
	public Group selectOne(int gBoardNo);

	// 게시글 등록
	public void insertGroup(Group group);

	// 게시글 삭제
	public void removeGroup(int gBoardNo);

	// 게시글 수정
	public void modifyGroup(Group group);

	// 게시글 검색
	public List<Group> searchPosts(String keyword);

	// 조회수 증가
	public void incrementViewCount(int gBoardNo);

	// 좋아요수 증가
	public void incrementLikeCount(int gBoardNo);

	public boolean isUserLiked(int gBoardNo, int userNo);

	public void deleteLike(int gBoardNo, int userNo);

	public void addLike(int gBoardNo, int userNo);


}
