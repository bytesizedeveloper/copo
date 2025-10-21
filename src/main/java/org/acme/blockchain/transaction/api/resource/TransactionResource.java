package org.acme.blockchain.transaction.api.resource;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.exception.InsufficientBalanceException;
import org.acme.blockchain.transaction.api.contract.TransactionRequest;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.mapper.TransactionMapper;
import org.acme.blockchain.transaction.service.TransactionService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * JAX-RS Resource endpoint for managing transaction operations, specifically fund transfers.
 * <p>
 * This class handles incoming HTTP requests, validates the {@link TransactionRequest} payload,
 * delegates the core business logic to the {@link TransactionService}, and maps exceptions
 * to appropriate HTTP status codes for API consumers.
 */
@Slf4j
@ApplicationScoped
@RegisterForReflection
@Tag(name = "Transaction Management", description = "Operations related to COPO transactions.")
@Path("/v1/transaction")
public class TransactionResource {

    private final TransactionService transactionService;

    /**
     * Constructs the TransactionResource, injecting the core business logic service.
     *
     * @param transactionService The service responsible for creating, validating, and signing transactions.
     */
    @Inject
    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Handles the HTTP POST request to initiate a new fund transfer transaction.
     * <p>
     * The request payload is validated automatically by the container using {@code @Valid}.
     * The process involves mapping the request to a model, creating the transaction via the
     * service, and converting the resulting model back to a response object.
     *
     * @param request The validated {@link TransactionRequest} containing the sender, recipient, and amount.
     * @return A JAX-RS {@link Response} object:
     * <ul>
     * <li>**201 CREATED:** On successful transaction creation, containing the {@link TransactionResponse}.</li>
     * <li>**400 BAD_REQUEST:** If the transaction is invalid (e.g., negative amount, {@link InsufficientBalanceException}, or general {@link IllegalStateException}).</li>
     * <li>**500 INTERNAL_SERVER_ERROR:** For unexpected issues like database failure or cryptographic errors.</li>
     * </ul>
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/new")
    public Response create(@Valid TransactionRequest request) {
        try {
            log.info("{} Received request to create transaction.", request);

            TransactionResponse transactionResponse =
                    TransactionMapper.INSTANCE.modelToResponse(
                            transactionService.createTransfer(
                                    TransactionMapper.INSTANCE.requestToModel(request)));

            log.info("{} Successfully created transaction.", transactionResponse);
            return Response.status(Response.Status.CREATED).entity(transactionResponse).build();

        } catch (IllegalStateException | InsufficientBalanceException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.error("{} Failed to create transaction due to an unexpected exception: {}", request, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }
}
