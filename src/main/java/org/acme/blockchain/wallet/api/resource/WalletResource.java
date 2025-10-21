package org.acme.blockchain.wallet.api.resource;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.mapper.WalletMapper;
import org.acme.blockchain.wallet.service.WalletService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * JAX-RS Resource endpoint for handling all operations related to a cryptocurrency wallet.
 * <p>
 * This class maps HTTP requests to the core business logic handled by the {@link WalletService}.
 * It manages response formatting and exception handling for API consumers.
 */
@Slf4j
@ApplicationScoped
@RegisterForReflection
@Tag(name = "Wallet Management", description = "Operations related to user wallets.")
@Path("/v1/wallet")
public class WalletResource {

    /**
     * Injected service containing the core business logic for wallet creation and management.
     */
    private final WalletService walletService;

    @Inject
    public WalletResource(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Handles the HTTP POST request to create a new wallet.
     * <p>
     * This endpoint triggers the generation of a cryptographic key pair, derives a unique
     * address, and securely persists the private key using the configured master keystore password.
     *
     * @return A JAX-RS {@link Response} object:
     * <ul>
     * <li>**201 CREATED:** On successful wallet creation, containing the {@link WalletResponse} body.</li>
     * <li>**500 INTERNAL_SERVER_ERROR:** If a cryptographic or persistence error occurs during creation
     * (e.g., keystore file access failure, invalid configuration password).</li>
     * </ul>
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/new")
    public Response create() {
        try {
            WalletResponse response = WalletMapper.INSTANCE.modelToResponse(walletService.create());

            log.info("Successfully created and persisted new wallet: {}", response.getAddress());
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            log.error("Failed to create wallet due to an unexpected exception: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
