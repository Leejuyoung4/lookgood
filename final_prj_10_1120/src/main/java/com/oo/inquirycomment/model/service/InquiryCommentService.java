package com.oo.inquirycomment.model.service;

import java.util.List;

import com.oo.inquirycomment.model.dto.InquiryComment;

public interface InquiryCommentService {

	List<InquiryComment> getCommentsBygINo(int iNo);

}
