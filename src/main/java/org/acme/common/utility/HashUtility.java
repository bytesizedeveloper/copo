package org.acme.common.utility;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HexFormat;

/**
 * A utility class for generating cryptographic hashes using standard and modern algorithms.
 * It provides convenient, reusable methods for creating SHA-256 and BLAKE2b digests,
 * relying on the Bouncy Castle provider for advanced algorithms like BLAKE2b.
 * <p>
 * All core hashing methods return the raw hash as a byte array to facilitate cryptographic
 * chaining (e.g., hash(hash(data))).
 */
public final class HashUtility {

    private static final String SHA_256_HASH_ALGORITHM = "SHA-256";

    private static final String BLAKE2B_256_HASH_ALGORITHM = "BLAKE2b-256";

    private static final String BC_PROVIDER = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Calculates the SHA-256 hash of a string.
     * <p>
     * This method is a convenience wrapper that first encodes the input string into a byte array
     * using {@code UTF-8} and then delegates to the byte array-based hashing method.
     *
     * @param input The string to be hashed. Must not be null.
     * @return The 256-bit SHA-256 hash as a 32-byte array.
     * @throws IllegalArgumentException if the input string is null.
     */
    public static byte[] calculateSHA256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        return calculateSHA256(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Calculates the SHA-256 hash of a byte array.
     * <p>
     * This method relies on a universally available JCA provider for SHA-256.
     *
     * @param input The byte array to be hashed.
     * @return The 256-bit SHA-256 hash as a 32-byte array.
     * @throws IllegalArgumentException if the input byte array is null.
     * @throws IllegalStateException if the SHA-256 algorithm becomes unexpectedly unavailable at runtime.
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
     * Calculates the BLAKE2b hash (256-bit) of a string.
     * <p>
     * The input string is first encoded into a byte array using {@code UTF-8}. This method
     * requires the Bouncy Castle provider to be available.
     *
     * @param input The string to be hashed. Must not be null.
     * @return The 256-bit BLAKE2b hash as a 32-byte array.
     * @throws IllegalArgumentException if the input string is null.
     */
    public static byte[] calculateBLAKE2b256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        return calculateBLAKE2b256(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Calculates the BLAKE2b hash (256-bit) of a byte array.
     * <p>
     * This highly efficient hashing method is specifically requested from the Bouncy Castle (BC) provider.
     *
     * @param input The byte array to be hashed.
     * @return The 256-bit BLAKE2b hash as a 32-byte array.
     * @throws IllegalArgumentException if the input byte array is null.
     * @throws IllegalStateException if the Bouncy Castle provider or the BLAKE2b algorithm is unavailable at runtime.
     */
    public static byte[] calculateBLAKE2b256(byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input byte array cannot be null.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(BLAKE2B_256_HASH_ALGORITHM, BC_PROVIDER);
            return digest.digest(input);
        } catch (NoSuchAlgorithmException | java.security.NoSuchProviderException e) {
            throw new IllegalStateException(BLAKE2B_256_HASH_ALGORITHM + " algorithm is unavailable, likely because the Bouncy Castle provider is not correctly configured.", e);
        }
    }

    /**
     * Converts a byte array into its hexadecimal string representation (lowercase).
     * This is typically used to format a raw hash for display or serialization.
     *
     * @param hash The byte array hash (e.g., the output of a hashing method).
     * @return The hexadecimal string representation of the hash.
     */
    public static String bytesToHex(byte[] hash) {
        return HexFormat.of().formatHex(hash).toLowerCase();
    }
}
