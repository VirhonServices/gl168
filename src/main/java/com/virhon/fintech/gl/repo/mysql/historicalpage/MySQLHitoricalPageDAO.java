package com.virhon.fintech.gl.repo.mysql.historicalpage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper
public interface MySQLHitoricalPageDAO {
    Long insert(@Param("tablename") String tablename, @Param("pojo") MySQLHistoricalPageRecord pojo);
    void update(@Param("tablename") String tablename, @Param("pojo") MySQLHistoricalPageRecord pojo);
    MySQLHistoricalPageRecord selectById(@Param("tablename") String tablename, @Param("id") Long id);
    MySQLHistoricalPageRecord selectByAccountId(@Param("tablename") String tablename,
                                                @Param("accountId") Long accountId,
                                                @Param("postedAt")  ZonedDateTime postedAt);
    List<MySQLHistoricalPageRecord> selectHistory(@Param("tablename") String tablename, @Param("accountId") Long accountId);
}
