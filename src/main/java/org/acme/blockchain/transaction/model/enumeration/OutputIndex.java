package org.acme.blockchain.transaction.model.enumeration;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum OutputIndex {

    RECIPIENT("00"),

    SENDER("01");

    private static final Map<String, OutputIndex> OUTPUT_INDEX_MAP;

    static {
        OUTPUT_INDEX_MAP = Arrays.stream(OutputIndex.values())
                .collect(Collectors.toMap(OutputIndex::getIndex, Function.identity()));
    }

    private final String index;

    OutputIndex(String index) {
        this.index = index;
    }

    public static OutputIndex fromIndex(String index) {
        if (index == null || index.isBlank() || !OUTPUT_INDEX_MAP.containsKey(index)) {
            throw new IllegalArgumentException("Invalid output index: " + index);
        }

        return OUTPUT_INDEX_MAP.get(index);
    }
}
