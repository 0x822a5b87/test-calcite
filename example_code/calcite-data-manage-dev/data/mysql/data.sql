create schema db_cdm;
use db_cdm;

create table sys_user
(
    id            bigint auto_increment primary key,
    user_name     varchar(63)                           not null comment '用户名',
    password      varchar(258)                          not null comment '密码',
    is_admin      tinyint     default 0                 not null comment '是否是管理员',
    role          varchar(20) default 'user'            null comment '角色',
    created_date  timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_date timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    unique index uniq_name (user_name)
);

insert into sys_user(user_name, password, is_admin, role)
values ('admin', 'admin123456', 1, 'admin'),
       ('jimo', 'ps123456', 0, 'user'),
       ('hehe', 'ps654321', 0, 'nobody');