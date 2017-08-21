-- create user for testing
CREATE USER travis IDENTIFIED BY travis;
grant connect, resource, dba to travis;
grant create session, alter any procedure to travis;

-- to enable xa recovery, see: https://community.oracle.com/thread/378954
grant select on sys.dba_pending_transactions to travis;

grant select on sys.pending_trans$ to travis;
grant select on sys.dba_2pc_pending to travis;
grant execute on sys.dbms_system to travis;

-- http://www.dba-oracle.com/t_ora_01950_no+priviledges_on_tablespace_string.htm
GRANT UNLIMITED TABLESPACE TO travis;
