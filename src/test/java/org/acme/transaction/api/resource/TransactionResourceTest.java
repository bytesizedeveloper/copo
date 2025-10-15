package org.acme.transaction.api.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.acme.common.exception.InsufficientBalanceException;
import org.acme.test_common.base.ObjectMapperBase;
import org.acme.test_common.test_data.TransactionTestData;
import org.acme.test_common.test_data.WalletTestData;
import org.acme.transaction.api.contract.TransactionRequest;
import org.acme.transaction.model.TransactionModel;
import org.acme.transaction.model.enumeration.TransactionType;
import org.acme.transaction.service.TransactionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TransactionResourceTest extends ObjectMapperBase {

    private static final String URL = "/v1/transaction/new";

    @InjectMock
    TransactionService transactionService;

    @Test
    public void testCreate_validRequest_returns201() {
        // Given
        TransactionRequest request = TransactionTestData.getRequest();

        TransactionModel transactionPreInitialise = TransactionTestData.getTransactionPreInitialise();

        TransactionModel transaction = TransactionTestData.getTransactionPostInitialise();

        // When
        Mockito.when(transactionService.createTransfer(transactionPreInitialise)).thenReturn(transaction);

        // Then
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body("hash_id", Matchers.equalTo(TransactionTestData.HASH_ID))
                .body("sender_address", Matchers.equalTo(WalletTestData.ADDRESS_ALPHA))
                .body("recipient_address", Matchers.equalTo(WalletTestData.ADDRESS_BETA))
                .body("amount", Matchers.equalTo(1))
                .body("fee", Matchers.equalTo(1))
                .body("type", Matchers.equalTo(TransactionType.TRANSFER.name()))
                .body("inputs", Matchers.notNullValue())
                .body("outputs", Matchers.notNullValue())
                .body("created_at", Matchers.equalTo(TransactionTestData.NOW.format(DATE_TIME_FORMATTER)))
                .body("signature", Matchers.equalTo(TransactionTestData.SIGNATURE));

        Mockito.verify(transactionService, Mockito.times(1)).createTransfer(transactionPreInitialise);
    }

    @Test
    public void testCreate_illegalStateException_returns500() {
        // Given
        TransactionRequest request = TransactionTestData.getRequest();

        TransactionModel transactionPreInitialise = TransactionTestData.getTransactionPreInitialise();

        // When
        Mockito.when(transactionService.createTransfer(transactionPreInitialise)).thenThrow(IllegalStateException.class);

        // Then
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Mockito.verify(transactionService, Mockito.times(1)).createTransfer(transactionPreInitialise);
    }

    @Test
    public void testCreate_insufficientBalanceException_returns500() {
        // Given
        TransactionRequest request = TransactionTestData.getRequest();

        TransactionModel transactionPreInitialise = TransactionTestData.getTransactionPreInitialise();

        // When
        Mockito.when(transactionService.createTransfer(transactionPreInitialise)).thenThrow(InsufficientBalanceException.class);

        // Then
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        Mockito.verify(transactionService, Mockito.times(1)).createTransfer(transactionPreInitialise);
    }

    @Test
    public void testCreate_runtimeException_returns500() {
        // Given
        TransactionRequest request = TransactionTestData.getRequest();

        TransactionModel transactionPreInitialise = TransactionTestData.getTransactionPreInitialise();

        // When
        Mockito.when(transactionService.createTransfer(transactionPreInitialise)).thenThrow(RuntimeException.class);

        // Then
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        Mockito.verify(transactionService, Mockito.times(1)).createTransfer(transactionPreInitialise);
    }
}
