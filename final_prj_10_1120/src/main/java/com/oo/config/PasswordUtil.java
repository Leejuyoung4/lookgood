package com.oo.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    
    // Salt 생성
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    // 비밀번호 암호화
    public static String encrypt(String password) {
        try {
            // Salt 생성 및 비밀번호와 결합
            String salt = generateSalt();
            String passwordWithSalt = password + salt;
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(passwordWithSalt.getBytes());
            byte[] bytes = md.digest();
            
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            
            // Salt를 해시값과 함께 저장 (구분자 사용)
            return salt + ":" + builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 비밀번호 검증
    public static boolean verify(String password, String storedHash) {
        try {
            // 저장된 해시에서 Salt 추출
            String[] parts = storedHash.split(":");
            String salt = parts[0];
            String hash = parts[1];
            
            // 입력된 비밀번호를 같은 Salt로 해시
            String passwordWithSalt = password + salt;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(passwordWithSalt.getBytes());
            byte[] bytes = md.digest();
            
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            
            // 해시값 비교
            return hash.equals(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}