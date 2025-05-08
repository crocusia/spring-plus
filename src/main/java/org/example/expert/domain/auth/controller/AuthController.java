package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.entity.CustomUserDetails;
import org.example.expert.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest signinRequest) {
        try {
            //authenticationToken 발급
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword());

            //Security 내부에서 유저 조회 후 authentication 반환
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            //jwtToken 생성
            String bearerToken = jwtUtil.createToken(userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getRole(),
                    userDetails.getNickname());

            return ResponseEntity.ok(new SigninResponse(bearerToken));
            //return authService.signin(signinRequest);
        } catch (BadCredentialsException e){
            log.warn("로그인 실패 - 잘못된 자격 증명: {}", signinRequest.getEmail());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (AuthenticationException e){
            log.warn("로그인 실패 - 인증 오류: {}", signinRequest.getEmail());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
