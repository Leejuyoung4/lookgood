package com.oo.groupcomment.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class GroupComment {
    private int boardCommentNo;           
    private String boardCommentContent;    
    private LocalDateTime boardCommentRegDate;  
    private LocalDateTime boardCommentModDate;  
    private int boardNo;                  
    private int userNo;
    private Integer parentCommentNo;       
    private int commentDepth;                    
    private List<GroupComment> children;   
    
	public GroupComment() {	}

    public GroupComment(int boardCommentNo, String boardCommentContent, LocalDateTime boardCommentRegDate,
    		LocalDateTime boardCommentModDate, int boardNo, int userNo, Integer parentCommentNo, int commentDepth,
    		List<GroupComment> children) {
    	this.boardCommentNo = boardCommentNo;
    	this.boardCommentContent = boardCommentContent;
    	this.boardCommentRegDate = boardCommentRegDate;
    	this.boardCommentModDate = boardCommentModDate;
    	this.boardNo = boardNo;
    	this.userNo = userNo;
    	this.parentCommentNo = parentCommentNo;
    	this.commentDepth = commentDepth;
    	this.children = children;
    }
    
    public Integer getParentCommentNo() {
        return parentCommentNo;
    }

    public void setParentCommentNo(Integer parentCommentNo) {
        this.parentCommentNo = parentCommentNo;
    }

    

    public List<GroupComment> getChildren() {
        return children;
    }

    public void setChildren(List<GroupComment> children) {
        this.children = children;
    }

	public int getBoardCommentNo() {
		return boardCommentNo;
	}

	public void setBoardCommentNo(int boardCommentNo) {
		this.boardCommentNo = boardCommentNo;
	}

	public String getBoardCommentContent() {
		return boardCommentContent;
	}

	public void setBoardCommentContent(String boardCommentContent) {
		this.boardCommentContent = boardCommentContent;
	}

	public LocalDateTime getBoardCommentRegDate() {
		return boardCommentRegDate;
	}

	public void setBoardCommentRegDate(LocalDateTime boardCommentRegDate) {
		this.boardCommentRegDate = boardCommentRegDate;
	}

	public LocalDateTime getBoardCommentModDate() {
		return boardCommentModDate;
	}

	public void setBoardCommentModDate(LocalDateTime boardCommentModDate) {
		this.boardCommentModDate = boardCommentModDate;
	}

	public int getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}

	public int getUserNo() {
		return userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}

	public int getCommentDepth() {
		return commentDepth;
	}

	public void setCommentDepth(int commentDepth) {
		this.commentDepth = commentDepth;
	}

	@Override
	public String toString() {
		return "GroupComment [boardCommentNo=" + boardCommentNo + ", boardCommentContent=" + boardCommentContent
				+ ", boardCommentRegDate=" + boardCommentRegDate + ", boardCommentModDate=" + boardCommentModDate
				+ ", boardNo=" + boardNo + ", userNo=" + userNo + ", parentCommentNo=" + parentCommentNo
				+ ", commentDepth=" + commentDepth + ", children=" + children + "]";
	}


    
    

    
}