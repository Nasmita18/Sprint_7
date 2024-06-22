package tests.courierTest;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;
import tests.dto.Courier;
import tests.dto.CourierLogin;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierAuthTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Step("Создание курьера")
    private void createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @Step("Авторизация курьера")
    private Response loginCourier(String login, String password) {
        CourierLogin courierLogin = new CourierLogin(login, password);

        return given()
                .header("Content-type", "application/json")
                .body(courierLogin)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();
    }

    @Test
    @Step("Тест: успешная авторизация курьера")
    public void courierCanLogin() {
        String randomUUID = UUID.randomUUID().toString(); //это строка нужна для того, чтобы каждый раз создавался уникальный курьер
        createCourier("ninja" + randomUUID, "1234", "saske");

        Response response = loginCourier("ninja" + randomUUID, "1234");
        response.then().statusCode(200);

        Integer id = response.path("id");
        MatcherAssert.assertThat(id, notNullValue());
    }

    @Test
    @Step("Тест: авторизация без логина")
    public void loginWithoutLogin() {
        Response response = loginCourier(null, "1234");
        response.then().statusCode(400);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест: авторизация без пароля")
    public void loginWithoutPassword() {
        Response response = loginCourier("ninja", null);
        response.then().statusCode(400);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест: авторизация с неправильными данными")
    public void loginWithIncorrectCredentials() {
        String randomUUID = UUID.randomUUID().toString(); //это строка нужна для того, чтобы каждый раз создавался уникальный курьер
        createCourier("ninja" + randomUUID, "1234", "saske");

        Response response = loginCourier("wronglogin", "wrongpassword");
        response.then().statusCode(404);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Тест: авторизация несуществующего пользователя")
    public void loginWithNonExistentUser() {
        Response response = loginCourier("nonexistent", "1234");
        response.then().statusCode(404);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Учетная запись не найдена"));
    }
}






