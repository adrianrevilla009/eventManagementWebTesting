package es.codeurjc.mca.practica_1_pruebas_ordinaria.restAssuredTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Calendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.Event;

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
                body("id", equalTo(5));
    }

}
