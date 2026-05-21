-- =============================================
-- V2: Enable pg_trgm and add trigram indexes
-- for fast ILIKE '%search%' queries
-- =============================================

-- Enable the trigram extension (safe to run multiple times)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Create GIN trigram indexes for fast text search
CREATE INDEX IF NOT EXISTS idx_primary_families_name_trgm
  ON primary_families USING gin (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_primary_families_hindi_name_trgm
  ON primary_families USING gin (hindi_name gin_trgm_ops);
