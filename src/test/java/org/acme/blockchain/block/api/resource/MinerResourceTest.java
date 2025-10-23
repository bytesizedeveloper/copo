package org.acme.blockchain.block.api.resource;

import org.acme.blockchain.block.service.MinerService;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MinerResourceTest {

    private static final String URL_START = "/v1/miner/" + WalletTestData.ADDRESS_ALPHA.value() + "/start";

    private static final String URL_STOP = "/v1/miner/" + WalletTestData.ADDRESS_ALPHA.value() + "/stop";

    @InjectMock
    MinerService minerService;

    @Test
    void testStart_returns202() {
        // When
        Mockito.when(minerService.startMining(WalletTestData.ADDRESS_ALPHA)).thenReturn(true);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.ACCEPTED.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Successfully started mining."));

        Mockito.verify(minerService, Mockito.times(1)).startMining(WalletTestData.ADDRESS_ALPHA);
    }

    @Test
    void testStart_alreadyMining_returns409() {
        // When
        Mockito.when(minerService.startMining(WalletTestData.ADDRESS_ALPHA)).thenReturn(false);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Failed to start mining as mining in progress."));

        Mockito.verify(minerService, Mockito.times(1)).startMining(WalletTestData.ADDRESS_ALPHA);
    }

    @Test
    void testStart_invalidAddress_returns400() {
        // When
        Mockito.when(minerService.startMining(WalletTestData.ADDRESS_ALPHA)).thenThrow(IllegalStateException.class);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).startMining(WalletTestData.ADDRESS_ALPHA);
    }

    @Test
    void testStart_runtimeException_returns500() {
        // When
        Mockito.when(minerService.startMining(WalletTestData.ADDRESS_ALPHA)).thenThrow(RuntimeException.class);

        // Then
        given()
                .when()
                .post(URL_START)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).startMining(WalletTestData.ADDRESS_ALPHA);
    }

    @Test
    void testStop_returns202() {
        // When
        Mockito.when(minerService.stopMining(WalletTestData.ADDRESS_ALPHA)).thenReturn(true);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.ACCEPTED.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Successfully stopped mining."));

        Mockito.verify(minerService, Mockito.times(1)).stopMining(WalletTestData.ADDRESS_ALPHA);
    }

    @Test
    void testStop_notMining_returns409() {
        // When
        Mockito.when(minerService.stopMining(WalletTestData.ADDRESS_ALPHA)).thenReturn(false);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .contentType(ContentType.TEXT)
                .body(Matchers.equalTo("Failed to stop mining as mining not in progress."));

        Mockito.verify(minerService, Mockito.times(1)).stopMining(WalletTestData.ADDRESS_ALPHA);
    }

    @Test
    void testStop_invalidAddress_returns400() {
        // When
        Mockito.when(minerService.stopMining(WalletTestData.ADDRESS_ALPHA)).thenThrow(IllegalStateException.class);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).stopMining(WalletTestData.ADDRESS_ALPHA);
    }

    @Test
    void testStop_runtimeException_returns500() {
        // When
        Mockito.when(minerService.stopMining(WalletTestData.ADDRESS_ALPHA)).thenThrow(RuntimeException.class);

        // Then
        given()
                .when()
                .post(URL_STOP)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        Mockito.verify(minerService, Mockito.times(1)).stopMining(WalletTestData.ADDRESS_ALPHA);
    }
}
