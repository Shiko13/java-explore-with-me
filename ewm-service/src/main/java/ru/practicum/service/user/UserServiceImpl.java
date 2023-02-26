package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.NewUserRequest;
import ru.practicum.converter.UserConverter;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        List<User> resultList;
        if (ids.isEmpty()) {
            resultList = userRepository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            resultList = userRepository.findAllById(ids);
        }
        return resultList.stream()
                .map(UserConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest request) {
        final User user = UserConverter.fromDto(request);
        final User savedUser = userRepository.save(user);
        final UserDto newDto = UserConverter.toDto(savedUser);
        log.info("New user with id {}, name {} & email {} created.",
                newDto.getId(),
                newDto.getName(),
                newDto.getEmail());

        return newDto;
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
        log.info("User with id {} deleted successfully", userId);
    }
}
