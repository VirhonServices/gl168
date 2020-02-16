package com.virhon.fintech.gl.repo.mysql;

import com.virhon.fintech.gl.repo.StorageSession;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.getProperties;

public class MySQLStorageSession implements StorageSession {
    private static MySQLStorageSession INSTANCE = null;

    public static final String CONFIGURATION_XML = "mybatis/mybatis-config.xml";
    private InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_XML);
    private SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(inputStream, getProperties());
    private SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.READ_COMMITTED);

    public MySQLStorageSession() throws IOException {
    }

    public static MySQLStorageSession getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new MySQLStorageSession();
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
