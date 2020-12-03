package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExampleTests {
    private RequestSpecification requestSpec() {

        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + "CZQPXwzzFgkezRPDv1c8VzX4kKqZiZ9ebfW9QEw5cLHpUTBqvHap7ipTrjW3u0Me")
                .setContentType(ContentType.JSON)
                .setBaseUri("https://api.whisk-dev.com/")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @Order(1)
    public void test_01_Create_Shopping_List() {
        File jsonDataInFile = new File("src/test/resources/qaList.json");
        Response response =
                given()
                        .spec(requestSpec())
                        .body(jsonDataInFile)
                        .when()
                        .post("/list/v2")
                        .then().statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response();
        String listId = response.path("list.id.");
        given()
                .spec(requestSpec())
                .get("/list/v2/" + listId)
                .then().statusCode(HttpStatus.SC_OK)
                .body("list.id.", equalTo(listId))
                .body("content.size()", equalTo(0));
    }

    @Test
    @Order(2)
    public void test_02_Delete_Shopping_List() {
        File jsonDataInFile = new File("src/test/resources/qaList.json");
        Response response =
                given()
                        .spec(requestSpec())
                        .body(jsonDataInFile)
                        .when()
                        .post("/list/v2")
                        .then().statusCode(HttpStatus.SC_OK)
                        .extract()
                        .response();
        String listId = response.path("list.id.");
        given()
                .spec(requestSpec())
                .when()
                .delete("/list/v2/" + listId)
                .then().statusCode(HttpStatus.SC_OK);
        given()
                .spec(requestSpec())
                .when()
                .get("/list/v2/" + listId)
                .then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("code.", equalTo("shoppingList.notFound"));
    }
}
