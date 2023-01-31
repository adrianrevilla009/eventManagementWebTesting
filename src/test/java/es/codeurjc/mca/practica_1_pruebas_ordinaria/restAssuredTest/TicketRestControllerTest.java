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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Calendar;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TicketRestControllerTest {
    @LocalServerPort
    private int port;

    private String apiPrefix = "/api/tickets";
    private String apiEventsPrefix = "/api/events";
    private String ticket;

    @BeforeEach
    public void init() throws JSONException, JsonProcessingException {
        RestAssured.port = this.port;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:" + this.port;

        User user = new User("admin", "admin@urjc.es", "pass", User.ROLE_ADMIN);
        user.setId(1L);
        Calendar c1 = Calendar.getInstance();
        c1.set(2021, Calendar.MAY, 2, 18, 30);
        Event e1 = new Event("Concierto municipal de Móstoles", "Concierto ofrecido por ...", c1.getTime(), 19.99, 50);
        e1.setCreator(user);

        Ticket ticket = new Ticket(user, e1);
        ticket.setPurchasePrice(20.0);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String ticketString = ow.writeValueAsString(ticket);

        this.ticket = ticketString;
    }

    @Test
    public void createTicketAsCustomerAndCheckIncreasedCapacity() {

        Response response = given().
                when().
                get("/api/events/4").
                thenReturn();

        int current_capacity = from(response.getBody().asString()).get("current_capacity");

        given().
                auth().basic("Michel", "pass").
                queryParam("eventId", 4).
                when().
                post("/api/tickets/").
                then().
                statusCode(201);

        given().
                when().
                get("/api/events/4").
                then().
                statusCode(200).
                body("current_capacity", equalTo(current_capacity + 1));
    }

    // TODO uncomment line 59 and 60 of DatabaseInitializer.java file to pass this test correctly
    @Test
    public void deleteTicketAsCustomerAndCheckDecreasedCapacity() {

        Response response = given().
                contentType("application/json").
                queryParam("id", 4L).
                when().
                get(this.apiEventsPrefix + "/4").
                then().
                extract().response();

        int capacity = from(response.getBody().asString()).get("current_capacity");

        given().
                contentType("application/json").
                queryParam("id", 5L).
                auth().basic("Michel", "pass").
                when().
                delete(this.apiPrefix + "/5").
                then().
                statusCode(200);

        given().
                contentType("application/json").
                queryParam("id", 4L).
                when().
                get(this.apiEventsPrefix + "/4").
                then().
                body("current_capacity", equalTo(capacity - 1));

    }
}
