package itdesign.web;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import itdesign.entity.Group;
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
import static itdesign.helper.EntitiesHelper.newGroup;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GroupRestControllerTest {

    @Autowired
    private DataSource dataSource;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() throws Exception {
        List<DataSetLoader> loaders = Arrays.asList(
            new DataSetLoader("slice", "slices.xml"),
            new DataSetLoader("slice", "rep_groups.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.deleteAll(dataSource.getConnection());

        loaders = Arrays.asList(
            new DataSetLoader("slice", "rep_groups.xml")
        );

        for (DataSetLoader loader : loaders)
            loader.cleanAndInsert(dataSource.getConnection());
    }

    @Test
    public void listStatusesMayBeFound() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/groups/";
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
            body("[0].name", is(not(nullValue())));
    }

    @Test
    public void existingStatusMayBeFoundById() throws Exception {
        Long testedGroupId = 1l;

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/groups/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
        when().
            get(testedGroupId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().header("access-control-allow-origin", equalTo("*")).
            and().statusCode(200).
            body("id", is(equalTo(testedGroupId.intValue()))).
            body("name", is(equalTo(EntitiesHelper.GROUP_NAME)));
    }

    @Test
    public void shouldMethodNotAllowedWhenTryCreateStatus() throws JSONException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/groups/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Group newGroup = newGroup(null);
        JSONObject body = new JSONObject();
        body.put("name", newGroup.getName());

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
    public void shouldMethodNotAllowedWhenTryChangeGroup() throws JSONException {
        Long testedGroupId = 1l;

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/groups/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        Group newGroup = newGroup(testedGroupId);
        JSONObject body = new JSONObject();
        body.put("id", newGroup.getId());
        body.put("name", newGroup.getName());

        given().
            log().all().
            accept("application/json;charset=utf-8").
            contentType("application/json;charset=utf-8").
            body(body.toString()).
        when().
            put(testedGroupId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().statusCode(405);
    }

    @Test
    public void shouldMethodNotAllowedWhenTryRemoveGroup() {
        Long testedGroupId = 1l;

        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1/slices/groups/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        given().
            log().all().
        when().
            delete(testedGroupId.toString()).
        then().
            log().all().
            contentType(ContentType.fromContentType("application/json;charset=utf-8")).
            and().statusCode(405);
    }
}
