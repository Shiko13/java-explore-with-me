package ru.practicum.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

@UtilityClass
public class UserConverter {

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User fromDto(NewUserRequest newUserRequest) {
        return new User(null, newUserRequest.getName(), newUserRequest.getEmail());
    }

    public static UserShortDto toShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
