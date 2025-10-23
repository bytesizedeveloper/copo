package org.acme.blockchain.common.api.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Builder(toBuilder = true)
@Schema(description = "Message indicating error attributed to the failure of client request.")
public record ErrorResponse(

        @JsonProperty("message")
        @Schema(description = "Error message to describe failure", examples = "Some failure")
        String message
) {}
