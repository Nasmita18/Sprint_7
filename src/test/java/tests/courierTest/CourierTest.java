package tests.courierTest;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.dto.Courier;
import tests.helper.CourierHelper;


public class CourierTest {

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

    @Test
    @Step("Тест: успешное создание курьера")
    public void createCourierSuccess() {
        courier = courierHelper.createCourier("ninja" + System.currentTimeMillis(), "1234", "saske");
        Response response = courierHelper.sendCreateCourierRequest(courier);
        courierHelper.verifyCourierCreation(response);
    }

    @Test
    @Step("Тест: создание курьера без логина")
    public void createCourierWithoutLogin() {
        courier = courierHelper.createCourier(null, "1234", "saske");
        Response response = courierHelper.sendCreateCourierRequest(courier);
        courierHelper.verifyErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Step("Тест: создание курьера без пароля")
    public void createCourierWithoutPassword() {
        courier = courierHelper.createCourier("ninja" + System.currentTimeMillis(), null, "saske");
        Response response = courierHelper.sendCreateCourierRequest(courier);
        courierHelper.verifyErrorResponse(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Step("Тест: создание курьера с уже существующим логином")
    public void createCourierWithDuplicateLogin() {
        String login = "ninja" + System.currentTimeMillis();
        courier = courierHelper.createCourier(login, "1234", "saske");

        // Создаем курьера
        courierHelper.sendCreateCourierRequest(courier);

        // Пытаемся создать второго курьера с тем же логином
        Courier duplicateCourier = courierHelper.createCourier(login, "1234", "saske");
        Response response = courierHelper.sendCreateCourierRequest(duplicateCourier);
        courierHelper.verifyErrorResponse(response, 409, "Этот логин уже используется"); //тут ловим баг
    }
}
