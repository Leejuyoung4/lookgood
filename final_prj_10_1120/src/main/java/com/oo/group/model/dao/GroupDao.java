package com.oo.group.model.dao;

import java.util.List;
import com.oo.group.model.dto.Group;

public interface GroupDao {
    // 전체 게시글 조회
    public List<Group> selectAll();

    // 상세 게시글 조회
    public Group selectOne(int boardNo);  // gBoardNo -> boardNo

    // 게시글 등록
    public void insertGroup(Group group);

    // 게시글 삭제
    public void removeGroup(int boardNo);  // gBoardNo -> boardNo

    // 게시글 수정
    public void modifyGroup(Group group);

    // 게시글 검색
    public List<Group> searchPosts(String keyword);

    // 조회수 증가
    public void incrementViewCount(int boardNo);  // gBoardNo -> boardNo

    // 좋아요 관련
    public void incrementLikeCount(int boardNo);  // gBoardNo -> boardNo
    public boolean isUserLiked(int boardNo, int userNo);  // gBoardNo -> boardNo
    public void deleteLike(int boardNo, int userNo);  // gBoardNo -> boardNo
    public void addLike(int boardNo, int userNo);  // gBoardNo -> boardNo
    public void decrementLikeCount(int boardNo);  // gBoardNo -> boardNo
}