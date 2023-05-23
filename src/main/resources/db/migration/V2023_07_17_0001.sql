CREATE TABLE one_time_code (
    id VARCHAR(36) PRIMARY KEY,
    one_time_code_id VARCHAR(36) NOT NULL,
    "value" VARCHAR(255) NOT NULL,
    expires TIMESTAMP(3) NOT NULL,
    attempts SMALLINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    date_created TIMESTAMP(3) NOT NULL,
    date_updated TIMESTAMP(3) NOT NULL,
    version SMALLINT NOT NULL
);

CREATE UNIQUE INDEX one_time_code_id_idx
ON one_time_code (
    one_time_code_id
);
