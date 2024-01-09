package com.ssdam.todolist.repository;

import com.ssdam.todolist.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
}
