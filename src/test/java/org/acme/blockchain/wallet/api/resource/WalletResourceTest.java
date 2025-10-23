package org.acme.blockchain.wallet.api.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.acme.blockchain.common.model.AddressModel;
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

    private static final String URL = "/v1/wallet/";

    @InjectMock
    WalletService walletService;

    @Test
    void testCreate_success_returns201() {
        // Given & When
        WalletModel wallet = WalletTestData.getWalletAlpha();

        Mockito.when(walletService.create()).thenReturn(wallet);

        given()
                .when()
                .contentType(ContentType.JSON)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(ContentType.JSON)
                .body("address", Matchers.equalTo(wallet.address().value()))
                .body("public_key", Matchers.equalTo(Base64.getEncoder().encodeToString(wallet.publicKeyEncoded())))
                .body("created_at", Matchers.notNullValue());

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    void testCreate_addressCollision_returns409() {
        // Given & When
        Mockito.when(walletService.create()).thenThrow(IllegalStateException.class);

        given()
                .when()
                .contentType(ContentType.JSON)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",
                        Matchers.equalTo("Unlucky! Failed to create wallet due to an address collision. Please try again."));

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    void testCreate_exception_returns500() {
        // Given & When
        Mockito.when(walletService.create()).thenThrow(RuntimeException.class);

        given()
                .when()
                .contentType(ContentType.JSON)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",
                        Matchers.equalTo("Failed to create wallet. Please try again."));

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    void testGet_validAddress_returns200() {
        // Given & When
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        WalletModel wallet = WalletTestData.getWalletAlpha();

        Mockito.when(walletService.get(address)).thenReturn(wallet);

        given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + address.value())
                .then()

                // Then
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("address", Matchers.equalTo(wallet.address().value()))
                .body("public_key", Matchers.equalTo(Base64.getEncoder().encodeToString(wallet.publicKeyEncoded())))
                .body("created_at", Matchers.notNullValue());

        Mockito.verify(walletService, Mockito.times(1)).get(address);
    }

    @Test
    void testGet_invalidAddress_returns400() {
        // Given & When
        String address = "COPO_abcdef0123456789";

        given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + address)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",
                        Matchers.equalTo("Invalid address format. Please ensure wallet address is correctly input and try again."));

        Mockito.verify(walletService, Mockito.never()).get(Mockito.any(AddressModel.class));
    }

    @Test
    void testGet_nullAddress_returns400() {
        // Given & When
        String address = null;

        given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + address)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",
                        Matchers.equalTo("Invalid address format. Please ensure wallet address is correctly input and try again."));

        Mockito.verify(walletService, Mockito.never()).get(Mockito.any(AddressModel.class));
    }

    @Test
    void testGet_notFoundAddress_returns404() {
        // Given & When
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        Mockito.when(walletService.get(address)).thenThrow(NotFoundException.class);

        given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + address.value())
                .then()

                // Then
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",
                        Matchers.equalTo("Address not found. Please ensure wallet address is correctly input and try again."));

        Mockito.verify(walletService, Mockito.times(1)).get(address);
    }
    
    @Test
    void testGet_exception_returns500() {
        // Given & When
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        Mockito.when(walletService.get(address)).thenThrow(RuntimeException.class);

        given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + address.value())
                .then()

                // Then
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",
                        Matchers.equalTo("Failed to retrieve wallet. Please try again."));

        Mockito.verify(walletService, Mockito.times(1)).get(address);
    }
}
