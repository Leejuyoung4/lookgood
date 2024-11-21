package com.oo.group.model.service;

import java.util.List;

import com.oo.group.model.dto.Group;

public interface GroupService {
	// 게시글 전체 조회
	public List<Group> getGroupList();

	// 게시글 상세 조회
	public Group readGroup(int gBoardNo);

	// 게시글 삭제
	public void deleteGroup(int gBoardNo);

	// 게시글 수정
	public void updateGroup(Group group);

}
