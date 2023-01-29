package es.codeurjc.mca.practica_1_pruebas_ordinaria.restAssuredTest;

/*import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static io.restassured.RestAssured.given;*/
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.Event;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Calendar;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventRestControllerTest {
    @LocalServerPort
    private int port;

    private String apiPrefix = "/api/events";
    private String event;
    @BeforeEach
    public void init() throws JsonProcessingException {
        RestAssured.port = this.port;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:"+ this.port;

        User user = new User("admin", "admin@urjc.es", "pass", User.ROLE_ADMIN);
        user.setId(1L);
        Calendar c1 = Calendar.getInstance();
        c1.set(2021, Calendar.MAY, 2, 18, 30);

        Event e1 = new Event("Concierto municipal de Móstoles", "Concierto ofrecido por ...", c1.getTime(), 19.99, 50);
        e1.setCreator(user);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String eventString = ow.writeValueAsString(e1);

        this.event = eventString;
    }
    /*@Container
    public static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.22")
            .withDatabaseName("events")
            .withUsername("adrian")
            .withPassword("adrian");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    }*/

    @Test
    public void createEventTestAsOrganizer() {
        String body = this.event;

        given().
                contentType("application/json").
                body(body)
                .auth().basic("Patxi", "pass").
        when().
                post(this.apiPrefix + "/").
        then().
                statusCode(201).
                body("id", equalTo(1L));
    }

}
