package car.sharing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import car.sharing.service.impl.UserServiceImpl;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private Role role;
    private UserDto userDto;
    private UserUpdateRequestDto updateRequestDto;
    private UserRegistrationRequestDto registrationRequestDto;

    @BeforeEach
    public void setup() {
        role = new Role();
        role.setId(1L);
        role.setName(RoleName.CUSTOMER);

        user = new User();
        user.setId(1L);
        user.setEmail("bob@example.com");
        user.setPassword("123456789encodedPassword");
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setRoles(Set.of(role));

        userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
    }

    @Test
    @DisplayName("Given correct id, check if user is registered")
    void register_VerifyRegistration() throws Exception {
        //given
        registrationRequestDto = new UserRegistrationRequestDto();
        registrationRequestDto.setEmail("bob@example.com");
        registrationRequestDto.setPassword("123456789");
        registrationRequestDto.setRepeatPassword("123456789");
        registrationRequestDto.setFirstName("Bob");
        registrationRequestDto.setLastName("Smith");

        //when
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequestDto.getPassword()))
                .thenReturn("encodedPassword");

        when(roleRepository.findByName(RoleName.CUSTOMER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto actual = userService.register(registrationRequestDto);

        //then
        assertNotNull(actual);
        assertEquals(userDto, actual);
    }

    @Test
    @DisplayName("Verify user registration with existing email throws exception")
    public void register_WithExistingEmail_ThrowsRegistrationException()
            throws RegistrationException {
        //given
        registrationRequestDto = new UserRegistrationRequestDto();
        registrationRequestDto.setEmail("bob@example.com");
        registrationRequestDto.setPassword("123456789");
        registrationRequestDto.setRepeatPassword("123456789");
        registrationRequestDto.setFirstName("Bob");
        registrationRequestDto.setLastName("Smith");

        //when
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        RegistrationException thrown = assertThrows(
                RegistrationException.class,
                () -> userService.register(registrationRequestDto)
        );

        //then
        assertEquals("Can`t register user by email: bob@example.com", thrown.getMessage());
    }

    @Test
    @DisplayName("Verify update user`s role by id, given incorrect id")
    void updateRoleById_WhenUserDoesNotExist_ShouldThrowException() {
        //given
        updateRequestDto = new UserUpdateRequestDto();
        updateRequestDto.setRoleName("MANAGER");

        //when
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoleById(user.getId(), updateRequestDto));
    }

    @Test
    @DisplayName("Verify user`s info, given correct value")
    void getInfo_WhenUserExists_ShouldReturnUserDto() {
        //when
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getInfo(user);

        //then
        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Verify user`s info, given incorrect value")
    void getInfo_WhenUserDoesNotExist_ShouldThrowException() {
        //when
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getInfo(user)
        );

        //then
        assertEquals("Can`t find user by email:" + user.getEmail(), thrown.getMessage());
    }

    @Test
    @DisplayName("Verify update user info")
    public void updateInfo_WithValidData_ReturnUserDto() {
        //given
        registrationRequestDto = new UserRegistrationRequestDto();
        registrationRequestDto.setEmail("bob@example.com");
        registrationRequestDto.setPassword("123456789");
        registrationRequestDto.setRepeatPassword("123456789");
        registrationRequestDto.setFirstName("Bob");
        registrationRequestDto.setLastName("Smith");

        //when
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateInfo(user, registrationRequestDto);

        //then
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals("123456789", user.getPassword());
    }
}
