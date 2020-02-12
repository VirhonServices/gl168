package com.virhon.fintech.gl.repo.mysql.historicalpage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper
public interface MySQLHitoricalPageDAO {
    MySQLHistoricalPageRecord selectById(@Param("id") Long id);
    MySQLHistoricalPageRecord selectByAccountId(@Param("accountId") Long accountId,
                                                @Param("postedAt") ZonedDateTime postedAt);
    List<MySQLHistoricalPageRecord> selectReportedHistory(@Param("accountId") Long accountId,
                                                          @Param("from") LocalDate from,
                                                          @Param("to") LocalDate to);
    Long insert(@Param("pojo") MySQLHistoricalPageRecord pojo);
    void update(@Param("pojo") MySQLHistoricalPageRecord pojo);
}
