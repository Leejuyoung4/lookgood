package com.oo.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oo.config.JwtUtil;
import com.oo.config.PasswordUtil;
import com.oo.user.model.dto.User;
import com.oo.user.model.service.UserService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 필수 필드 검증
            if (user.getUserId() == null || user.getPassword() == null || 
                user.getUserName() == null || user.getEmail() == null) {
                response.put("success", false);
                response.put("message", "필수 정보를 모두 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }

            // 비밀번호 암호화
            String encryptedPassword = PasswordUtil.encrypt(user.getPassword());
            user.setPassword(encryptedPassword);

            boolean result = userService.signup(user);
            if (result) {
                response.put("success", true);
                response.put("message", "회원가입이 완료되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "회원가입에 실패했습니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("로그인 요청 데이터: " + user);  // 요청 데이터 출력

            if (user.getUserId() == null || user.getPassword() == null) {
                response.put("success", false);
                response.put("message", "아이디와 비밀번호를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }

            User loginUser = userService.login(user.getUserId(), user.getPassword());
            System.out.println("로그인 결과: " + loginUser);  // 로그인 결과 출력

            if (loginUser != null) {
                String token = jwtUtil.createToken(loginUser.getUserNo(), loginUser.getUserId());
                
                loginUser.setPassword(null);  // 비밀번호 제외
                
                response.put("success", true);
                response.put("token", token);
                response.put("user", loginUser);
                response.put("message", "로그인 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();  // 스택 트레이스 출력
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 사용자 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Bearer 토큰에서 실제 토큰 추출
            token = token.replace("Bearer ", "");
            
            // 토큰 유효성 검사
            if (!jwtUtil.validateToken(token)) {
                response.put("success", false);
                response.put("message", "유효하지 않은 토큰입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 토큰에서 사용자 정보 추출
            Claims claims = jwtUtil.getClaims(token);
            int userNo = claims.get("userNo", Integer.class);

            User user = userService.getUserDetail(userNo);
            if (user != null) {
                user.setPassword(null);
                response.put("success", true);
                response.put("user", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "사용자 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 아이디 중복 체크
     */
    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Object>> checkId(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDuplicate = userService.isUserIdDuplicate(userId);
            response.put("success", true);
            response.put("isDuplicate", isDuplicate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}