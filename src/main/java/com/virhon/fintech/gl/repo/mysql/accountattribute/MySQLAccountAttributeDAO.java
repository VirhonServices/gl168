package com.virhon.fintech.gl.repo.mysql.accountattribute;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MySQLAccountAttributeDAO {
    MySQLAccountAttributeRecord selectById(@Param("id") Long id);
    MySQLAccountAttributeRecord selectByUuid(@Param("uuid") String uuid);
    MySQLAccountAttributeRecord selectByIdExclusive(@Param("id") Long id);
    Long insert(@Param("pojo") MySQLAccountAttributeRecord pojo);
    void update(@Param("pojo") MySQLAccountAttributeRecord pojo);
}
