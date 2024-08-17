package car.sharing;

import car.sharing.service.telegram.BotInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ApplicationTests {

    @MockBean
    private BotInitializer botInitializer;

    @Test
         void contextLoads() {
    }
}
