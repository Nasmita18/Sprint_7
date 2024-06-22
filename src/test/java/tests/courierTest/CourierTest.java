package tests.courierTest;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;
import tests.dto.Courier;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierTest {

    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Step("Создание курьера с логином: {login}, паролем: {password}, именем: {firstName}")
    private Courier createCourier(String login, String password, String firstName) {
        return new Courier(login, password, firstName);
    }

    @Step("Отправка запроса на создание курьера")
    private Response sendCreateCourierRequest(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .extract()
                .response();
    }

    @Step("Проверка успешного создания курьера")
    private void verifyCourierCreation(Response response) {
        response.then().statusCode(201);
        Boolean ok = response.path("ok");
        MatcherAssert.assertThat(ok, equalTo(true));
    }

    @Step("Проверка ошибки создания курьера с кодом статуса {expectedStatusCode} и сообщением {expectedMessage}")
    private void verifyErrorResponse(Response response, int expectedStatusCode, String expectedMessage) {
        response.then().statusCode(expectedStatusCode);
        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo(expectedMessage));
    }

    @Step("Получение ID курьера")
    private Integer getCourierId(Courier courier) {
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

    @Step("Удаление курьера")
    private void deleteCourier(Integer courierId) {
        if (courierId != null) {
            given()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }

    @After
    public void tearDown() {
        if (courier != null) {
            try {
                Integer courierId = getCourierId(courier);
                deleteCourier(courierId);
            } catch (Exception e) {
                System.out.println("Ошибка удаления курьера: " + e.getMessage());
            }
        }
    }

    @Test
    @Step("Тест: успешное создание курьера")
    public void createCourierSuccess() {
        courier = createCourier("ninja" + System.currentTimeMillis(), "1234", "saske");
        Response response = sendCreateCourierRequest(courier);
        verifyCourierCreation(response);
    }

    @Test
    @Step("Тест: создание курьера без логина")
    public void createCourierWithoutLogin() {
        courier = createCourier(null, "1234", "saske");
        Response response = sendCreateCourierRequest(courier);
        verifyErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Step("Тест: создание курьера без пароля")
    public void createCourierWithoutPassword() {
        courier = createCourier("ninja" + System.currentTimeMillis(), null, "saske");
        Response response = sendCreateCourierRequest(courier);
        verifyErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Step("Тест: создание курьера с уже существующим логином")
    public void createCourierWithDuplicateLogin() {
        String login = "ninja" + System.currentTimeMillis();
        courier = createCourier(login, "1234", "saske");

        // Создаем курьера
        sendCreateCourierRequest(courier);

        // Пытаемся создать второго курьера с тем же логином
        Courier duplicateCourier = createCourier(login, "1234", "saske");
        Response response = sendCreateCourierRequest(duplicateCourier);
        verifyErrorResponse(response, 409, "Этот логин уже используется"); //тут ловим баг
    }
}
