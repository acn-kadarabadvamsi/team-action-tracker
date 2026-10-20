# Database Setup and Migrations

## Current Database

The application uses MySQL and contains four application tables:

- `users`
- `action_items`
- `action_comments`
- `action_history`

The existing development database has a Flyway baseline at version 1. It was preserved during schema verification.

## Fresh Database Initialization

The initialization script is located at:

`database/initialization/initial_schema.sql`

It creates the four application tables and their relationships. It does not create the Flyway metadata table and contains no `DROP TABLE` statements.

### Verification Performed

- Created a separate test database.
- Imported the initialization script successfully.
- Confirmed all four application tables were created.
- Verified all six expected foreign-key relationships.
- Started Spring Boot against the test database with Hibernate schema validation enabled.
- Disabled Flyway for that specific startup test because the initialization script does not create Flyway metadata.

These checks confirm schema creation and startup compatibility. They do not replace a full integration test.

## Flyway Migration Policy

- Store versioned migration scripts in `backend/src/main/resources/db/migration/`.
- Use sequential versioned filenames, such as `V2__add_user_status_index.sql`.
- Back up the database before applying schema changes.
- Test migrations against a separate database before applying them to the development or production database.
- Never execute the schema reference snapshot as a migration.
- Never recreate existing tables or modify existing data without an approved migration plan.

## Current Limitations

- The existing database has a baseline at version 1.
- No versioned SQL migration scripts currently exist.
- Fresh database initialization and Flyway migration history are not yet unified into one automated setup process.
- Hibernate schema auto-update remains enabled in the normal application configuration.

## Next Steps

1. Establish a consistent Flyway strategy for existing and fresh databases.
2. Test the migration strategy against separate databases.
3. Consider changing Hibernate schema management from `update` to `validate` after migrations are verified.
4. Document the complete setup and verification process in the project README.

## Flyway Baseline Verification

A separate test database, `team_action_tracker_schema_test_20261020`, was used to verify the initialization and baseline process.

Results:
- Imported `database/initialization/initial_schema.sql` successfully.
- Confirmed the four application tables and six foreign-key relationships.
- Started the application with Hibernate schema validation.
- Enabled Flyway with baseline-on-migrate and baseline version 1.
- Confirmed that Flyway created a successful baseline record.
- Verified that all application tables remained present after baseline creation.

This test verifies schema initialization and baseline creation. It does not yet verify the execution of a versioned migration script.

## Versioned Migration Verification

The following migration was tested against the separate test database:

- `V2__add_action_item_status_index.sql`
- Added index: `idx_action_items_status` on `action_items(status)`.
- Confirmed Flyway recorded version 2 with `success = 1`.
- Confirmed the index exists in MySQL after migration.

This verifies that a versioned migration can run after the version-1 baseline. The migration has not been applied to the original development database.
