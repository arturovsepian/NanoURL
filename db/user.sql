create user <put_user_here> identified by "put_password_here";
select default_tablespace from dba_users where username = '<put_user_here>';
grant create table to <put_user_here>;
grant create session to <put_user_here>;
alter user <put_user_here> quota unlimited on <default_tablespace>;