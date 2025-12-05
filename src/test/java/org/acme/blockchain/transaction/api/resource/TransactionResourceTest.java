package org.acme.blockchain.transaction.api.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.acme.blockchain.common.api.contract.ErrorResponse;
import org.acme.blockchain.test_common.factory.TransactionHashTestFactory;
import org.acme.blockchain.test_common.factory.TransactionTestFactory;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.api.contract.TransferRequest;
import org.acme.blockchain.transaction.api.contract.UtxoResponse;
import org.acme.blockchain.transaction.mapper.UtxoMapper;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Base64;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TransactionResourceTest {

    private static final String URL = "/v1/transaction/";

    @InjectMock
    TransactionService transactionService;

    @Test
    void testCreate_success_returns201() {
        // Given & When
        TransferRequest request = TransactionTestFactory.getTransferRequest();

        TransferModel transfer = TransactionTestFactory.getTransferModel(
                request.senderAddress(),
                request.recipientAddress(),
                request.amount()
        );

        Mockito.when(transactionService.create(Mockito.any(TransferModel.class))).thenReturn(transfer);

        TransactionResponse response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(TransactionResponse.class);

        Assertions.assertEquals(transfer.getHashId().value(), response.hashId());
        Assertions.assertEquals(transfer.getSenderAddress().value(), response.senderAddress());
        Assertions.assertEquals(transfer.getRecipientAddress().value(), response.recipientAddress());
        Assertions.assertEquals(Base64.getEncoder().encodeToString(transfer.getSenderPublicKeyEncoded()), response.senderPublicKeyEncoded());
        Assertions.assertEquals(transfer.getAmount().value(), response.amount());
        Assertions.assertEquals(transfer.getFee().value(), response.fee());
        Assertions.assertEquals(transfer.getType(), response.type());
        Assertions.assertTrue(verifyUtxos(transfer.getInputs(), response.inputs()));
        Assertions.assertTrue(verifyUtxos(transfer.getOutputs(), response.outputs()));
        Assertions.assertEquals(transfer.getCreatedAt(), response.createdAt());
        Assertions.assertEquals(transfer.getSignature().value(), response.signature());
        Assertions.assertEquals(transfer.getStatus(), response.status());

        Mockito.verify(transactionService, Mockito.times(1)).create(Mockito.any(TransferModel.class));
    }

    @Test
    void testCreate_invalidAddressOrAmount_returns400() {
        // Given & When
        TransferRequest request = TransactionTestFactory.getTransferRequest();

        // When
        Mockito.when(transactionService.create(Mockito.any(TransferModel.class))).thenThrow(new IllegalStateException("Invalid address or amount."));

        ErrorResponse response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Failed to initialise transfer due to: Invalid address or amount.", response.message());

        Mockito.verify(transactionService, Mockito.times(1)).create(Mockito.any(TransferModel.class));
    }

    @Test
    void testCreate_insufficientFunds_returns400() {
        // Given & When
        TransferRequest request = TransactionTestFactory.getTransferRequest();

        // When
        Mockito.when(transactionService.create(Mockito.any(TransferModel.class))).thenThrow(new IllegalStateException("Sender has insufficient balance..."));

        ErrorResponse response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Failed to initialise transfer due to: Sender has insufficient balance...", response.message());

        Mockito.verify(transactionService, Mockito.times(1)).create(Mockito.any(TransferModel.class));
    }

    @Test
    void testCreate_addressNotFound_returns404() {
        // Given & When
        TransferRequest request = TransactionTestFactory.getTransferRequest();

        // When
        Mockito.when(transactionService.create(Mockito.any(TransferModel.class))).thenThrow(NotFoundException.class);

        ErrorResponse response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Sender address not found. Please ensure sender's wallet address is " +
                "correctly input and try again.", response.message());

        Mockito.verify(transactionService, Mockito.times(1)).create(Mockito.any(TransferModel.class));
    }

    @Test
    void testCreate_exception_returns500() {
        // Given & When
        TransferRequest request = TransactionTestFactory.getTransferRequest();

        // When
        Mockito.when(transactionService.create(Mockito.any(TransferModel.class))).thenThrow(RuntimeException.class);

        ErrorResponse response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(request)
                .post(URL)
                .then()

                // Then
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Failed to initialise transfer. Please try again.", response.message());

        Mockito.verify(transactionService, Mockito.times(1)).create(Mockito.any(TransferModel.class));
    }

    @Test
    void testGet_validHash_returns200() {
        // Given & When
        TransferModel transfer = TransactionTestFactory.getTransferModel();

        Mockito.when(transactionService.get(transfer.getHashId())).thenReturn(transfer);

        TransactionResponse response = given()
                .when()
                .get(URL + transfer.getHashId().value())
                .then()

                // Then
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(TransactionResponse.class);

        Assertions.assertEquals(transfer.getHashId().value(), response.hashId());
        Assertions.assertEquals(transfer.getSenderAddress().value(), response.senderAddress());
        Assertions.assertEquals(transfer.getRecipientAddress().value(), response.recipientAddress());
        Assertions.assertEquals(Base64.getEncoder().encodeToString(transfer.getSenderPublicKeyEncoded()), response.senderPublicKeyEncoded());
        Assertions.assertEquals(transfer.getAmount().value(), response.amount());
        Assertions.assertEquals(transfer.getFee().value(), response.fee());
        Assertions.assertEquals(transfer.getType(), response.type());
        Assertions.assertTrue(verifyUtxos(transfer.getInputs(), response.inputs()));
        Assertions.assertTrue(verifyUtxos(transfer.getOutputs(), response.outputs()));
        Assertions.assertEquals(transfer.getCreatedAt(), response.createdAt());
        Assertions.assertEquals(transfer.getSignature().value(), response.signature());
        Assertions.assertEquals(transfer.getStatus(), response.status());

        Mockito.verify(transactionService, Mockito.times(1)).get(transfer.getHashId());
    }

    @Test
    void testGet_invalidHash_returns400() {
        // Given & When
        String hash = "abcdef0123456789";

        ErrorResponse response = given()
                .when()
                .get(URL + hash)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Invalid hash ID format. Please ensure transaction hash ID is " +
                "correctly input and try again.", response.message());
    }

    @Test
    void testGet_nullHash_returns400() {
        // Given & When
        String hash = null;

        ErrorResponse response = given()
                .when()
                .get(URL + hash)
                .then()

                // Then
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Invalid hash ID format. Please ensure transaction hash ID is " +
                "correctly input and try again.", response.message());
    }

    @Test
    void testGet_notFoundHash_returns404() {
        // Given & When
        TransactionHash hash = TransactionHashTestFactory.getTransactionHash();

        Mockito.when(transactionService.get(hash)).thenThrow(NotFoundException.class);

        ErrorResponse response = given()
                .when()
                .get(URL + hash.value())
                .then()

                // Then
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Hash ID not found. Please ensure transaction hash ID is correctly input and try again.", response.message());
    }

    @Test
    void testGet_exception_returns500() {
        // Given & When
        TransactionHash hash = TransactionHashTestFactory.getTransactionHash();

        Mockito.when(transactionService.get(hash)).thenThrow(RuntimeException.class);

        ErrorResponse response = given()
                .when()
                .get(URL + hash.value())
                .then()

                // Then
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);

        Assertions.assertEquals("Failed to retrieve transaction. Please try again.", response.message());
    }

    private boolean verifyUtxos(List<UtxoModel> expected, List<UtxoResponse> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }

        List<UtxoResponse> asModels = expected.stream().map(UtxoMapper.INSTANCE::modelToResponse).toList();
        Assertions.assertEquals(asModels, actual);

        return true;
    }
}
