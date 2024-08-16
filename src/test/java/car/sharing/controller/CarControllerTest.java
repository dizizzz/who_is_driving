package car.sharing.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import car.sharing.dto.car.CarDto;
import car.sharing.dto.car.CarRequestDto;
import car.sharing.model.car.CarType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/add-cars.sql")
            );
        }
    }

    @AfterEach
    void teardown() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/remove-cars.sql")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Add the car")
    @Sql(
            scripts = "classpath:database/cars/remove-cars-by-model.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void addCar_validRequestDto_success() throws Exception {
        CarRequestDto requestDto = new CarRequestDto()
                .setModel("Model 1")
                .setBrand("BMW")
                .setType(CarType.UNIVERSAL)
                .setInventory(12)
                .setDailyFee(BigDecimal.valueOf(145));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(
                post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.model", is(requestDto.getModel())))
                .andExpect(jsonPath("$.brand", is(requestDto.getBrand())))
                .andExpect(jsonPath("$.inventory", is(requestDto.getInventory())));

    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get all cars")
    void getAll_givenCars_shouldReturnAllCars() throws Exception {
        mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].model", is("Model 3")))
                .andExpect(jsonPath("$[0].brand", is("Tesla")))
                .andExpect(jsonPath("$[0].inventory", is(5)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].model", is("Model S")))
                .andExpect(jsonPath("$[1].brand", is("Tesla")))
                .andExpect(jsonPath("$[1].inventory", is(10)));
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get car by ID")
    void getCarById_givenId_shouldReturnCar() throws Exception {
        CarDto expected = new CarDto()
                .setId(1L).setModel("Model 3").setBrand("Tesla").setType(CarType.SUV)
                        .setInventory(5).setDailyFee(BigDecimal.valueOf(10.00));

        mockMvc.perform(
                        get("/cars/{id}", expected.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.model", is("Model 3")))
                .andExpect(jsonPath("$.brand", is("Tesla")))
                .andExpect(jsonPath("$.inventory", is(5)));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update car by ID")
    void updateCar_givenId_shouldReturnCar() throws Exception {
        Long updateId = 1L;
        CarRequestDto updateCar = new CarRequestDto()
                .setModel("Model 3")
                .setBrand("Tesla")
                .setType(CarType.SUV)
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(10.50));

        mockMvc.perform(
                        put("/cars/{id}", updateId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateCar))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model", is(updateCar.getModel())));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Delete a car")
    @Sql(
            scripts = "classpath:database/cars/add-car-to-delete.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void delete_validCarId_success() throws Exception {
        mockMvc.perform(
                        delete("/cars/{id}", 3L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}
