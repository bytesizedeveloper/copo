package org.acme.test_common.resource;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresResource {

    private static PostgreSQLContainer<?> container;

    public static PostgreSQLContainer<?> getContainer() {
        if (container == null) {
            container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                    .withDatabaseName("copo")
                    .withUsername("satoshi")
                    .withPassword("2theMOON");

            Runtime.getRuntime().addShutdownHook(new Thread(container::stop));
        }
        return container;
    }
}
