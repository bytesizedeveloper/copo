package org.acme.blockchain.test_common.factory;

import org.acme.blockchain.common.model.Address;
import org.instancio.Instancio;

import static org.instancio.Select.all;
import static org.instancio.Select.field;

public final class AddressTestFactory {

    public static Address getAddress() {
        return Instancio.of(Address.class)
                .supply(field(Address::value), AddressTestFactory::getAddressString)
                .create();
    }

    public static String getAddressString() {
        return Instancio.of(String.class)
                .generate(all(String.class),
                        gen -> gen.string().prefix(Address.PREFIX).hex().lowerCase().length(64))
                .create();
    }
}
