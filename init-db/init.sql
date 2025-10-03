-- Initialize library database
-- Note: Database library_db is automatically created by Docker environment variables

-- Grant permissions (database already exists via POSTGRES_DB env var)
GRANT ALL PRIVILEGES ON DATABASE library_db TO library_user;

-- Connect to the library database
\c library_db;

-- Create schema if needed
CREATE SCHEMA IF NOT EXISTS public;

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO library_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO library_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO library_user;