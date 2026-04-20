package db;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

/**
 * Thin wrapper around Flyway so migrations can be run at application
 * startup. Each .sql file under classpath:db/migration is applied once
 * (tracked in the flyway_schema_history table).
 */
public class MigrationRunner {

    private static final String URL = "jdbc:postgresql://localhost:5432/movies_db";
    private static final String USER = "postgres";
    private static final String PASS = "0745493242";

    public void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(URL, USER, PASS)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        MigrateResult result = flyway.migrate();
        System.out.println("Flyway: applied "
                + result.migrationsExecuted
                + " migration(s); schema is at version "
                + result.targetSchemaVersion);
    }
}
