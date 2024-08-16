package car.sharing.repository;

import car.sharing.model.user.Role;
import car.sharing.model.user.RoleName;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Find the role by correct name")
    void findByName_correctName_returnTrue() {
        Optional<Role> role = roleRepository.findByName(RoleName.CUSTOMER);

        Assertions.assertTrue(role.isPresent(), "CUSTOMER role should be present");
        Assertions.assertEquals(RoleName.CUSTOMER,
                role.get().getName(),
                "Role name should be CUSTOMER");
    }
}
