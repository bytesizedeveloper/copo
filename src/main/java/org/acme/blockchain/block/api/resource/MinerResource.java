package org.acme.blockchain.block.api.resource;

import org.acme.blockchain.block.service.MinerService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.model.AddressModel;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST resource for managing blockchain miner operations.
 * <p>
 * This class provides endpoints to start and stop the mining process associated with a specific wallet address.
 * It is designed for use in a COPO (Consensus of Proof of Ownership) based blockchain system.
 * </p>
 */
@Slf4j
@ApplicationScoped
@RegisterForReflection
@Tag(name = "Miner Management", description = "Operations related to COPO miners.")
@Path("/v1/miner")
public class MinerResource {

    private final MinerService minerService;

    /**
     * Constructs a new MinerResource and injects the necessary service dependency.
     * <p>
     * Quarkus handles this injection automatically due to the {@code @Inject} annotation.
     * </p>
     * @param minerService The service component handling the core mining logic.
     */
    @Inject
    public MinerResource(MinerService minerService) {
        this.minerService = minerService;
    }

    /**
     * Initiates the mining process for the given wallet address.
     *
     * <p>The address is typically the public key hash of the miner's wallet.</p>
     *
     * @param address The wallet address of the miner to start.
     * @return A {@link Response} indicating the status of the start operation:
     * <ul>
     * <li>{@code 202 ACCEPTED}: Mining started successfully.</li>
     * <li>{@code 400 BAD REQUEST}: The address format is invalid or other illegal state.</li>
     * <li>{@code 409 CONFLICT}: Mining is already in progress for this address.</li>
     * <li>{@code 500 INTERNAL SERVER ERROR}: An unexpected server-side error occurred.</li>
     * </ul>
     */
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{address}/start")
    public Response start(@PathParam("address") String address) {
        try {
            if (minerService.startMining(new AddressModel(address))) {
                log.info("{} Successfully started mining.", address);
                return Response.status(Response.Status.ACCEPTED).entity("Successfully started mining.").build();
            } else {
                return Response.status(Response.Status.CONFLICT.getStatusCode()).entity("Failed to start mining as mining in progress.").build();
            }
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("{} Failed to start mining due to an unexpected exception: {}", address, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }

    /**
     * Halts the mining process for the given wallet address.
     *
     * @param address The wallet address of the miner to stop.
     * @return A {@link Response} indicating the status of the stop operation:
     * <ul>
     * <li>{@code 202 ACCEPTED}: Mining stopped successfully.</li>
     * <li>{@code 400 BAD REQUEST}: The address format is invalid or other illegal state.</li>
     * <li>{@code 409 CONFLICT}: Mining was not in progress for this address.</li>
     * <li>{@code 500 INTERNAL SERVER ERROR}: An unexpected server-side error occurred.</li>
     * </ul>
     */
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{address}/stop")
    public Response stop(@PathParam("address") String address) {
        try {
            if (minerService.stopMining(new AddressModel(address))) {
                log.info("{} Successfully stopped mining.", address);
                return Response.status(Response.Status.ACCEPTED).entity("Successfully stopped mining.").build();
            } else {
                return Response.status(Response.Status.CONFLICT.getStatusCode()).entity("Failed to stop mining as mining not in progress.").build();
            }
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("{} Failed to stop mining due to an unexpected exception: {}", address, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }
}
