-- =============================================
-- V3: Normalize data model — drop children array,
-- add index on parent_id for fast child lookups
-- =============================================

-- Add index on parent_id for efficient child queries
CREATE INDEX IF NOT EXISTS idx_primary_families_parent_id
  ON primary_families (parent_id);

-- Drop the denormalized children array column
ALTER TABLE primary_families DROP COLUMN IF EXISTS children;
