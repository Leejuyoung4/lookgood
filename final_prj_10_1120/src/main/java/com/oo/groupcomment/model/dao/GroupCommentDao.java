package com.oo.groupcomment.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.oo.groupcomment.model.dto.GroupComment;

@Mapper
public interface GroupCommentDao {
    // 모임게시판 댓글 목록 조회
    List<GroupComment> findByBoardNo(@Param("boardNo") int boardNo);

    // 댓글 등록
    void insertComment(GroupComment comment);
    
    // 댓글 삭제
    boolean removeComment(int boardCommentNo);

    // 댓글 수정
    void modifyComment(GroupComment comment);
    
    // 댓글 좋아요 추가
    boolean insertLike(@Param("boardCommentNo") int boardCommentNo, @Param("userNo") int userNo);
    
    // 댓글 좋아요 삭제
    boolean deleteLike(@Param("boardCommentNo") int boardCommentNo, @Param("userNo") int userNo);

	int countCommentsByBoardNo(int boardNo);

	void updateCommentCount(int boardNo, int commentCount);
	
    
    // 댓글 번호로 게시글 번호 조회
    int getBoardNoByCommentNo(int boardCommentNo);
}