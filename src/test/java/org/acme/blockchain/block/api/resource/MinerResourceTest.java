package org.acme.blockchain.block.api.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.acme.blockchain.block.service.MinerService;
import org.acme.blockchain.common.model.Address;
import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MinerResourceTest {
    
    private static final Address ADDRESS = Instancio.create(Address.class);

    private static final String URL_START = "/v1/miner/" + ADDRESS.value() + "/start";

    private static final String URL_STOP = "/v1/miner/" + ADDRESS.value() + "/stop";

    @InjectMock
    MinerService minerService;

    @Test
    void testStart_returns202() {
        // When
        Mockito.when(minerService.startMining(ADDRESS)).thenReturn(true);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.ACCEPTED.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Successfully started mining."));

        Mockito.verify(minerService, Mockito.times(1)).startMining(ADDRESS);
    }

    @Test
    void testStart_alreadyMining_returns409() {
        // When
        Mockito.when(minerService.startMining(ADDRESS)).thenReturn(false);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Failed to start mining as mining in progress."));

        Mockito.verify(minerService, Mockito.times(1)).startMining(ADDRESS);
    }

    @Test
    void testStart_invalidAddress_returns400() {
        // When
        Mockito.when(minerService.startMining(ADDRESS)).thenThrow(IllegalStateException.class);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).startMining(ADDRESS);
    }

    @Test
    void testStart_runtimeException_returns500() {
        // When
        Mockito.when(minerService.startMining(ADDRESS)).thenThrow(RuntimeException.class);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).startMining(ADDRESS);
    }

    @Test
    void testStop_returns202() {
        // When
        Mockito.when(minerService.stopMining(ADDRESS)).thenReturn(true);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.ACCEPTED.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Successfully stopped mining."));

        Mockito.verify(minerService, Mockito.times(1)).stopMining(ADDRESS);
    }

    @Test
    void testStop_notMining_returns409() {
        // When
        Mockito.when(minerService.stopMining(ADDRESS)).thenReturn(false);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Failed to stop mining as mining not in progress."));

        Mockito.verify(minerService, Mockito.times(1)).stopMining(ADDRESS);
    }

    @Test
    void testStop_invalidAddress_returns400() {
        // When
        Mockito.when(minerService.stopMining(ADDRESS)).thenThrow(IllegalStateException.class);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).stopMining(ADDRESS);
    }

    @Test
    void testStop_runtimeException_returns500() {
        // When
        Mockito.when(minerService.stopMining(ADDRESS)).thenThrow(RuntimeException.class);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).stopMining(ADDRESS);
    }
}
