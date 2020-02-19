package com.virhon.fintech.gl.repo.mysql;

import com.virhon.fintech.gl.repo.StorageConnection;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.getProperties;

public class MySQLStorageConnection implements StorageConnection {
    private static MySQLStorageConnection INSTANCE = null;

    public static final String CONFIGURATION_XML = "mybatis/mybatis-config.xml";
    private InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_XML);
    private SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(inputStream, getProperties());
    private SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.READ_COMMITTED);

    public MySQLStorageConnection() throws IOException {
    }

    public static MySQLStorageConnection getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new MySQLStorageConnection();
        }
        return INSTANCE;
    }

    public SqlSession getSession() {
        return this.session;
    }

    @Override
    public void commit() {
        this.session.commit();
    }
}
