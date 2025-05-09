package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoRepositoryCustom {
    Page<Todo> searchTodo(String weather, LocalDate startModifiedAt, LocalDate endModifiedAt, Pageable pageable);

    //키워드 검색 조회
    Page<TodoSearchResponse> searchTodoWithKeyword(String title, String nickname, LocalDate startAt, LocalDate endAt, Pageable pageable);

    //QueryDSL을 사용하기 위해
    Optional<Todo> findByIdWithUser(Long todoId);

}
