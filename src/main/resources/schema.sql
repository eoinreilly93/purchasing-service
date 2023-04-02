CREATE TABLE product_purchase_reserve (
   id SERIAL NOT NULL,
   purchase_id VARCHAR NOT NULL,
   product_id INT NOT NULL,
   quantity INT NOT NULL,
   CONSTRAINT pk_product_purchase_reserve PRIMARY KEY (id)
);

CREATE INDEX product_id_index
ON product_purchase_reserve(product_id);