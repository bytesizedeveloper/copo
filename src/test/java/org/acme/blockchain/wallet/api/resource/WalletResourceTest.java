package org.acme.blockchain.wallet.api.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.acme.blockchain.common.api.contract.ErrorResponse;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.test_common.factory.AddressTestFactory;
import org.acme.blockchain.test_common.factory.WalletTestFactory;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.model.WalletModel;
import org.acme.blockchain.wallet.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class WalletResourceTest {

    private static final String URL = "/v1/wallet/";

    @InjectMock
    WalletService walletService;

    @Test
    void testCreate_success_returns201() {
        // Given & When
        WalletModel wallet = WalletTestFactory.getWalletModel();

        Mockito.when(walletService.create()).thenReturn(wallet);

        WalletResponse response = given()
                .when()
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(WalletResponse.class);

        Assertions.assertEquals(wallet.address().value(), response.address());
        Assertions.assertArrayEquals(wallet.publicKeyEncoded(), response.publicKeyEncoded());
        Assertions.assertEquals(wallet.createdAt(), response.createdAt());

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    void testCreate_addressCollision_returns409() {
        // Given & When
        Mockito.when(walletService.create()).thenThrow(IllegalStateException.class);

        ErrorResponse response = given()
                .when()
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Unlucky! Failed to create wallet due to an address collision. Please try again.", response.message());

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    void testCreate_exception_returns500() {
        // Given & When
        Mockito.when(walletService.create()).thenThrow(RuntimeException.class);

        ErrorResponse response = given()
                .when()
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Failed to create wallet. Please try again.", response.message());

        Mockito.verify(walletService, Mockito.times(1)).create();
    }

    @Test
    void testGet_validAddress_returns200() {
        // Given & When
        WalletModel wallet = WalletTestFactory.getWalletModel();
        Mockito.when(walletService.get(wallet.address())).thenReturn(wallet);

        WalletResponse response = given()
                .when()
                .get(URL + wallet.address().value())
                .then()

                // Then
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(WalletResponse.class);

        Assertions.assertEquals(wallet.address().value(), response.address());
        Assertions.assertArrayEquals(wallet.publicKeyEncoded(), response.publicKeyEncoded());
        Assertions.assertEquals(wallet.createdAt(), response.createdAt());

        Mockito.verify(walletService, Mockito.times(1)).get(wallet.address());
    }

    @Test
    void testGet_invalidAddress_returns400() {
        // Given & When
        String address = "COPO_abcdef0123456789";

        ErrorResponse response = given()
                .when()
                .get(URL + address)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

                Assertions.assertEquals("Invalid address format. Please ensure wallet address is correctly input and try again.", response.message());

        Mockito.verify(walletService, Mockito.never()).get(Mockito.any(Address.class));
    }

    @Test
    void testGet_nullAddress_returns400() {
        // Given & When
        String address = null;

        ErrorResponse response = given()
                .when()
                .get(URL + address)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Invalid address format. Please ensure wallet address is correctly input and try again.", response.message());

        Mockito.verify(walletService, Mockito.never()).get(Mockito.any(Address.class));
    }

    @Test
    void testGet_notFoundAddress_returns404() {
        // Given & When
        Address address = AddressTestFactory.getAddress();

        Mockito.when(walletService.get(address)).thenThrow(NotFoundException.class);

        ErrorResponse response = given()
                .when()
                .get(URL + address.value())
                .then()

                // Then
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Address not found. Please ensure wallet address is correctly input and try again.", response.message());

        Mockito.verify(walletService, Mockito.times(1)).get(address);
    }
    
    @Test
    void testGet_exception_returns500() {
        // Given & When
        Address address = AddressTestFactory.getAddress();

        Mockito.when(walletService.get(address)).thenThrow(RuntimeException.class);

        ErrorResponse response = given()
                .when()
                .get(URL + address.value())
                .then()

                // Then
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Failed to retrieve wallet. Please try again.", response.message());

        Mockito.verify(walletService, Mockito.times(1)).get(address);
    }
}
