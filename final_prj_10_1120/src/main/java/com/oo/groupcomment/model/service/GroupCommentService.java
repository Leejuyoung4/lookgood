package com.oo.groupcomment.model.service;

import java.util.List;
import com.oo.groupcomment.model.dto.GroupComment;

public interface GroupCommentService {
    // 모임게시판 댓글 목록 조회
    List<GroupComment> getCommentsByBoardNo(int boardNo);
    
    // 댓글 등록
    void addComment(GroupComment comment);
    
    // 댓글 삭제
    boolean deleteComment(int boardCommentNo);

    // 댓글 수정
    void updateComment(GroupComment comment);

    // 댓글 좋아요
    boolean addLike(int boardCommentNo, int userNo);
    
    // 댓글 좋아요 취소
    boolean removeLike(int boardCommentNo, int userNo);
    
    public void updateCommentCount(int boardNo);

	List<GroupComment> getRepliesByCommentNo(int parentCommentNo);
   
    
}