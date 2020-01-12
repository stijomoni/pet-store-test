package com.petstore.services.test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetStoreTest {

    public static long id;
    Response response;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        String requestPayload ="{\n" +
                "  \"id\": 0,\n" +
                "  \"category\": {\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"string\"\n" +
                "  },\n" +
                "  \"name\": \"tiger\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";

     response=   given()
                  .body(requestPayload)
                    .contentType(ContentType.JSON)
                    .log().all().
                when()
                    .post("/pet").
                then()
                    .log().all()
                    .statusCode(200)
                    .extract().response();
        id= response.then().extract().path("id");

    }

    @Test
    public void verify_createAndReturnNewPet_successfully(){
        response.
                then()
                    .body("name", equalTo("tiger"))
                    .body("photoUrls", hasItem("string"))
                    .body("id",notNullValue());
    }

    @Test
    public void verify_updateAndReturnRecord_successfully() {
        given()
                .pathParam("petId", id)
                .formParam("petId", id)
                .formParam("name", "cute_tiger")
                .formParam("status", "not available").log().all().
        when()
                .post("/pet/{petId}").
        then()
                .log().all()
                .statusCode(200);

        given()
                .pathParam("petId", id).
        when()
                .get("/pet/{petId}").
        then()
                .log().all()
                .statusCode(200)
                .body("name", equalTo("cute_tiger"));
    }

    @Test
    public void verify_deletePet_successfully(){
       //delete pet
        given()
                .pathParam("petId",id).
        when()
                .delete("/pet/{petId}").
        then()
                .statusCode(200);
        //check the pet does not exist
        given()
                .pathParam("petId",id).
        when()
                .get("/pet/{petId}").
        then()
                .statusCode(404);

    }


}