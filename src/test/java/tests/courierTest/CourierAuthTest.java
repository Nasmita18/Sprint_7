package tests.courierTest;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;
import tests.dto.Courier;
import tests.dto.CourierLogin;
import tests.helper.CourierHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierAuthTest {

    private CourierHelper courierHelper;
    private Courier courier;

    @Before
    public void setUp() {
        courierHelper = new CourierHelper();
    }

    @After
    public void tearDown() {
        if (courier != null) {
            try {
                Integer courierId = courierHelper.getCourierId(courier);
                courierHelper.deleteCourier(courierId);
            } catch (Exception e) {
                System.out.println("Ошибка удаления курьера: " + e.getMessage());
            }
        }
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
        courier = courierHelper.createCourier("ninja" + System.currentTimeMillis(), "1234", "saske");
        courierHelper.sendCreateCourierRequest(courier);

        Response response = loginCourier(courier.getLogin(), courier.getPassword());
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
        courier = courierHelper.createCourier("ninja" + System.currentTimeMillis(), "1234", "saske");
        courierHelper.sendCreateCourierRequest(courier);

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
