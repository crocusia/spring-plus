package org.example.expert.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public UserRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<UserResponse> findByNickname(String nickname) {
        QUser user = QUser.user;
        return jpaQueryFactory
                .select(Projections.constructor(
                        UserResponse.class,
                        user.id,
                        user.email
                ))
                .from(user)
                .where(
                        user.nickname.eq(nickname)
                )
                .fetch();
    }
}
