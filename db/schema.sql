create table nano_url(short_url varchar2(7) UNIQUE, long_url varchar2(2000), create_time timestamp default systimestamp);