package es.codeurjc.mca.practica_1_pruebas_ordinaria.restAssuredTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class EventRestControllerTest {
    @Container
    public static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.22")
            .withDatabaseName("events")
            .withUsername("adrian")
            .withPassword("adrian");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    }




}
