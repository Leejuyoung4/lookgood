package com.oo.groupcomment.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<GroupComment> getCommentsByBoardNo(int boardNo) {
        return groupCommentDao.findByBoardNo(boardNo);
    }

    // 댓글 등록
    @Override
    public void addComment(GroupComment comment) {
        groupCommentDao.insertComment(comment);
    }
    
    // 댓글 삭제
    @Override
    public boolean deleteComment(int boardCommentNo) {
        return groupCommentDao.removeComment(boardCommentNo);
    }

    // 댓글 수정
    @Override
    public void updateComment(GroupComment comment) {
        groupCommentDao.modifyComment(comment);
    }

    // 댓글 좋아요
    @Override
    public boolean addLike(int boardCommentNo, int userNo) {
        // TODO: 좋아요 기능 구현
        return groupCommentDao.insertLike(boardCommentNo, userNo);
    }

    // 댓글 좋아요 취소
    @Override
    public boolean removeLike(int boardCommentNo, int userNo) {
        // TODO: 좋아요 취소 기능 구현
        return groupCommentDao.deleteLike(boardCommentNo, userNo);
    }
    
 // 댓글 수 업데이트
    @Transactional
    public void updateCommentCount(int boardNo) {
        int commentCount = groupCommentDao.countCommentsByBoardNo(boardNo);
        groupCommentDao.updateCommentCount(boardNo, commentCount);
    }

    @Override
    public List<GroupComment> getRepliesByCommentNo(int parentCommentNo) {
        return groupCommentDao.findRepliesByCommentNo(parentCommentNo);
    }
    
}