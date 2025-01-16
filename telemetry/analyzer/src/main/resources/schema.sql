CREATE SCHEMA IF NOT EXISTS analyzer;

CREATE TABLE IF NOT EXISTS analyzer.hubs
(
  id VARCHAR PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS analyzer.devices
(
  id     VARCHAR PRIMARY KEY,
  hub_id VARCHAR,
  type   VARCHAR
);

CREATE TABLE IF NOT EXISTS analyzer.scenarios
(
  id     UUID PRIMARY KEY,
  hub_id VARCHAR,
  name   VARCHAR,
  CONSTRAINT scenarios_name_hub_id_ux UNIQUE (hub_id, name)
);

CREATE TABLE IF NOT EXISTS analyzer.scenario_conditions
(
  id             UUID PRIMARY KEY,
  scenario_id    UUID,
  device_id      VARCHAR,
  condition_type VARCHAR,
  operation      VARCHAR,
  value_type     VARCHAR,
  value          VARCHAR
);

CREATE TABLE IF NOT EXISTS analyzer.device_actions
(
  id          UUID PRIMARY KEY,
  scenario_id UUID,
  device_id   VARCHAR,
  type        VARCHAR,
  value       INT
)
