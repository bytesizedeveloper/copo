package org.acme.blockchain.transaction.api.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Schema(description = "Information of a transaction.")
public record TransactionResponse(

        @JsonProperty("hash_id")
        @Schema(description = "Unique hash ID of the transaction", examples = "abcdaf0123456789abcdef0123456789abcdef0123456789abcdef0123456789")
        String hashId,

        @JsonProperty("sender_address")
        @Schema(description = "Address of the sender's wallet", examples = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789")
        String senderAddress,

        @JsonProperty("recipient_address")
        @Schema(description = "Address of the recipient's wallet", examples = "COPO_0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
        String recipientAddress,

        @JsonProperty("sender_public_key")
        @Schema(description = "Encoded public key of the sender's wallet", examples = "MIIKMjALBglghkgBZQMEAxMDggohAHLTzl2pjEba3IZjKCH/tUzb6U2i3T6P/WzuHNRMRYjR3grrd2oGNSU03x2Ns3dif0znEG9TswGUThOKEw9WbpDNHQnSyd5eUjWAxtI8mTvu/...[768 characters omitted]...w/fEmb8fVM002EYQ6eth7JJODbuMSrPCdK1BT/z6NGwMevEJA52q+vyeEQxhAGDZ27v1slacgKxTIVINuFJDXKl4WF99EQTMNy+9lm4FWDWcJtCbf9TpfacDwZCn/Pf/zJvzIA==")
        String senderPublicKeyEncoded,

        @JsonProperty("amount")
        @Schema(description = "Quantity of COPO to transfer from sender's to recipient's wallet", examples = "100")
        BigDecimal amount,

        @JsonProperty("fee")
        @Schema(description = "Quantity of COPO required to facilitate the transaction", examples = "100")
        BigDecimal fee,

        @JsonProperty("type")
        @Schema(description = "Type of transaction")
        TransactionType type,

        @JsonProperty("inputs")
        @Schema(description = "Inputs (UTXOs) required to fund transaction")
        List<UtxoResponse> inputs,

        @JsonProperty("outputs")
        @Schema(description = "Outputs (UTXOs) generated as result of transaction")
        List<UtxoResponse> outputs,

        @JsonProperty("created_at")
        @Schema(description = "Timestamp at which the transaction was created")
        OffsetDateTime createdAt,

        @JsonProperty("signature")
        @Schema(description = "Signature of sender to guarantee authenticity, encoded in hexadecimal", examples = "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789...[9126 characters omitted]...0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
        String signature,

        @JsonProperty("status")
        @Schema(description = "Status of transaction")
        TransactionStatus status
) {}
