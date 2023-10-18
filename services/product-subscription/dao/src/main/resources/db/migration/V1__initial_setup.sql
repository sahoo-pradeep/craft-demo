create table product_subscription(
    id          bigserial primary key,
    user_id     varchar not null,
    product     varchar not null,
    status      varchar not null,
    created_at  timestamp not null,
    updated_at  timestamp not null
);

-- unique index to fetch product_subscription by user_id product
create unique index product_subscription_user_id_product_key
    on product_subscription (user_id, product);
