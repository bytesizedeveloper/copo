package org.acme.blockchain.transaction.mapper;

import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.mapstruct.Mapper;

@Mapper
public interface OutputIndexMapper {

    default OutputIndex map(String outputIndex) {
        return OutputIndex.fromIndex(outputIndex);
    }

    default String map(OutputIndex outputIndex) {
        return outputIndex.getIndex();
    }
}
