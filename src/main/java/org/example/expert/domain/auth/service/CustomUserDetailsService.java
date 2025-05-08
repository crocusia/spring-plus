package org.example.expert.domain.auth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.entity.CustomUserDetails;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    //실제 DB 조회, jwtToken에 필요한 정보를 위해 커스텀
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 유저입니다."));

        return new CustomUserDetails(user);
    }
}
