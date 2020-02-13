package com.virhon.fintech.gl.repo.mysql.reservation;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MySQLReservationDAO {
    MySQLReservationRecord selectById(@Param("tablename") String tablename, @Param("id") Long id);
    MySQLReservationRecord selectByIdExclusive(@Param("tablename") String tablename, @Param("id") Long id);
    Long insert(@Param("tablename") String tablename, @Param("pojo") MySQLReservationRecord pojo);
    void delete(@Param("tablename") String tablename, @Param("id") Long id);
}
