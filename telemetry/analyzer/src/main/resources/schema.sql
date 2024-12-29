CREATE TABLE IF NOT EXISTS hubs
(
  id VARCHAR PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS devices
(
  id     VARCHAR PRIMARY KEY,
  hub_id VARCHAR,
  type   VARCHAR
);

CREATE TABLE IF NOT EXISTS scenarios
(
  id     UUID PRIMARY KEY,
  hub_id VARCHAR,
  name   VARCHAR,
  CONSTRAINT scenarios_name_hub_id_ux UNIQUE (hub_id, name)
);

CREATE TABLE IF NOT EXISTS scenario_conditions
(
  id             UUID PRIMARY KEY,
  scenario_id    UUID,
  device_id      VARCHAR,
  condition_type VARCHAR,
  operation      VARCHAR,
  value_type     VARCHAR,
  value          VARCHAR
);

CREATE TABLE IF NOT EXISTS device_actions
(
  id          UUID PRIMARY KEY,
  scenario_id UUID,
  device_id   VARCHAR,
  type        VARCHAR,
  value       INT
)
