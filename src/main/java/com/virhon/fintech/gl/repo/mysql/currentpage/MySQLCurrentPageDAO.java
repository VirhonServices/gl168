package com.virhon.fintech.gl.repo.mysql.currentpage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MySQLCurrentPageDAO {
    MySQLCurrentPageRecord selectById(@Param("id") Long id);
    MySQLCurrentPageRecord selectByIdExclusive(@Param("id") Long id);
    void insert(@Param("pojo") MySQLCurrentPageRecord pojo);
    void update(@Param("pojo") MySQLCurrentPageRecord pojo);
}
