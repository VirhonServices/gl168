package com.virhon.fintech.gl.repo.mysql.historicalpage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper
public interface MySQLHitoricalPageDAO {
    Long insert(@Param("pojo") MySQLHistoricalPageRecord pojo);
    void update(@Param("pojo") MySQLHistoricalPageRecord pojo);
    MySQLHistoricalPageRecord selectById(@Param("id") Long id);
    MySQLHistoricalPageRecord selectByAccountId(@Param("accountId") Long accountId,
                                                @Param("postedAt")  ZonedDateTime postedAt);
    List<MySQLHistoricalPageRecord> selectHistory(@Param("accountId") Long accountId);
}
