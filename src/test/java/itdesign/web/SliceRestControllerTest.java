package itdesign.web;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import itdesign.entity.Slice;
import itdesign.helper.DataSetLoader;
import itdesign.helper.EntitiesHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static itdesign.helper.EntitiesHelper.newSlice;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SliceRestControllerTest {

    @Autowired
    private DataSource dataSource;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() throws Exception {
        List<DataSetLoader> loaders = Arrays.asList(
            new DataSetLoader("slice", "slices.xml"),
            new DataSetLoader("slice", "rep_groups.xml"),
            new DataSetLoader("slice", "rep_statuses.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.deleteAll(dataSource.getConnection());

        loaders = Arrays.asList(
            new DataSetLoader("slice", "rep_statuses.xml"),
            new DataSetLoader("slice", "rep_groups.xml"),
            new DataSetLoader("slice", "slices.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.cleanAndInsert(dataSource.getConnection());
    }

    @Test
    public void listSlicesMayBeFound() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/";
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
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            body("id", hasSize(greaterThan(0))).
            body("statusId", is(not(hasItems(3)))).
            body("[0].id", is(not(nullValue()))).
            body("[0].period", is(not(nullValue()))).
            body("[0].groupName", is(not(nullValue()))).
            body("[0].statusName", is(not(nullValue()))).
            body("[0].maxRecNum", is(not(nullValue())));
    }

    @Test
    public void existingSliceMayBeFoundById()  {
        Long testedSliceId = 1l;

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
        when().
            get(testedSliceId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().header("access-control-allow-origin", equalTo("*")).
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            and().statusCode(200).
            body("id", is(equalTo(testedSliceId.intValue()))).
            body("groupName", is(equalTo(EntitiesHelper.GROUP_NAME))).
            body("statusName", is(equalTo(EntitiesHelper.STATUS_DEFAULT_NAME + " " + EntitiesHelper.SLICE_START_DATE.getYear()))).
            body("region", is(equalTo(EntitiesHelper.SLICE_REGION))).
            body("maxRecNum", is(equalTo(EntitiesHelper.SLICE_MAX_REC_NUM.intValue()))).
            body("startDate", is(equalTo(EntitiesHelper.SLICE_START_DATE.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))).
            body("endDate", is(equalTo(EntitiesHelper.SLICE_END_DATE.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))).
            body("created", is(equalTo(EntitiesHelper.SLICE_CREATED_DATE.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))));
    }


    @Test
    public void newSliceMayBeCreated() throws JSONException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Slice slice = newSlice(null);
        JSONObject body = new JSONObject();
        body.put("startDate", slice.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        body.put("endDate", slice.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        body.put("maxRecNum", slice.getMaxRecNum().toString());
        body.put("region", slice.getRegion());
        JSONArray arr = new JSONArray();
        arr.put(1);arr.put(2);arr.put(3);
        body.put("groups", arr);

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
            and().header("access-control-allow-origin", equalTo("*")).
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            and().statusCode(201).
            body("id", hasSize(3)).
            body("[0].statusId", is(equalTo(0))).
            body("[1].statusId", is(equalTo(0))).
            body("[2].statusId", is(equalTo(0))).
            body("[0].id", is(not(nullValue()))).
            body("[0].period", is(not(nullValue()))).
            body("[0].groupName", is(not(nullValue()))).
            body("[0].statusName", is(not(nullValue()))).
            body("[0].maxRecNum", is(not(nullValue()))).
            body("[0].created", is(not(nullValue())));
    }

    @Test
    public void existingSliceMayBeRemoved()  {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        //Помечаем запись как удаленную
        Long testedSliceId = 1l;
        given().
            log().all().
        when().
            delete(testedSliceId.toString()).
        then().
            log().all().
            and().header("access-control-allow-origin", equalTo("*")).
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            and().statusCode(204);

        //Проверяем, что фактически запись не удалилась, а статус сменился на Удалено
        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
        when().
            get(testedSliceId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().header("access-control-allow-origin", equalTo("*")).
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            and().statusCode(200).
            body("id", is(equalTo(testedSliceId.intValue()))).
            body("statusId", is(equalTo(EntitiesHelper.STATUS_DELETED_ID.intValue())));
    }


    @Test
    public void maxRecNumMa()  {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/max/";
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
            and().header("access-control-allow-origin", equalTo("*")).
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            and().statusCode(200).
            body("value", is(not(nullValue())));
    }



    @Test
    public void shouldBadRequestWhenInvalidJSON() throws JSONException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        JSONObject body = new JSONObject();
        body.put("startDate", "bla bla bla");

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
            and().header("access-control-allow-origin", equalTo("*")).
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            and().statusCode(400);
    }


    @Test
    public void shouldInternalServerErrorWhenInconsistentData()throws JSONException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Slice slice = newSlice(null);
        JSONObject body = new JSONObject();
        body.put("startDate", slice.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        body.put("endDate", slice.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        body.put("maxRecNum", slice.getMaxRecNum().toString());
        body.put("region", "999"); // slice.getRegion());
        JSONArray arr = new JSONArray();
        arr.put(1);
        body.put("groups", arr);

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
            and().header("access-control-allow-origin", equalTo("*")).
            and().header("access-control-allow-methods", equalTo("*")).
            and().header("access-control-allow-headers", equalTo("*")).
            and().statusCode(500);
    }

}
