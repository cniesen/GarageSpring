package com.niesens.garagespring;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class GarageDoorControllerTest {

    @Value("${local.server.port}")
    private int port = 0;

    private TestRestTemplate testRestTemplate = new TestRestTemplate("user", "garagetest");

    @Test
    public void testAuthenticationFailUnauthorized() {
        ResponseEntity<Garage> entity = new TestRestTemplate("user", "garage").getForEntity(
                "http://localhost:" + this.port + "/garage", Garage.class);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    }

    @Test
    public void testGarage() {
        ResponseEntity<Garage> entity = testRestTemplate.getForEntity(
                "http://localhost:" + this.port + "/garage", Garage.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        Garage garage = entity.getBody();
        Map<Integer, GarageDoor> garageDoors = garage.getDoors();
        assertEquals(2, garageDoors.size());
        assertGarageDoor(1, garageDoors.get(1));
        assertGarageDoor(2, garageDoors.get(2));
    }

    @Test
    public void testGetGarageDoors() throws InvocationTargetException, IllegalAccessException {
        ResponseEntity<Collection> entity = testRestTemplate.getForEntity(
                "http://localhost:" + this.port + "/garageDoors", Collection.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        for (Map garageDoorProperties : (List<Map>) entity.getBody()) {
            GarageDoor garageDoor = new GarageDoor();
            BeanUtils.populate(garageDoor, garageDoorProperties);
            assertGarageDoor(garageDoor.getId(), garageDoor);
        }
    }

    @Test
    public void testGetGarageDoor() {
        ResponseEntity<GarageDoor> entity = testRestTemplate.getForEntity(
                "http://localhost:" + this.port + "/garageDoor/1", GarageDoor.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertGarageDoor(1, entity.getBody());
    }

    @Test
    public void testGetGarageDoorFailNotFound() {
        ResponseEntity<GarageDoor> entity = testRestTemplate.getForEntity(
                "http://localhost:" + this.port + "/garageDoor/3", GarageDoor.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void testActivateOpener() {
        ResponseEntity entity = testRestTemplate.exchange(
                "http://localhost:" + this.port + "/garageDoor/1?action=ACTIVATE_OPENER", HttpMethod.PUT, null, String.class);
        assertEquals(HttpStatus.ACCEPTED, entity.getStatusCode());
    }

    @Test
    public void testActivateOpenerFailNotFound() {
        ResponseEntity entity = testRestTemplate.exchange(
                "http://localhost:" + this.port + "/garageDoor/3?action=ACTIVATE_OPENER", HttpMethod.PUT, null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void testActivateOpenerFailBadRequest() {
        ResponseEntity entity = testRestTemplate.exchange(
                "http://localhost:" + this.port + "/garageDoor/1?action=FAIL", HttpMethod.PUT, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    private void assertGarageDoor(int id, GarageDoor garageDoor) {
        switch(id) {
            case 1 :
                assertEquals(1, garageDoor.getId());
                assertEquals("Double Garage Door", garageDoor.getDescription());
                assertEquals(0, garageDoor.getOpenerSwitchPin());
                assertEquals(1, garageDoor.getClosedSensorPin());
                assertFalse(garageDoor.isClosed());
                break;
            case 2:
                assertEquals(2, garageDoor.getId());
                assertEquals("Single Garage Door", garageDoor.getDescription());
                assertEquals(2, garageDoor.getOpenerSwitchPin());
                assertEquals(3, garageDoor.getClosedSensorPin());
                assertFalse(garageDoor.isClosed());
                break;
            default:
                fail("invalid garage door id " + id);
        }
    }

}
