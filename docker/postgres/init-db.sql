-- Create the database if it doesn't exist
SELECT 'CREATE DATABASE copo'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'copo')\gexec

-- Create the user if it doesn't exist
DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'satoshi') THEN

      CREATE ROLE satoshi LOGIN PASSWORD '2theMOON';
   END IF;
END
$do$;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE copo TO satoshi;

-- Connect to the COPO database
\c copo;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO satoshi;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO satoshi;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO satoshi;

SELECT 'Database initialization completed successfully.' AS status;
