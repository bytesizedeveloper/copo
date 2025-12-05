package org.acme.blockchain.wallet.api.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
@Schema(description = "Public information of a wallet.")
public record WalletResponse(

        @JsonProperty("address")
        @Schema(description = "Address of the wallet", examples = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789")
        String address,

        @JsonProperty("public_key")
        @Schema(description = "Encoded public key of the wallet", examples = "MIIKMjALBglghkgBZQMEAxMDggohAHLTzl2pjEba3IZjKCH/tUzb6U2i3T6P/WzuHNRMRYjR3grrd2oGNSU03x2Ns3dif0znEG9TswGUThOKEw9WbpDNHQnSyd5eUjWAxtI8mTvu/...[768 characters omitted]...w/fEmb8fVM002EYQ6eth7JJODbuMSrPCdK1BT/z6NGwMevEJA52q+vyeEQxhAGDZ27v1slacgKxTIVINuFJDXKl4WF99EQTMNy+9lm4FWDWcJtCbf9TpfacDwZCn/Pf/zJvzIA==")
        byte[] publicKeyEncoded,

        @JsonProperty("created_at")
        @Schema(description = "Timestamp at which the wallet was created")
        OffsetDateTime createdAt
) {}
