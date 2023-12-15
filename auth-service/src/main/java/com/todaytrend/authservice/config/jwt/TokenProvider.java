package com.todaytrend.authservice.config.jwt;

import com.todaytrend.authservice.domain.UserInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public TokenInfo generateToken(UserInterface userInterface, Duration expiredAt, String tokenName) {
        Date now = new Date();
        String token = makeToken(new Date(now.getTime() + expiredAt.toMillis()), userInterface);

        // 쿠키의 만료 시간을 2시간으로 설정
        int cookieExpiresIn = (int) Duration.ofHours(2).getSeconds();

        return new TokenInfo(token, cookieExpiresIn);
    }

    // JWT 토큰 생성 메서드
    private String makeToken(Date expiry, UserInterface userInterface){
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ :JWT
                .setIssuer(jwtProperties.getIssuer()) // yml에 저장한 issuer 값
                .setIssuedAt(now) // iat : 현재 시간
                .setExpiration(expiry) // expiry 멤버 변숫값
                .setSubject(userInterface.getUuid()) // uuid로 생성
                .claim("uuid", userInterface.getUuid()) // 유저 uuid
                .claim("role", userInterface.getRole()) // 유저 Role
                // 서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token);
            log.info("복호화 성공");
            return true;
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            log.info("복호화 실패" + e);
            return false;
        }
    }
    
    // 토큰을 기반으로 UUID를 가져오는 메서드
    public String getUserUuid(String token){
        Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
