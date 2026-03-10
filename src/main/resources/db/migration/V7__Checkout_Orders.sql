ALTER TABLE order_items
    DROP CONSTRAINT order_items_status_check;

ALTER TABLE order_items
    ADD CONSTRAINT order_items_status_check
        CHECK (status IN (
                          'PAID_PENDING_SHIPMENT',
                          'SHIPPED',
                          'COMPLETED',
                          'CANCELED',
                          'REFUNDED'
            ));