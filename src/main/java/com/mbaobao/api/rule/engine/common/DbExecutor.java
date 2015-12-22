package com.mbaobao.api.rule.engine.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DbExecutor {

	/**
	 * 获取Connection
	 * @return
	 */
	public static Connection getConnection() {
		return DBConnection.getConnection();

	}

	/**
	 * 取得ResultSet
	 * @param conn
	 * @param sqlText
	 * @return
	 */
	public static ResultSet read(Connection conn, String sqlText) {
		try {
			return conn.prepareStatement(sqlText).executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 关闭
	 * @param conn
	 * @param stat
	 * @param rs
	 */
	public static void close(Connection conn, Statement stat, ResultSet rs) {
		DBConnection.closeConnection(conn, stat, rs);
	}

	public static boolean write(String sqlStr) {
		return write(sqlStr, null);
	}

	/**
	 * 执行写操作
	 * @param sqlStr
	 * @param rowList
	 * @return
	 */
	public static boolean write(String sqlStr, Map<String, String> row) {
		Connection conn = getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sqlStr);
			int parameterIndex = 1;

			conn.setAutoCommit(false);
			if (row != null) {
				for (String key : row.keySet()) {
					String aa = row.get(key);
					ps.setObject(parameterIndex, aa);
					parameterIndex++;
				}
			}
			ps.addBatch();

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);

			return true;
		} catch (Exception e) {
			try {
				if (!conn.isClosed()) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;
		} finally {
			close(conn, null, null);
		}
	}
}
