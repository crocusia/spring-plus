package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoRepositoryCustom {
    Page<Todo> searchTodo(String weather, LocalDate startModifiedAt, LocalDate endModifiedAt, Pageable pageable);

    //QueryDSL을 사용하기 위해
    Optional<Todo> findByIdWithUser(Long todoId);
}
