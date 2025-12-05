package org.acme.blockchain.transaction.api.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.api.contract.ErrorResponse;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.api.contract.TransferRequest;
import org.acme.blockchain.transaction.mapper.TransactionMapper;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.service.TransactionService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Resource that exposes endpoint functionality to clients enabling the initialisation and
 * retrieval of COPO transactions.
 */
@Slf4j
@ApplicationScoped
@Tag(name = "Transaction Management", description = "Operations related to COPO transactions.")
@Path("/v1/transaction")
public class TransactionResource {

    private final TransactionService transactionService;

    @Inject
    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Handles the HTTP POST request to initiate a new COPO fund transfer transaction.
     * <p>
     * This endpoint triggers the generation of a COPO transfer from one COPO wallet to another using the unique
     * addresses assigned. After the transaction is generated it is asynchronously verified internally before being
     * broadcast to network peers who must also confirm the validity of the transaction. The transaction is not
     * officially recognised until it is mined with a block.
     *
     * @param request The validated {@link TransferRequest} containing the sender, recipient, and amount.
     * {@return} The HTTP response containing the {@link TransactionResponse} object if successful (201) or an error response (400/404/500).
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Initialise a transfer", description = "Triggers the generation of a COPO fund transfer from one " +
            "COPO wallet to another using the unique addresses assigned. After the transfer is generated it is " +
            "asynchronously verified internally before being broadcast to network peers who must also confirm the " +
            "validity of the transfer. The transfer is not officially recognised until it is mined with a block.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Transfer created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TransactionResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Response create(@Valid TransferRequest request) {
        try {
            TransactionResponse transactionResponse =
                    TransactionMapper.INSTANCE.modelToResponse(
                            transactionService.create(
                                    TransactionMapper.INSTANCE.requestToModel(request)));

            log.info("{} Successfully initialised transfer.", transactionResponse);

            return Response.status(Response.Status.CREATED).entity(transactionResponse).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed to initialise transfer due to: {}", e.getMessage());

            ErrorResponse message = new ErrorResponse("Failed to initialise transfer due to: " + e.getMessage());

            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(message).build();
        } catch (NotFoundException e) {
            log.info("Failed to retrieve wallet due to address not found: {}", request.senderAddress());

            ErrorResponse message = new ErrorResponse("Sender address not found. Please ensure sender's wallet address is " +
                    "correctly input and try again.");

            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        } catch (Exception e) {
            log.error("Failed to initialise transfer due to an unexpected exception: {}\n", e.getMessage(), e);

            ErrorResponse message = new ErrorResponse("Failed to initialise transfer. Please try again.");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    /**
     * Handles the HTTP GET request to retrieve a transaction.
     * <p>
     * This endpoint triggers the retrieval of a transaction by querying the local database for
     * a hash ID that corresponds to the input parameter.
     *
     * @param hashId The unique hash ID of the transaction to retrieve.
     * @return The HTTP response containing the retrieved {@link TransactionResponse} (200) or an error (400/404/500).
     */
    @GET
    @Path("/{hash_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a transaction", description = "Triggers the retrieval of a transaction by querying " +
            "the local database for a hash ID that corresponds to the input parameter. This endpoint will only retrieve " +
            "a transaction that has been mined by the blockchain.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Transaction retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TransactionResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Response get(@PathParam("hash_id") String hashId) {
        try {
            TransactionResponse response = TransactionMapper.INSTANCE.modelToResponse(
                    transactionService.get(new TransactionHash(hashId))
            );

            return Response.ok().entity(response).build();
        } catch (IllegalArgumentException e) {
            log.info("Failed to retrieve transaction due to invalid hash ID format: {}", hashId);

            ErrorResponse message = new ErrorResponse("Invalid hash ID format. Please ensure transaction hash ID is " +
                    "correctly input and try again.");

            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (NotFoundException e) {
            log.info("Failed to retrieve transaction due to hash ID not found: {}", hashId);

            ErrorResponse message = new ErrorResponse("Hash ID not found. Please ensure transaction hash ID is " +
                    "correctly input and try again.");

            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        } catch (Exception e) {
            log.error("Failed to retrieve transaction due to an unexpected exception: {}\n", e.getMessage(), e);

            ErrorResponse message = new ErrorResponse("Failed to retrieve transaction. Please try again.");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
