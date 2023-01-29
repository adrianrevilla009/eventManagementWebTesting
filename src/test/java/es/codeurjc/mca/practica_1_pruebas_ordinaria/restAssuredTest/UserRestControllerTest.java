package es.codeurjc.mca.practica_1_pruebas_ordinaria.restAssuredTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.Event;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.ticket.Ticket;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Calendar;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestControllerTest {
    @LocalServerPort
    private int port;

    private String apiPrefix = "/api/users";
    private String user;

    @BeforeEach
    public void init() throws JSONException, JsonProcessingException {
        RestAssured.port = this.port;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:" + this.port;

        User user = new User("Michel", "michel.maes@urjc.es", "pass", User.ROLE_CUSTOMER);
        user.setId(1L);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String userString = ow.writeValueAsString(user);

        this.user = userString;
    }

    @Test
    public void createUserAsCustomerAndCheckCreateAvailability() throws JSONException {
        JSONObject body = new JSONObject();

        body.put("eventId", 1L);

        Response response = given().
                contentType("application/json").
                auth().basic("Michel", "pass").
                body(this.user).
                when().
                post(this.apiPrefix).
                then().
                extract().response();

        int id = from(response.getBody().asString()).get("id");
    }
}
