package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TodoRepositoryCustom {
    Page<Todo> searchTodo(String weather, LocalDate startModifiedAt, LocalDate endModifiedAt, Pageable pageable);
}
