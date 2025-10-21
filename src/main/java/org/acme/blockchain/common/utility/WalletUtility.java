package org.acme.blockchain.common.utility;

/**
 * Utility class providing common validation and helper functions related to cryptocurrency wallet addresses.
 * <p>
 * Ensures consistency and correctness when dealing with wallet addresses throughout the application.
 */
public final class WalletUtility {

    /**
     * Validates if a given string conforms to the required format of a COPO wallet address.
     * <p>
     * A valid COPO address must:
     * <ul>
     * <li>Not be {@code null}.</li>
     * <li>Start with the prefix "COPO_".</li>
     * <li>Be followed by exactly 64 hexadecimal characters (0-9, a-f).</li>
     * </ul>
     *
     * @param address The wallet address string to validate.
     * @return {@code true} if the address is valid and conforms to the format, {@code false} otherwise.
     */
    public static boolean isValid(String address) {
        return address != null && address.matches("^COPO_[a-f0-9]{64}$");
    }
}
