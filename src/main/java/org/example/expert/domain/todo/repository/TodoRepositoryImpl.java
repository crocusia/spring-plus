package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Todo> searchTodo(String weather, LocalDate startModifiedAt, LocalDate endModifiedAt, Pageable pageable) {

        //조건 입력 여부에 따른 동적 쿼리 생성
        StringBuilder jpql = new StringBuilder("SELECT t FROM Todo AS t LEFT JOIN FETCH t.user WHERE 1=1");

        Map<String, Object> params = new HashMap<>();

        //날씨 정보가 Clear and Cool과 같은 형식으로 저장되기 떄문에 Clear가 쓰인 날씨를 찾기 위해 LIKE절을 사용함
        if (weather != null && !weather.isBlank()) {
            jpql.append(" AND t.weather LIKE :weather");
            params.put("weather", "%" + weather + "%");
        }

        if (startModifiedAt != null) {
            jpql.append(" AND t.modifiedAt >= :startModifiedAt");
            params.put("startModifiedAt", startModifiedAt.atStartOfDay());
        }

        if (endModifiedAt != null) {
            jpql.append(" AND t.modifiedAt <= :endModifiedAt");
            params.put("endModifiedAt", endModifiedAt.atTime(LocalTime.MAX));
        }

        jpql.append(" ORDER BY t.modifiedAt DESC");

        TypedQuery<Todo> query = em.createQuery(jpql.toString(), Todo.class);
        params.forEach(query::setParameter);

        // 페이징
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Todo> results = query.getResultList();

        // Count 쿼리
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(t) FROM Todo t WHERE 1=1");

        if (weather != null && !weather.isBlank()) {
            countJpql.append(" AND t.weather LIKE :weather");
        }

        if (startModifiedAt != null) {
            countJpql.append(" AND t.modifiedAt >= :startModifiedAt");
        }

        if (endModifiedAt != null) {
            countJpql.append(" AND t.modifiedAt <= :endModifiedAt");
        }

        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        params.forEach(countQuery::setParameter);

        Long total = countQuery.getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<TodoSearchResponse> searchTodoWithKeyword(String title, String nickname, LocalDate startAt, LocalDate endAt, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QUser writer = new QUser("writer");
        QUser managerUser = new QUser("managerUser");
        QComment comment = QComment.comment;

        Expression<Long> countManager = ExpressionUtils.as(
                JPAExpressions
                        .select(manager.count())
                        .from(manager)
                        .where(manager.todo.eq(todo)),
                "managerCount"
        );

        Expression<Long> countComment = ExpressionUtils.as(
                JPAExpressions
                        .select(comment.count())
                        .from(comment)
                        .where(comment.todo.eq(todo)),
                "commentCount"
        );

        List<TodoSearchResponse> results = jpaQueryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.title,
                        countManager,
                        countComment
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, managerUser)
                .join(todo.user, writer)
                .where(
                        title != null && !title.isBlank() ? todo.title.contains(title) : null,
                        nickname != null && !nickname.isBlank() ? writer.nickname.contains(nickname) : null,
                        startAt != null ? todo.createdAt.goe(startAt.atStartOfDay()) : null,
                        endAt != null ? todo.createdAt.loe(endAt.atTime(LocalTime.MAX)) : null
                )
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long total = jpaQueryFactory
                .select(todo.countDistinct())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, managerUser)
                .join(todo.user, writer)
                .where(
                        title != null && !title.isBlank() ? todo.title.contains(title) : null,
                        nickname != null && !nickname.isBlank() ? writer.nickname.contains(nickname) : null,
                        startAt != null ? todo.createdAt.goe(startAt.atStartOfDay()) : null,
                        endAt != null ? todo.createdAt.loe(endAt.atTime(LocalTime.MAX)) : null
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user, user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }
}
