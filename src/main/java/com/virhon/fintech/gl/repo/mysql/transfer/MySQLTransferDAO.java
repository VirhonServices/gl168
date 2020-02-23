package com.virhon.fintech.gl.repo.mysql.transfer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MySQLTransferDAO {
    MySQLTransferRecord selectById(@Param("tablename") String tablename, @Param("id") Long id);
    MySQLTransferRecord selectByUuid(@Param("tablename") String tablename, @Param("uuid") String uuid);
    MySQLTransferRecord selectByIdExclusive(@Param("tablename") String tablename, @Param("id") Long id);
    Long insert(@Param("tablename") String tablename, @Param("pojo") MySQLTransferRecord pojo);
    void update(@Param("tablename") String tablename, @Param("pojo") MySQLTransferRecord pojo);
}
