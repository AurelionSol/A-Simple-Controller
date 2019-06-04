package sc.ustc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {
	protected String driver;
	protected String url;
	protected String userName;
	protected String userPassword;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	protected Connection openDBConnection() {
		Connection conn = null;
		try {
			Class.forName(driver);// 动态加载mysql驱动
			conn = DriverManager.getConnection(url,userName,userPassword);
		} catch (SQLException e) {
			System.out.println("数据库连接失败");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return conn;
	}

	protected boolean closeDBConnection(Connection conn) {
		boolean closed = true;
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			closed = false;
			e.printStackTrace();
		}
		return closed;
	}

	public abstract Object query(String sql);

	public abstract boolean insert(String sql);

	public abstract boolean update(String sql);

	public abstract boolean delete(String sql);
}
