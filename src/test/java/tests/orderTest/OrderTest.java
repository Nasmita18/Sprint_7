package tests.orderTest;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.hamcrest.MatcherAssert;
import tests.dto.Order;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderTest {

    private Order order;

    public OrderTest(Order order) {
        this.order = order;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", "4", "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{"BLACK"})},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", "4", "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{"GREY"})},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", "4", "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{"BLACK", "GREY"})},
                {new Order("Naruto", "Uchiha", "Konoha, 142 apt.", "4", "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{})}
        });
    }

    @Before
    public void setUp() {
        // Устанавливаем базовый URI для RestAssured
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Создание заказа")
    private Response createOrder(Order order) {
        // Отправляем POST-запрос для создания заказа
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders")
                .then()
                .extract()
                .response(); // Извлекаем ответ
    }

    @Test
    @Step("Тест: создание заказа с параметрами")
    public void createOrderWithParameters() {
        Response response = createOrder(order);
        response.then().statusCode(201); // Проверяем, что ответ имеет статус 201 Created

        Integer track = response.path("track"); // Извлекаем track из ответа
        MatcherAssert.assertThat(track, notNullValue()); // Проверяем, что track не равен null
    }
}
