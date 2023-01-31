package es.codeurjc.mca.practica_1_pruebas_ordinaria.restAssuredTest;

import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import io.restassured.RestAssured;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestControllerTest {
    @LocalServerPort
    private int port;

    private String apiPrefix = "/api/users";
    private User userOrganizer;
    private User userCustomer;

    @BeforeEach
    public void init() {
        RestAssured.port = this.port;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:" + this.port;

        User userOrganizer = new User("Pepe", "pepe@urjc.es", "pass", User.ROLE_ORGANIZER);
        userOrganizer.setId(2L);
        User userCustomer = new User("Juan", "juan@urjc.es", "pass", User.ROLE_CUSTOMER);
        userCustomer.setId(3L);

        this.userOrganizer = userOrganizer;
        this.userCustomer = userCustomer;
    }

    @Test
    public void createUserAsCustomerAndCheckCreateAvailability() throws JSONException{

        JSONObject bodyOrganizerUser = new JSONObject();
        bodyOrganizerUser.put("name", "2" + this.userOrganizer.getName());
        bodyOrganizerUser.put("email", "2" + this.userOrganizer.getEmail());
        bodyOrganizerUser.put("password", this.userOrganizer.getPassword());

        given().
                contentType("application/json").
                body(bodyOrganizerUser.toString()).
                queryParam("type", "Organizer").
                when().
                post(this.apiPrefix + "/").
                then().
                statusCode(201);

        JSONObject bodyCustomer = new JSONObject();
        bodyCustomer.put("name", "2" + this.userCustomer.getName());
        bodyCustomer.put("email", "2" + this.userCustomer.getEmail());
        bodyCustomer.put("password", this.userCustomer.getPassword());

        given().
                contentType("application/json").
                body(bodyCustomer.toString()).
                queryParam("type", "Customer").
                when().
                post(this.apiPrefix + "/").
                then().
                statusCode(201);
    }

    @Test
    public void deleteUserAsAdminAndCheckDeleteAvailability() {

        given().
                auth().basic("admin", "pass").
                when().
                delete( this.apiPrefix + "/2").
                then().
                statusCode(204);

        given().
                auth().basic("admin", "pass").
                when().
                delete(this.apiPrefix + "/3").
                then().
                statusCode(204);
    }
}
