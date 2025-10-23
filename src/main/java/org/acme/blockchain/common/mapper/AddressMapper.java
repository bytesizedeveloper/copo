package org.acme.blockchain.common.mapper;

import org.acme.blockchain.common.model.AddressModel;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper {

    default AddressModel map(String address) {
        return new AddressModel(address);
    }

    default String map(AddressModel address) {
        return address.value();
    }
}
