package com.prosa.workshop.rest.todo.service;

import com.prosa.workshop.rest.todo.exception.ResourceNotFoundException;
import com.prosa.workshop.rest.todo.dto.CreateTodoRequest;
import com.prosa.workshop.rest.todo.dto.TodoDto;
import com.prosa.workshop.rest.todo.dto.UpdateTodoRequest;
import com.prosa.workshop.rest.todo.model.Todo;
import com.prosa.workshop.rest.todo.model.TodoStatus;
import com.prosa.workshop.rest.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // -------------------------------------------------------------------------
    // TODO 1 — findAll
    // -------------------------------------------------------------------------
    // If `status` is not null, filter todos by that status.
    // Otherwise return all todos.
    // Map each Todo entity to a TodoDto using toDto().
    // -------------------------------------------------------------------------
    public List<TodoDto> findAll(String status) {
        //throw new UnsupportedOperationException("TODO 1: implement findAll");
        if (status != null) {
            return todoRepository.findByStatus(TodoStatus.valueOf(status.toUpperCase()))
                    .stream().map(this::toDto).collect(Collectors.toList());
        }
        return todoRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        
    }

    // -------------------------------------------------------------------------
    // TODO 2 — findById
    // -------------------------------------------------------------------------
    // Find a Todo by id. If not found, throw ResourceNotFoundException.forTodo(id).
    // Return the result mapped to a TodoDto.
    // -------------------------------------------------------------------------
    public TodoDto findById(Long id) {
        // throw new UnsupportedOperationException("TODO 2: implement findById");
        return todoRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> ResourceNotFoundException.forTodo(id));
    }

    // -------------------------------------------------------------------------
    // TODO 3 — create
    // -------------------------------------------------------------------------
    // Build a new Todo entity from the request, save it, and return the DTO.
    // -------------------------------------------------------------------------
    @Transactional
    public TodoDto create(CreateTodoRequest request) {
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(TodoStatus.OPEN)
                .build();
        return toDto(todoRepository.save(todo));
    }

    // -------------------------------------------------------------------------
    // TODO 4 — update
    // -------------------------------------------------------------------------
    // Find the todo by id (throw 404 if missing). Apply non-null fields from
    // the request. Save and return the updated DTO.
    // -------------------------------------------------------------------------
    @Transactional
    public TodoDto update(Long id, UpdateTodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forTodo(id));
        if (request.getTitle() != null)       todo.setTitle(request.getTitle());
        if (request.getDescription() != null) todo.setDescription(request.getDescription());
        if (request.getStatus() != null)      todo.setStatus(request.getStatus());
        if (request.getDueDate() != null)     todo.setDueDate(request.getDueDate());
        return toDto(todoRepository.save(todo));
    }

    // -------------------------------------------------------------------------
    // TODO 5 — updateStatus
    // -------------------------------------------------------------------------
    // Find the todo by id (throw 404 if missing). Set its status. Return DTO.
    // -------------------------------------------------------------------------
    @Transactional
    public TodoDto updateStatus(Long id, TodoStatus newStatus) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forTodo(id));
        todo.setStatus(newStatus);
        return toDto(todoRepository.save(todo));
    }


    // -------------------------------------------------------------------------
    // TODO 6 — delete
    // -------------------------------------------------------------------------
    // Find the todo by id (throw 404 if missing). Then delete it.
    // -------------------------------------------------------------------------
    @Transactional
    public void delete(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forTodo(id));
        todoRepository.delete(todo);
    }

    // -------------------------------------------------------------------------
    // Mapping helper — converts a Todo entity to a TodoDto.
    // You can use this in all methods above with: .map(this::toDto)
    // -------------------------------------------------------------------------
    private TodoDto toDto(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .status(todo.getStatus().name())
                .createdAt(todo.getCreatedAt())
                .dueDate(todo.getDueDate())
                .build();
    }
}
