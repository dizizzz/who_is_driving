package car.sharing.service.impl;

import car.sharing.dto.user.UserDto;
import car.sharing.dto.user.UserRegistrationRequestDto;
import car.sharing.dto.user.UserUpdateRequestDto;
import car.sharing.exception.EntityNotFoundException;
import car.sharing.exception.RegistrationException;
import car.sharing.mapper.UserMapper;
import car.sharing.model.user.Role;
import car.sharing.model.user.RoleName;
import car.sharing.model.user.User;
import car.sharing.repository.RoleRepository;
import car.sharing.repository.UserRepository;
import car.sharing.service.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("Can`t register user by email: " + email);
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setLastName(requestDto.getLastName());
        user.setFirstName(requestDto.getFirstName());
        Role role = roleRepository.findByName(RoleName.CUSTOMER).orElseThrow(
                () -> new EntityNotFoundException("Can`t find role by name" + RoleName.CUSTOMER)
        );
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateRoleById(Long id, UserUpdateRequestDto requestDto) {
        String roleName = requestDto.getRoleName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user by id:" + id)
        );
        Role role = roleRepository.findByName(RoleName.valueOf(roleName)).orElseThrow(
                () -> new EntityNotFoundException("Can`t find role by name" + roleName)
        );

        Set<Role> updatedRoles = new HashSet<>(user.getRoles());
        updatedRoles.add(role);
        user.setRoles(updatedRoles);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getInfo(User authUser) {
        User user = userRepository.findByEmail(authUser.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find user by email:" + authUser.getEmail())
        );
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateInfo(User updateUser,
                              UserRegistrationRequestDto requestDto) {
        updateUser.setEmail(requestDto.getEmail());
        updateUser.setPassword(requestDto.getPassword());
        updateUser.setFirstName(requestDto.getFirstName());
        updateUser.setLastName(requestDto.getLastName());
        return userMapper.toDto(userRepository.save(updateUser));
    }
}
