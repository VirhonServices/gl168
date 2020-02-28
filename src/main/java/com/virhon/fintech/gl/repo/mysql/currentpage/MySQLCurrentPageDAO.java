package com.virhon.fintech.gl.repo.mysql.currentpage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MySQLCurrentPageDAO {
    MySQLCurrentPageRecord selectById(@Param("tablename") String tablename, @Param("id") Long id);
    MySQLCurrentPageRecord selectByIdExclusive(@Param("tablename") String tablename, @Param("id") Long id);
    MySQLCurrentPageRecord selectByUuid(@Param("tablename") String tablename, @Param("uuid") String uuid);
    void insert(@Param("tablename") String tablename, @Param("pojo") MySQLCurrentPageRecord pojo);
    void update(@Param("tablename") String tablename, @Param("pojo") MySQLCurrentPageRecord pojo);
}
