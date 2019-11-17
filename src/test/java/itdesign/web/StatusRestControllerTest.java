package itdesign.web;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import itdesign.entity.Status;
import itdesign.helper.DataSetLoader;
import itdesign.helper.EntitiesHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static itdesign.helper.EntitiesHelper.newStatus;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StatusRestControllerTest {

    @Autowired
    private DataSource dataSource;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() throws Exception {
        List<DataSetLoader> loaders = Arrays.asList(
            new DataSetLoader("slice", "slices.xml"),
            new DataSetLoader("slice", "rep_statuses.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.deleteAll(dataSource.getConnection());

        loaders = Arrays.asList(
            new DataSetLoader("slice", "rep_statuses.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.cleanAndInsert(dataSource.getConnection());
    }

    @Test
    public void listStatusesMayBeFound() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/ru/slices/statuses/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
        when().
            get().
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().statusCode(200).
            and().header("access-control-allow-origin", equalTo("*")).
            body("id", hasSize(greaterThan(0))).
            body("[0].id", is(not(nullValue()))).
            body("[0].code", is(not(nullValue()))).
            body("[0].name", is(not(nullValue())));
    }

    @Test
    public void shouldNotFoundWhenTryFoundStatusById() throws Exception {
        Long testedStatusId = 1l;

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/ru/slices/statuses/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
        when().
            get(testedStatusId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().statusCode(404);
    }

    @Test
    public void shouldMethodNotAllowedWhenTryCreateStatus() throws JSONException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/ru/slices/statuses/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Status newStatus = newStatus(null);
        JSONObject body = new JSONObject();
        body.put("name", newStatus.getName());

        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
            body(body.toString()).
        when().
            post().
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().statusCode(405);
    }

    @Test
    public void shouldMethodNotAllowedWhenTryChangeStatus() throws JSONException {
        Long testedStatusId = 1l;

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/ru/slices/statuses/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Status newStatus = newStatus(1l);
        JSONObject body = new JSONObject();
        body.put("id", newStatus.getId());
        body.put("name", newStatus.getName());

        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
            body(body.toString()).
        when().
            put(testedStatusId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().statusCode(404);
    }

    @Test
    public void shouldMethodNotAllowedWhenTryRemoveStatus() {
        Long testedStatusId = 1l;

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/ru/slices/statuses/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        given().
            log().all().
        when().
            delete(testedStatusId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().statusCode(404);
    }
}
