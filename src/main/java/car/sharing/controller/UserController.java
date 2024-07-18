package car.sharing.controller;

import car.sharing.dto.user.UserDto;
import car.sharing.dto.user.UserRegistrationRequestDto;
import car.sharing.dto.user.UserUpdateRequestDto;
import car.sharing.model.user.User;
import car.sharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users management", description = "Endpoints for users managing")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update the user`s role by ID",
            description = "Update the user`s role by ID")
    public UserDto updateRole(@PathVariable Long id,
                          @RequestBody UserUpdateRequestDto requestDto) {
        return userService.updateRoleById(id, requestDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get user`s profile info", description = "Get user`s profile info")
    public UserDto getInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getInfo(user);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/me")
    @Operation(summary = "Update user`s profile info", description = "Update user`s profile info")
    public UserDto updateInfo(Authentication authentication,
                              @RequestBody UserRegistrationRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateInfo(user, requestDto);
    }

}
