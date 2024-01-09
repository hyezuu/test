package com.ssdam.todolist.controller;

import com.ssdam.dto.MultiResponseDto;
import com.ssdam.dto.SingleResponseDto;
import com.ssdam.todolist.dto.TodoListDto;
import com.ssdam.todolist.entity.TodoList;
import com.ssdam.todolist.mapper.TodoListMapper;
import com.ssdam.todolist.service.TodoListService;
import com.ssdam.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/todos")
@Validated
public class TodoListController {
    private final static String TODOLIST_DEFAULT_URL = "/v1/todos";
    private final TodoListService todoListService;
    private final TodoListMapper mapper;

    public TodoListController(TodoListService todoListService, TodoListMapper mapper) {
        this.todoListService = todoListService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postTodoList(@Valid @RequestBody TodoListDto.Post requestBody) {
        TodoList TodoList = mapper.todoPostDtoToTodoList(requestBody);
        TodoList createdTodoList = todoListService.createTodoList(TodoList);
        URI location = UriCreator.createUri(TODOLIST_DEFAULT_URL, createdTodoList.getTodolistId());

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{todolist-id}")
    public ResponseEntity patchTodo(@PathVariable("todolist-id") @Positive long todolistId,
                                    @Valid @RequestBody TodoListDto.Patch requestBody) {
        requestBody.addTodoListId(todolistId);
        TodoList updatedTodo = todoListService.updateTodoList(mapper.todoPatchDtoToTodoList(requestBody));

        return ResponseEntity.status(HttpStatus.OK).body(mapper.todoToTodoListResponseDto(updatedTodo));
    }

    @GetMapping("/{todolist-id}")
    public ResponseEntity getTodo(@PathVariable("todolist-id") @Positive long todolistId) {
        TodoList foundTodoList = todoListService.findTodoList(todolistId);
        TodoListDto.Response response = mapper.todoToTodoListResponseDto(foundTodoList);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    @GetMapping
    public ResponseEntity getTodos(@Positive @RequestParam int page,
                                   @Positive @RequestParam int size) {
        Page<TodoList> pageTodo = todoListService.findAll(page, size);
        List<TodoList> todos = pageTodo.getContent();
        return ResponseEntity.ok(new MultiResponseDto(mapper.todosToTodoResponseDtos(todos), pageTodo));
    }

    @DeleteMapping("/{todolist-id}")
    public ResponseEntity deleteTodoList(@PathVariable("todolist-id") long todolistId) {
        todoListService.deleteTodo(todolistId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity deleteAllTodos() {
        todoListService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
