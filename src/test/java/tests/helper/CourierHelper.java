package tests.helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import tests.dto.Courier;

import static io.restassured.RestAssured.given;

public class CourierHelper {

    public CourierHelper() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    public Courier createCourier(String login, String password, String firstName) {
        return new Courier(login, password, firstName);
    }

    public Response sendCreateCourierRequest(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .extract()
                .response();
    }

    public Integer getCourierId(Courier courier) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();

        if (response.statusCode() == 200) {
            return response.path("id");
        } else {
            return null;
        }
    }

    public void deleteCourier(Integer courierId) {
        if (courierId != null) {
            given()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }

    public void verifyCourierCreation(Response response) {
        response.then().statusCode(201);
        Boolean ok = response.path("ok");
        assert ok != null && ok;
    }

    public void verifyErrorResponse(Response response, int expectedStatusCode, String expectedMessage) {
        response.then().statusCode(expectedStatusCode);
        String message = response.path("message");
        assert message.equals(expectedMessage);
    }
}
