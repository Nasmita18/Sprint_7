package tests.orderTest;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderListTest {

    @Before
    public void setUp() {
        // Устанавливаем базовый URI
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Получение списка заказов")
    private Response getOrderList() {
        // Отправляем GET-запрос для получения списка заказов
        return given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders")
                .then()
                .extract()
                .response(); // Извлекаем ответ
    }

    @Test
    @Step("Тест: получение списка заказов")
    public void checkOrderList() {
        Response response = getOrderList();
        response.then().statusCode(200); // Проверяем, что ответ имеет статус 200 OK

        String orders = response.path("orders").toString(); // Извлекаем список заказов из ответа
        MatcherAssert.assertThat(orders, notNullValue()); // Проверяем, что список заказов не равен null
    }
}
