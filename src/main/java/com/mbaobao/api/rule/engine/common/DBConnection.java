package com.mbaobao.api.rule.engine.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;

public class DBConnection {

	private BasicDataSource		dataSource;

	private static DBConnection	DB_CON	= new DBConnection();

	private DBConnection() {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/MayMayDB?autoReconnect=true");
		dataSource.setUsername("root");
		dataSource.setPassword("123qwe");
		dataSource.setConnectionProperties("characterEncoding=utf8;");
		dataSource.setMaxIdle(1000);
		dataSource.setMinIdle(10);
		dataSource.setMaxActive(1000);
		dataSource.setValidationQuery("select 1");
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setTimeBetweenEvictionRunsMillis(3600000);
		dataSource.setMinEvictableIdleTimeMillis(1800000);
	}

	public static Connection getConnection() {
		try {
			Connection con = DBConnection.DB_CON.dataSource.getConnection();
			return con;
		} catch (SQLException e) {
		}
		throw new RuntimeException("无法取得数据源连接");
	}

	/**
	 * 释放数据库的资源.
	 * 
	 * @param conn
	 *        数据库连接
	 * @param stat
	 *        {@link Statement}对象
	 * @param rs
	 *        {@link ResultSet}对象
	 */
	public static void closeConnection(Connection conn, Statement stat, ResultSet rs) {
		try {
			// 关闭结果集对象.
			if (rs != null && !rs.isClosed()) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			// 当关闭失败时,记录错误信息,并报告上层代码.
			throw new RuntimeException(e);
		} finally {
			try {
				// 关闭Statement对象.
				if (stat != null && !stat.isClosed()) {
					stat.close();
					stat = null;
				}
			} catch (SQLException e) {
				// 当关闭失败时,记录错误信息,并报告上层代码.
				throw new RuntimeException(e);
			} finally {
				try {
					// 关闭数据库连接.
					if (conn != null && !conn.isClosed()) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
					// 当关闭失败时,记录错误信息,并报告上层代码.
					throw new RuntimeException(e);
				}
			}
		}
	}
}
