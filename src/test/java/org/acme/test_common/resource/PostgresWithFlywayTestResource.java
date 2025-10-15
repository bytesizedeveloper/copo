package org.acme.test_common.resource;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class PostgresWithFlywayTestResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer<?> postgresContainer;

    @Override
    public Map<String, String> start() {
        // Start the PostgreSQL container using the utility class
        postgresContainer = PostgresResource.getContainer();
        postgresContainer.start();

        // Run Flyway migrations on the test database
        runFlywayMigrations();

        // Return the properties to configure the Quarkus application for the test database
        Map<String, String> properties = new HashMap<>();
        properties.put("quarkus.datasource.jdbc.url", postgresContainer.getJdbcUrl());
        properties.put("quarkus.datasource.username", postgresContainer.getUsername());
        properties.put("quarkus.datasource.password", postgresContainer.getPassword());
        return properties;
    }

    private void runFlywayMigrations() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgresContainer.getJdbcUrl(),
                        postgresContainer.getUsername(),
                        postgresContainer.getPassword()
                )
                .schemas("blockchain")
                .locations("db/migration")
                .load();
        flyway.migrate();
    }

    @Override
    public void stop() {
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
    }
}
