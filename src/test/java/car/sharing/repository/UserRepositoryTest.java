package car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import car.sharing.model.user.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/users/add-users.sql",
        "classpath:database/users/add-users-roles.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/users/remove-users-roles.sql",
        "classpath:database/users/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Verify the user by correct email")
    void existsByEmail_correctEmail_returnTrue() {
        String email = "sam@email.com";
        boolean actual = userRepository.existsByEmail(email);

        assertTrue(actual);
    }

    @Test
    @DisplayName("Verify the user by incorrect email")
    void existsByEmail_incorrectEmail_returnFalse() {
        String email = "sam@email";
        boolean actual = userRepository.existsByEmail(email);

        assertFalse(actual);
    }

    @Test
    @DisplayName("Find the user by correct email")
    void findByEmail_correctEmail_returnUser() {
        String email = "sam@email.com";
        Optional<User> actual = userRepository.findByEmail(email);

        assertTrue(actual.isPresent());
        assertEquals(email, actual.get().getEmail());
    }

    @Test
    @DisplayName("Find the user by incorrect email")
    void findByEmail_incorrectEmail_returnEmptyOptional() {
        String email = "sam@email";
        Optional<User> actual = userRepository.findByEmail(email);

        assertFalse(actual.isPresent());
    }
}
