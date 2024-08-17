package car.sharing.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
