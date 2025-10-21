package org.acme.blockchain.common.utility;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HexFormat;

/**
 * A utility class for generating cryptographic hashes essential for blockchain integrity and security.
 * <p>
 * It provides methods for standard hashing algorithms like SHA-256, as well as modern,
 * high-performance algorithms like BLAKE2b, leveraging the Bouncy Castle provider.
 * This class is designed to be immutable and non-instantiable, focusing only on static
 * helper methods.
 * <p>
 * In a blockchain context, these methods are vital for:
 * <ul>
 * <li>Calculating block hashes (Proof-of-Work).</li>
 * <li>Creating transaction IDs.</li>
 * <li>Generating Merkle tree roots.</li>
 * </ul>
 */
public final class HashUtility {

    private static final String SHA_256_HASH_ALGORITHM = "SHA-256";

    private static final String BLAKE2B_HASH_ALGORITHM = "BLAKE2b-256";

    private static final String BC_PROVIDER = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Calculates the **Double-SHA-256** hash (SHA-256d) of a string, used
     * for block and transaction hashing.
     * <p>
     * The input string is first encoded using {@code UTF-8}.
     *
     * @param input The string data to be double-hashed. Must not be null.
     * @return The 256-bit SHA-256d hash as a hexadecimal {@code String} (64 characters).
     * @throws IllegalArgumentException if the input string is null.
     */
    public static String calculateSHA256d(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        byte[] hash = calculateSHA256(input.getBytes(StandardCharsets.UTF_8));
        byte[] hash2 = calculateSHA256(hash);
        return bytesToHex(hash2);
    }

    /**
     * Calculates the SHA-256 hash of a string and returns the result as a hexadecimal string.
     * <p>
     * This method is a convenience wrapper that first encodes the input string into a byte array
     * using {@code UTF-8} and then delegates to the byte array-based hashing method.
     *
     * @param input The string to be hashed (e.g., a transaction payload). Must not be null.
     * @return The 256-bit SHA-256 hash as a hexadecimal {@code String} (64 characters).
     * @throws IllegalArgumentException if the input string is null.
     */
    public static String calculateSHA256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        byte[] hash = calculateSHA256(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * Calculates the SHA-256 hash of a raw byte array.
     * <p>
     * This is the core hashing method, typically used when processing binary data
     * (e.g., serialized objects or the output of other hashes).
     *
     * @param input The byte array to be hashed. Must not be null.
     * @return The 256-bit SHA-256 hash as a 32-byte array.
     * @throws IllegalArgumentException if the input byte array is null.
     * @throws IllegalStateException if the {@code SHA-256} algorithm is unexpectedly unavailable at runtime.
     */
    public static byte[] calculateSHA256(byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input byte array cannot be null.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256_HASH_ALGORITHM);
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(SHA_256_HASH_ALGORITHM + " algorithm unexpectedly unavailable at runtime.", e);
        }
    }

    /**
     * Calculates the BLAKE2b hash of a byte array, resulting in a **256-bit (32-byte)** digest.
     * <p>
     *
     * @param input The byte array to be hashed (e.g., wallet address).
     * Must not be null.
     * @return The 256-bit BLAKE2b hash as a 32-byte array.
     * @throws IllegalArgumentException if the input byte array is null.
     * @throws IllegalStateException if the Bouncy Castle provider or the underlying
     * {@code BLAKE2b-256} algorithm is unavailable at runtime, indicating a
     * misconfigured security environment.
     */
    public static byte[] calculateBLAKE2b256(byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input byte array cannot be null.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(BLAKE2B_HASH_ALGORITHM, BC_PROVIDER);
            return digest.digest(input);
        } catch (NoSuchAlgorithmException | java.security.NoSuchProviderException e) {
            throw new IllegalStateException(BLAKE2B_HASH_ALGORITHM
                    + " algorithm is unavailable, likely because the Bouncy Castle provider is not correctly configured.", e);
        }
    }

    /**
     * Converts a raw hash byte array into its standard **lowercase hexadecimal string** representation.
     * This is the typical final step before a hash is logged, displayed, or serialized into a JSON block/transaction.
     *
     * @param hash The byte array hash (e.g., the output of {@code calculateSHA256(byte[])}). Must not be null.
     * @return The lowercase hexadecimal string representation of the hash.
     * @throws IllegalArgumentException if the input hash array is null.
     */
    public static String bytesToHex(byte[] hash) {
        if (hash == null) {
            throw new IllegalArgumentException("Byte array input for conversion cannot be null.");
        }
        return HexFormat.of().formatHex(hash).toLowerCase();
    }

    /**
     * Converts a hexadecimal string back into a raw hash byte array.
     * This is essential for deserializing received blockchain data (e.g., validating a block hash or transaction ID).
     *
     * @param hex The hexadecimal string. Must not be null and must be valid hex.
     * @return The original byte array.
     * @throws IllegalArgumentException if the input string is null or contains non-hexadecimal characters.
     */
    public static byte[] hexToBytes(String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("Hex string input for conversion cannot be null.");
        }
        return HexFormat.of().parseHex(hex);
    }
}
