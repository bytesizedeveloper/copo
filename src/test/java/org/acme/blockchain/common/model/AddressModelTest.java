package org.acme.blockchain.common.model;

import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AddressModelTest {

    @Test
    void testCanonicalConstructor_validAddress_createsRecord() {
        // Given
        String validAddress = WalletTestData.ADDRESS_ALPHA.value();

        // When
        AddressModel address = new AddressModel(validAddress);

        // Then
        Assertions.assertEquals(WalletTestData.ADDRESS_ALPHA, address);
    }

    @Test
    void testCanonicalConstructor_nullAddress_throwsIllegalArgumentException() {
        // Given
        String invalidAddress = null;

        // Then
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> new AddressModel(invalidAddress), "Exception should be thrown in the event of an invalid address being used."
        );

        Assertions.assertEquals("Invalid address: " + invalidAddress, thrown.getMessage());
    }

    @Test
    void testCanonicalConstructor_emptyAddress_throwsIllegalArgumentException() {
        // Given
        String invalidAddress = "";

        // Then
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> new AddressModel(invalidAddress), "Exception should be thrown in the event of an invalid address being used."
        );

        Assertions.assertEquals("Invalid address: " + invalidAddress, thrown.getMessage());
    }

    @Test
    void testCanonicalConstructor_blankAddress_throwsIllegalArgumentException() {
        // Given
        String invalidAddress = " ";

        // Then
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> new AddressModel(invalidAddress), "Exception should be thrown in the event of an invalid address being used."
        );

        Assertions.assertEquals("Invalid address: " + invalidAddress, thrown.getMessage());
    }

    @Test
    void testCanonicalConstructor_shortAddress_throwsIllegalArgumentException() {
        // Given
        String invalidAddress = "COPO_abcdef0123456789";

        // Then
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> new AddressModel(invalidAddress), "Exception should be thrown in the event of an invalid address being used."
        );

        Assertions.assertEquals("Invalid address: " + invalidAddress, thrown.getMessage());
    }

    @Test
    void testCanonicalConstructor_longAddress_throwsIllegalArgumentException() {
        // Given
        String invalidAddress = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";

        // Then
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> new AddressModel(invalidAddress), "Exception should be thrown in the event of an invalid address being used."
        );

        Assertions.assertEquals("Invalid address: " + invalidAddress, thrown.getMessage());
    }

    @Test
    void testCanonicalConstructor_invalidCharacters_throwsIllegalArgumentException() {
        // Given
        String invalidAddress = "COPO_ghijklmnopqrstuvwxyzghijklmnopqrstuvwxyzghijklmnopqrstuvwxyzghij";

        // Then
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> new AddressModel(invalidAddress), "Exception should be thrown in the event of an invalid address being used."
        );

        Assertions.assertEquals("Invalid address: " + invalidAddress, thrown.getMessage());
    }

    @Test
    void testCanonicalConstructor_missingPrefix_throwsIllegalArgumentException() {
        // Given
        String invalidAddress = "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";

        // Then
        Exception thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> new AddressModel(invalidAddress), "Exception should be thrown in the event of an invalid address being used."
        );

        Assertions.assertEquals("Invalid address: " + invalidAddress, thrown.getMessage());
    }
}
