package com.virhon.fintech.gl.repo.mysql.accountattribute;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MySQLAccountAttributeDAO {
    MySQLAccountAttributeRecord selectById(@Param("tablename") String tablename, @Param("id") Long id);
    MySQLAccountAttributeRecord selectByUuid(@Param("tablename") String tablename, @Param("uuid") String uuid);
    MySQLAccountAttributeRecord selectByIdExclusive(@Param("tablename") String tablename, @Param("id") Long id);
    Long insert(@Param("tablename") String tablename, @Param("pojo") MySQLAccountAttributeRecord pojo);
    void update(@Param("tablename") String tablename, @Param("pojo") MySQLAccountAttributeRecord pojo);
}
