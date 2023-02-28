package ru.practicum.service.user;

import ru.practicum.model.NewUserRequest;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    UserDto create(NewUserRequest request);

    void deleteById(Long userId);
}
