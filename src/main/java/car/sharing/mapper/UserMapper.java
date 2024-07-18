package car.sharing.mapper;

import car.sharing.config.MapperConfig;
import car.sharing.dto.user.UserDto;
import car.sharing.dto.user.UserRegistrationRequestDto;
import car.sharing.model.user.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
