create table address(
    id          bigserial primary key,
    user_id     varchar not null,
    line1       varchar not null,
    line2       varchar,
    line3       varchar,
    city        varchar not null,
    state       varchar not null,
    country     varchar not null,
    zip         varchar not null,
    created_at  timestamp not null,
    updated_at  timestamp not null
);

create table business_profile(
    id                  bigserial primary key,
    user_id             varchar not null,
    company_name        varchar not null,
    legal_name          varchar not null,
    pan                 varchar not null,
    ein                 varchar not null,
    email               varchar not null,
    website             varchar,
    business_address_id bigint not null,
    legal_address_id    bigint not null,
    created_at          timestamp not null,
    updated_at          timestamp not null,

    constraint business_profile_business_address_id_fkey
        foreign key (business_address_id) references address (id),

    constraint business_profile_legal_address_id_fkey
        foreign key (legal_address_id) references address (id)
);
-- index to fetch business profile by user id
create unique index business_profile_user_id_key
    on business_profile (user_id);

create table business_profile_change_request(
    id                  bigserial primary key,
    request_id          varchar not null,
    operation           varchar not null,
    user_id             varchar not null,
    company_name        varchar not null,
    legal_name          varchar not null,
    pan                 varchar not null,
    ein                 varchar not null,
    email               varchar not null,
    website             varchar,
    business_address_id bigint not null,
    legal_address_id    bigint not null,
    status              varchar not null,
    created_at          timestamp not null,
    updated_at          timestamp not null,

    constraint business_profile_change_request_business_address_id_fkey
        foreign key (business_address_id) references address (id),

    constraint business_profile_change_request_legal_address_id_fkey
        foreign key (legal_address_id) references address (id)
);

-- unique index on request_id to ensure all request_id are unique
create unique index business_profile_change_request_request_id_key
    on business_profile_change_request (request_id);

-- index to fetch update request for a given user with given status
create index business_profile_change_request_user_id_status_idx
    on business_profile_change_request (user_id, status);

create table change_request_product_status(
    id              bigserial primary key,
    request_id      varchar not null,
    product         varchar not null,
    status          varchar not null,
    created_at      timestamp not null,
    updated_at      timestamp not null
);

-- unique index to fetch product status by requestId
create index change_request_product_status_request_id_key
    on change_request_product_status (request_id);

create table change_request_failure_reason(
    id              bigserial primary key,
    request_id      varchar not null,
    product         varchar not null,
    field           varchar not null,
    reason          varchar,
    created_at      timestamp not null
);

-- unique index to fetch failure reason by requestId
create index change_request_failure_reason_request_id_key
    on change_request_failure_reason (request_id);