DROP TABLE IF EXISTS endpoint_hits;

CREATE TABLE endpoint_hits
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app  VARCHAR(255)                            NOT NULL,
    uri VARCHAR(255)                            NOT NULL,
    ip VARCHAR(31)                            NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_endpoint_hit PRIMARY KEY (id)
);