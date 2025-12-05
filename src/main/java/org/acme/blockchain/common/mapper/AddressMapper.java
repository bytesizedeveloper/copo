package org.acme.blockchain.common.mapper;

import org.acme.blockchain.common.model.Address;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper {

    default Address map(String address) {
        return new Address(address);
    }

    default String map(Address address) {
        return address.value();
    }
}
