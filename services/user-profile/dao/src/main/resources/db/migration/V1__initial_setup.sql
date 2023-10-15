create table address(
    id          bigserial primary key,
    line1       varchar not null ,
    line2       varchar,
    line3       varchar,
    city        varchar not null,
    state       varchar not null,
    country     varchar not null,
    zip         varchar not null,
    created_at  timestamp not null
    -- Non-modifiable table, so no updated_at.
);

create table business_profile(
    id                  bigserial primary key,
    user_id             varchar not null,
    company_name        varchar not null,
    legal_name          varchar not null,
    business_address_id bigint not null,
    legal_address_id    bigint not null,
    pan                 varchar  not null,
    ein                 varchar  not null,
    email               varchar  not null,
    website             varchar,
    created_at          timestamp not null,
    updated_at          timestamp not null,

    constraint business_profile_business_address_id_fkey
        foreign key (business_address_id) references address (id)
);
-- index to fetch business profile by user id
create unique index business_profile_user_id_key
    on business_profile (user_id);

create table business_profile_change_request(
    id                  bigserial primary key,
    request_uuid        varchar not null,
    user_id             varchar not null,
    company_name        varchar not null,
    legal_name          varchar not null,
    business_address_id bigint not null,
    legal_address_id    bigint not null,
    pan                 varchar  not null,
    ein                 varchar  not null,
    email               varchar  not null,
    website             varchar,
    created_at          timestamp not null,
    status              varchar not null,
    failure_reason      varchar
);
-- index to fetch business profile change request by unique request uuid
create unique index business_profile_change_request_request_uuid_key
    on business_profile_change_request (request_uuid);

-- index to fetch business profile change request for a given user with given status
create unique index business_profile_change_request_user_id_status_idx
    on business_profile_change_request (user_id, status);