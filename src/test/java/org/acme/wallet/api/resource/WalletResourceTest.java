package org.acme.wallet.api.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.acme.test_utility.WalletTestUtility;
import org.acme.wallet.model.WalletModel;
import org.acme.wallet.service.WalletService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;


@QuarkusTest
public class WalletResourceTest {

    private static final String URL = "/v1/wallet/new";

    @InjectMock
    WalletService walletService;

    @Test
    public void testCreate_validRequest_returns201() {
        WalletModel wallet = WalletTestUtility.wallet;

        String expectedAddress = WalletTestUtility.address;

        Mockito.when(walletService.create()).thenReturn(wallet);

        given()
                .when()
                .contentType(ContentType.JSON)
                .post(URL)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body("address", Matchers.equalTo(expectedAddress))
                .body("public_key", Matchers.notNullValue());

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    public void testCreate_exception_returns500() {
        Mockito.when(walletService.create()).thenThrow(RuntimeException.class);

        given()
                .when()
                .contentType(ContentType.JSON)
                .post(URL)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        Mockito.verify(walletService, Mockito.times(1)).create();
    }
}
