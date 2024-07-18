package car.sharing.service;

import car.sharing.dto.user.UserDto;
import car.sharing.dto.user.UserRegistrationRequestDto;
import car.sharing.dto.user.UserUpdateRequestDto;
import car.sharing.exception.RegistrationException;
import car.sharing.model.user.User;
import com.stripe.exception.StripeException;

public interface UserService {
    UserDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException, StripeException;

    UserDto updateRoleById(Long id, UserUpdateRequestDto requestDto);

    UserDto getInfo(User authUser);

    UserDto updateInfo(User updateUser, UserRegistrationRequestDto requestDto);
}
