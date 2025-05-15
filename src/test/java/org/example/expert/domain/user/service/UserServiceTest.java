package org.example.expert.domain.user.service;

import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    private static final int TOTAL_USERS = 1_000_000;
    private static final int BATCH_SIZE = 1000;

    @BeforeAll
    void setUp() {
        if (userRepository.count() >= TOTAL_USERS) {
            System.out.println("✅ 이미 데이터가 존재하므로 삽입 생략");
            return;
        }

        System.out.println("🚀 100만 사용자 데이터 삽입 시작");
        List<User> batch = new ArrayList<>();
        for (int i = 0; i < TOTAL_USERS; i++) {
            String nickname = String.format("user%06d", i);
            String email = "user" + i + "@test.com";
            String password = "password";
            UserRole userRole = UserRole.USER;

            batch.add(new User(email, password, userRole, nickname));

            if (batch.size() >= BATCH_SIZE) {
                userRepository.saveAll(batch);
                batch.clear();
            }
        }
        userRepository.saveAll(batch);
    }

    @Test
    void 닉네임_정확_일치_조회_성능_테스트() {
        String targetNickname = "user999999";

        System.out.println("🔍 닉네임 조회 성능 측정 시작");
        long start = System.currentTimeMillis();

        List<UserResponse> result = userService.getUserByNickname(targetNickname);

        long end = System.currentTimeMillis();
        System.out.println("⏱️ 조회 시간(ms): " + (end - start));
        System.out.println("📦 결과 개수: " + result.size());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("user999999@test.com", result.get(0).getEmail());
    }
}