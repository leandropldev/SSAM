CREATE TABLE IF NOT EXISTS asset_token (
    id UUID PRIMARY KEY,
    status VARCHAR(50),
    owner_id VARCHAR(255),
    confidential_data VARCHAR(255),
    slot_bitmap SMALLINT,
    update_counter BIGINT,
    last_status_change TIMESTAMP,
    parent_id UUID,
    CONSTRAINT fk_asset_token_parent
        FOREIGN KEY (parent_id) REFERENCES asset_token(id)
);