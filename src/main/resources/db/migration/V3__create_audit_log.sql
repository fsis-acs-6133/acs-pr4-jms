create table if not exists audit_log (
  id bigserial primary key,
  occurred_at timestamptz not null,
  action varchar(16) not null,
  entity_type varchar(64) not null,
  entity_id bigint,
  summary text,
  before_data text,
  after_data text
);

create index if not exists idx_audit_log_entity on audit_log(entity_type, entity_id);
create index if not exists idx_audit_log_occurred on audit_log(occurred_at);
