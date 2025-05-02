package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoRepositoryImpl implements TodoRepositoryCustom  {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Todo> searchTodo(String weather, LocalDate startModifiedAt, LocalDate endModifiedAt, Pageable pageable){

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
}
