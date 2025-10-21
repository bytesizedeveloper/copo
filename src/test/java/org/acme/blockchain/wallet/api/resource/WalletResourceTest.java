package org.acme.blockchain.wallet.api.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.acme.blockchain.wallet.model.WalletModel;
import org.acme.blockchain.wallet.service.WalletService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Base64;

import static io.restassured.RestAssured.given;


@QuarkusTest
public class WalletResourceTest {

    private static final String URL = "/v1/wallet/new";

    @InjectMock
    WalletService walletService;

    @Test
    void testCreate_validRequest_returns201() {
        // Given
        WalletModel wallet = WalletTestData.getWallet();

        // When
        Mockito.when(walletService.create()).thenReturn(wallet);

        // Then
        given()
                .when()
                .contentType(ContentType.JSON)
                .post(URL)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(ContentType.JSON)
                .body("address", Matchers.equalTo(WalletTestData.ADDRESS_ALPHA))
                .body("public_key", Matchers.equalTo(Base64.getEncoder().encodeToString(WalletTestData.KEYPAIR_ALPHA.getPublic().getEncoded())));

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    void testCreate_runtimeException_returns500() {
        // When
        Mockito.when(walletService.create()).thenThrow(RuntimeException.class);

        // Then
        given()
                .when()
                .contentType(ContentType.JSON)
                .post(URL)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        Mockito.verify(walletService, Mockito.times(1)).create();
    }
}
