package com.oo.user.model.service;

import java.util.List;
import com.oo.user.model.dto.User;

public interface UserService {
    /**
     * 전체 사용자 목록을 조회합니다.
     * @return 전체 사용자 목록
     */
    List<User> getAllUserList();

    /**
     * 특정 사용자의 상세 정보를 조회합니다.
     * @param no 조회할 사용자 번호
     * @return 사용자 정보
     */
    User getUserDetail(int no);

    /**
     * 회원가입을 처리합니다.
     * @param user 가입할 사용자 정보 (아이디, 비밀번호, 이름, 이메일, 전화번호 포함)
     * @return 가입 성공 여부
     */
    boolean signup(User user);

    /**
     * 로그인을 처리합니다.
     * @param userId 사용자 아이디
     * @param password 비밀번호
     * @return 로그인된 사용자 정보, 실패시 null
     */
    User login(String userId, String password);

    /**
     * 사용자 정보를 수정합니다.
     * @param user 수정할 사용자 정보
     * @return 수정 성공 여부
     */
    boolean modify(User user);

    /**
     * 회원 탈퇴를 처리합니다.
     * @param no 탈퇴할 사용자 번호
     * @return 탈퇴 성공 여부
     */
    boolean remove(int no);

    /**
     * 아이디 중복을 확인합니다.
     * @param userId 확인할 아이디
     * @return 중복 여부 (true: 중복)
     */
    boolean isUserIdDuplicate(String userId);
}