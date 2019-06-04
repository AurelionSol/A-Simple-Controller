package sc.ustc.dao;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.catalina.User;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import sc.ustc.entity.ClassPropertyBean;
import sc.ustc.entity.MappingBean;
import sc.ustc.entity.UserBean;

public class UserLazyLoader extends BaseDAO implements LazyLoader {
	private UserBean userBean;
	private List<MappingBean> mappingList;

	public UserLazyLoader(UserBean userBean) {
		Configuration configuration = Configuration.getConfiguration();
		mappingList = configuration.getMappingList();
		this.setUrl(configuration.getUrl_path());
		this.setDriver(configuration.getDriver_class());
		this.setUserName(configuration.getDb_username());
		this.setUserPassword(configuration.getDb_userpassword());
		this.userBean = userBean;
	}

	public Object loadObject() throws Exception {
		// TODO 自动生成的方法存根
		System.out.println("lazy-loading~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		String passWord=null;
		Connection conn = this.openDBConnection();
		ResultSet rs = null;
		try {
			Statement stmt = conn.createStatement();
			// 根据实体属性动态拼接sql
			String sql = "select " + "userPassword" + " from " + "user" + " where userId='" + userBean.getUserId()
					+ "'";
			System.out.println("EXECUTE SQL: " + sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				passWord=rs.getString("userPassword");
			}
			// 动态解析返回结果
			
		} catch (Exception e) {

		} finally {
			this.closeDBConnection(conn);
		}

		return passWord;

	}


	@Override
	public Object query(String sql) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public boolean insert(String sql) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean update(String sql) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean delete(String sql) {
		// TODO 自动生成的方法存根
		return false;
	}

}
