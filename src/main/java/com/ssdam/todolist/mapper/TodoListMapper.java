package com.ssdam.todolist.mapper;

import com.ssdam.todolist.dto.TodoListDto;
import com.ssdam.todolist.entity.TodoList;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TodoListMapper {
    TodoList todoPostDtoToTodoList(TodoListDto.Post requestBody);
    TodoList todoPatchDtoToTodoList(TodoListDto.Patch requestBody);

    TodoListDto.Response todoToTodoListResponseDto(TodoList todoList);
    List<TodoListDto.Response> todosToTodoResponseDtos(List<TodoList> todos);
}
