package org.acme.blockchain.common.utility;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A utility class for generating Merkle trees and calculating their root hash.
 * This class is designed to handle cryptographic hashing for data verification
 * and integrity checks.
 */
@Slf4j
public final class MerkleTreeUtility {

    /**
     * Calculates the Merkle root of a list of cryptographic hashes.
     * This method serves as the main entry point for generating a Merkle root from a set of data hashes.
     *
     * @param hashes The list of leaf-level hashes.
     * @return The final Merkle root hash as a hexadecimal string.
     * @throws IllegalStateException if the input list of hashes is null or empty.
     */
    public static String calculateMerkleRoot(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            throw new IllegalStateException();
        }

        log.debug("Calculating merkle tree for {} hashes.", hashes.size());
        return buildMerkleTree(hashes);
    }

    /**
     * Recursively calculates the Merkle root of a list of hashes.
     * <p>
     * This method builds a Merkle tree by repeatedly hashing pairs of branches.
     * If the number of hashes is uneven, the last hash is duplicated to ensure
     * a complete binary tree structure. The process is repeated until a single
     * root hash remains.
     *
     * @param hashes The initial list of SHA-256 hashes (leaf nodes).
     * @return The final Merkle root as a SHA-256 hash string.
     */
    private static String buildMerkleTree(List<String> hashes) {
        if (hashes.size() % 2 != 0) {
            log.debug("Input hash count is uneven ({}) - duplicating final hash.", hashes.size());
            hashes.add(hashes.getLast());
        }

        List<String> branches = collapseBranches(hashes);
        log.debug("Processed merkle tree for {} hashes.", hashes.size());

        if (branches.size() == 1) {
            String root = branches.getFirst();
            log.debug("Merkle tree root calculated: {}", root);
            return root;
        } else if (branches.size() > 1) {
            return buildMerkleTree(branches);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Processes a single level of a Merkle tree by hashing pairs of branches.
     * <p>
     * This private helper method iterates through a list of hashes, combines
     * each adjacent pair, and calculates the SHA-256 hash of the concatenated
     * pair.
     *
     * @param hashes A list of hashes from the current level of the tree.
     * @return A new list containing the parent hashes for the next level.
     */
    private static List<String> collapseBranches(List<String> hashes) {
        return IntStream.range(0, hashes.size() - 1)
                .filter(i -> i % 2 == 0)
                .mapToObj(i -> hashes.get(i) + hashes.get(i + 1)).toList()
                .stream().map(HashUtility::calculateSHA256).collect(Collectors.toList());
    }
}
