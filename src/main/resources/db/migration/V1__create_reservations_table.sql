CREATE SEQUENCE IF NOT EXISTS reservation_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE product_purchase_reserve
(
    id          BIGINT  NOT NULL,
    purchase_id VARCHAR NOT NULL,
    product_id  INT     NOT NULL,
    quantity    INT     NOT NULL,
    CONSTRAINT pk_product_purchase_reserve PRIMARY KEY (id)
);

CREATE INDEX product_id_index
    ON product_purchase_reserve (product_id);