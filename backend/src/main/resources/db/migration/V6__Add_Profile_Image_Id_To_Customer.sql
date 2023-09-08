alter table customer
    add column profile_image_id varchar(36);

alter table customer
    add constraint unique_profile_image_id
        unique (profile_image_id);