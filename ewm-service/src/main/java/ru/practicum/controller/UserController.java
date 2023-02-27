package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.service.user.UserService;
import ru.practicum.NewUserRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll(@RequestParam List<Long> ids,
                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get /admin/users");
        return userService.getAll(ids, from, size);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid NewUserRequest request) {
        log.info("Post /admin/users, user_name={}, user_email", request.getName(), request.getEmail());
        return new ResponseEntity<>(userService.create(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteById(@PathVariable @PositiveOrZero long userId) {
        log.info("Delete /admin/users/{}", userId);
        userService.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
