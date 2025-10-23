package org.acme.blockchain.wallet.api.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
import org.acme.blockchain.common.model.AddressModel;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.mapper.WalletMapper;
import org.acme.blockchain.wallet.service.WalletService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Resource that exposes endpoint functionality to clients enabling the creation and
 * retrieval of COPO wallets.
 */
@Slf4j
@ApplicationScoped
@Tag(name = "Wallet Management", description = "Operations related to COPO user wallets.")
@Path("/v1/wallet")
public class WalletResource {

    private final WalletService walletService;

    @Inject
    public WalletResource(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Handles the HTTP POST request to create a new COPO wallet.
     * <p>
     * This endpoint triggers the generation of a cryptographic key pair, derives a unique
     * address, and securely persists the private key using the configured master keystore password.
     * Only the public information will be served to the client.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a wallet", description = "Triggers the generation of a cryptographic key pair, " +
            "derives a unique address, and securely persists the private key using the configured master keystore " +
            "password. Only the public information will be served to the client.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Wallet created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = WalletResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "Conflict",
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
    public Response create() {
        try {
            WalletResponse response = WalletMapper.INSTANCE.modelToResponse(walletService.create());

            log.info("Successfully created and persisted new wallet: {}", response.address());

            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (IllegalStateException e) {
            log.error("Failed to create wallet due to an address collision.");

            ErrorResponse message = new ErrorResponse("Unlucky! Failed to create wallet due to an address collision. Please try again.");

            return Response.status(Response.Status.CONFLICT).entity(message).build();
        } catch (Exception e) {
            log.error("Failed to create wallet due to an unexpected exception: {}\n", e.getMessage(), e);

            ErrorResponse message = new ErrorResponse("Failed to create wallet. Please try again.");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    /**
     * Handles the HTTP GET request to retrieve a wallet.
     * <p>
     * This endpoint triggers the retrieval of a local COPO wallet by querying the local database for
     * an address that corresponds to the input parameter. Only the public information will be
     * served to the client.
     */
    @GET
    @Path("/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a wallet", description = "Triggers the retrieval of a local COPO wallet by querying " +
            "the local database for an address that corresponds to the input parameter. Only the public information " +
            "will be served to the client.")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Wallet retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = WalletResponse.class)
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
    public Response get(@PathParam("address") String address) {
        try {
            WalletResponse response = WalletMapper.INSTANCE.modelToResponse(
                    walletService.get(new AddressModel(address))
            );

            return Response.ok().entity(response).build();
        } catch (IllegalArgumentException e) {
            log.info("Failed to retrieve wallet due to invalid address format: {}", address);

            ErrorResponse message = new ErrorResponse("Invalid address format. Please ensure wallet address is " +
                    "correctly input and try again.");

            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (NotFoundException e) {
            log.info("Failed to retrieve wallet due to address not found: {}", address);

            ErrorResponse message = new ErrorResponse("Address not found. Please ensure wallet address is " +
                    "correctly input and try again.");

            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        } catch (Exception e) {
            log.error("Failed to retrieve wallet due to an unexpected exception: {}\n", e.getMessage(), e);

            ErrorResponse message = new ErrorResponse("Failed to retrieve wallet. Please try again.");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
