package com.oo.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oo.user.model.dto.User;
import com.oo.user.model.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*") // 모든 출처 허용
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// 전체 사용자 목록 가져오기
	@GetMapping
	public ResponseEntity<Object> userList() {
		try {
			List<User> userList = userService.getAllUserList();
			if (userList.isEmpty()) {
				return new ResponseEntity<>(userList, HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(userList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 특정 사용자 목록 가져오기
	@GetMapping("/{user_no}")
	public ResponseEntity<Object> userDetail(@PathVariable("user_no") int no) {
		try {
			User user = userService.getUserDetail(no);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	// 사용자 회원가입
	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> signup(@RequestBody User user) {
	    System.out.println("전달받은 유저 데이터: " + user); // 디버깅 로그 추가
	    try {
	        if (userService.signup(user)) {
	            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "회원가입 성공"));
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "회원가입 실패"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 로그 출력
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "서버 오류 발생"));
	    }
	}


	// 사용자 로그인
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 1. 요청값 유효성 검사
	        if (user.getUserId() == null || user.getUserId().trim().isEmpty() || 
	            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
	            response.put("success", false);
	            response.put("message", "아이디와 비밀번호를 모두 입력해주세요.");
	            return ResponseEntity.badRequest().body(response);
	        }

	        // 2. 로그인 처리
	        User loginUser = userService.login(user.getUserId(), user.getPassword());
	        if (loginUser != null) {
	            // 로그인 성공
	            response.put("success", true);
	            response.put("user", loginUser); // 사용자 정보 반환
	            response.put("message", "로그인 성공");
	            return ResponseEntity.ok(response);
	        } else {
	            // 로그인 실패
	            response.put("success", false);
	            response.put("message", "아이디 또는 비밀번호를 확인하세요.");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	        }
	    } catch (Exception e) {
	        // 3. 예외 처리
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "서버 오류가 발생했습니다.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}


	// 회원정보 수정
	@PutMapping("/{no}")
	public ResponseEntity<String> modify(@RequestBody User user, @PathVariable("no") int no) {
		try {
			user.setUserNo(no);
			boolean isModified = userService.modify(user);
			if (isModified) {
				return ResponseEntity.status(HttpStatus.OK).body("사용자 정보가 성공적으로 수정되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("사용자 정보 수정에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	// 회원탈퇴
	@DeleteMapping("/{no}")
	public ResponseEntity<String> delete(@PathVariable int no) {
		try {
			boolean isRemoved = userService.remove(no);
			if (isRemoved) {
				return ResponseEntity.status(HttpStatus.OK).body("사용자가 성공적으로 삭제되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 사용자를 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	// 아이디 중복 확인
	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestParam String userId) {
		System.out.println(10);
		try {
			boolean isDuplicate = userService.isUserIdDuplicate(userId);
			Map<String, Boolean> response = new HashMap<>();
			response.put("isDuplicate", isDuplicate);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
