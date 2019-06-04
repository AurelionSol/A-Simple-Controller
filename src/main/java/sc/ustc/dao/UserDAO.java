package sc.ustc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import sc.ustc.entity.UserBean;

public class UserDAO extends BaseDAO {
	protected String driver = "com.mysql.cj.jdbc.Driver";
	// 数据库连接地址
	protected String url = "jdbc:mysql://localhost:3306/user?useUnicode=true&characterEncoding=UTF-8";
	// 数据库用户名
	protected String userName = "root";
	// 数据库密码
	protected String userPassword = "123456";

	public UserDAO() {
		this.setUrl(url);
		this.setDriver(driver);
		this.setUserName(userName);
		this.setUserPassword(userPassword);
	}

	@Override
	public UserBean query(String sql) {
		// TODO 自动生成的方法存根
		Connection conn = this.openDBConnection();
		ResultSet resultSet = null;
		UserBean user = null;
		try {
			Statement stmt = conn.createStatement();
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				// 注意：这里要与数据库里的字段对应
				user = new UserBean();
				user.setUserId(resultSet.getString("userId"));
				user.setUserName(resultSet.getString("userName"));
				user.setUserPassword(resultSet.getString("userPassword"));
				System.out.println(user.toString());
			}
			conn.setAutoCommit(false);
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			this.closeDBConnection(conn);
		}

		return user;
	}

	@Override
	public boolean insert(String sql) {
		// TODO 自动生成的方法存根
		boolean success = false;
		int result = 0;
		Connection conn = this.openDBConnection();
		try {
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			Statement stmt = conn.createStatement();
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeDBConnection(conn);
		}
		if (result != -1) {
			success = true;
		}
		return success;
	}

	@Override
	public boolean update(String sql) {
		// TODO 自动生成的方法存根
		boolean success = false;
		int result = 0;
		Connection conn = this.openDBConnection();
		try {
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			Statement stmt = conn.createStatement();
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeDBConnection(conn);
		}
		if (result != -1) {
			success = true;
		}
		return success;
	}

	@Override
	public boolean delete(String sql) {
		// TODO 自动生成的方法存根
		boolean success = false;
		int result = 0;
		Connection conn = this.openDBConnection();
		try {
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			Statement stmt = conn.createStatement();
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeDBConnection(conn);
		}
		if (result != -1) {
			success = true;
		}
		return success;

	}

	public UserBean getObject(UserBean userBean) {
		Conversation conversation = Conversation.getConversation();
		UserBean user = (UserBean) conversation.getObject(userBean);
		return user;
	}
}
