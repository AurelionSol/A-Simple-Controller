package sc.ustc.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import sc.ustc.entity.ClassPropertyBean;
import sc.ustc.entity.MappingBean;
import sc.ustc.entity.UserBean;

public class Conversation {
	private static Conversation conversation;
	private List<MappingBean> mappingList;
	private String url;
	private String driver;
	private String userName;
	private String userPassword;
	private Class<?> clazz;

	public static Conversation getConversation() {
		Conversation instance = null;
		if (null == conversation) {
			instance = new Conversation();
		} else {
			instance = conversation;
		}
		return instance;
	}

	Conversation() {
		Configuration configuration = Configuration.getConfiguration();
		mappingList = configuration.getMappingList();
		this.setUrl(configuration.getUrl_path());
		this.setDriver(configuration.getDriver_class());
		this.setUserName(configuration.getDb_username());
		this.setUserPassword(configuration.getDb_userpassword());
	}

	public <T> Object getObject(T entity) {
		Object obj = null;
		Connection conn = null;
		String className = entity.getClass().getName();
		MappingBean mBean = null;
		HashMap fieldsValueMap = new HashMap();
		for (MappingBean mappingBean : mappingList) {
			if (mappingBean.getName().equals(className)) {
				mBean = mappingBean;
				break;
			}
		}
		List<ClassPropertyBean> propertyList = mBean.getPropertyList();
		String table = mBean.getTable();
		String id = mBean.getId();
		Method[] methods = entity.getClass().getMethods();
		// 需要查询的字段
		String showProerty = "userId";
		try {
			Method method = entity.getClass().getDeclaredMethod("getUserId");
			fieldsValueMap.put(id, method.invoke(entity));
			for (ClassPropertyBean classPropertyBean : propertyList) {
				if (!classPropertyBean.isLazy()) {
					showProerty += "," + classPropertyBean.getColumn();
					String field = "get" + Character.toUpperCase(classPropertyBean.getName().charAt(0))
							+ classPropertyBean.getName().substring(1);// getUserName
					method = entity.getClass().getDeclaredMethod(field);
					fieldsValueMap.put(classPropertyBean.getName(), method.invoke(entity));
				}
			}
			// 创建数据库连接
			Class.forName(driver);// 动态加载mysql驱动
			conn = DriverManager.getConnection(url, userName, userPassword);
			ResultSet rs = null;
			Statement stmt = conn.createStatement();
			// 根据实体属性动态拼接sql
			String sql = "select " + showProerty + " from " + table;
			boolean first = true;// 判断是否第一次拼接where，例如 where id=‘’ and name=‘’
			if (fieldsValueMap.get(id) != null) {
				if (first) {
					sql += " where id='" + fieldsValueMap.get(id) + "'";
					first = false;
				} else {
					sql += " and id='" + fieldsValueMap.get(id) + "'";
				}
			}
			for (ClassPropertyBean classPropertyBean : propertyList) {
				if (fieldsValueMap.get(classPropertyBean.getName()) != null) {
					if (first) {
						sql += " where " + classPropertyBean.getColumn() + "='"
								+ fieldsValueMap.get(classPropertyBean.getName()) + "'";
						first = false;
					} else {
						sql += " and " + classPropertyBean.getColumn() + "='"
								+ fieldsValueMap.get(classPropertyBean.getName()) + "'";
					}
				}
			}

			// 执行sql
			System.out.println("EXECUTE SQL: " + sql);
			rs = stmt.executeQuery(sql);

			// 动态解析返回结果
			while (rs.next()) {
				// 构建查询结果要返回的实体
				clazz = Class.forName(className);
				obj = clazz.newInstance();// 返回的实体

				ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] names = new String[count];
				for (int i = 0; i < count; i++)
					names[i] = rsmd.getColumnName(i + 1);

				// 遍历返回的列名,利用java反射调用对应的set方法，构建新的实体的属性值
				int i = 1;
				for (String name : names) {
					if ("userId".equals(name)) {
						name = "set" + Character.toUpperCase(id.charAt(0)) + id.substring(1);
					} else {
						name = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
					}
					for (Method m : methods) {
						if (m.getName().equals(name)) {
							m.invoke(obj, rs.getString(i));// 调用set方法对obj设值
							i++;
						}
					}
				}

				break;
			}

		} catch (NoSuchMethodException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		return obj;
	}

	// public <T> boolean deleteObject(T entity) {
	//
	// }

	public List<MappingBean> getMappingList() {
		return mappingList;
	}

	public void setMappingList(List<MappingBean> mappingList) {
		this.mappingList = mappingList;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String drive) {
		this.driver = drive;
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

	public static void setConversation(Conversation conversation) {
		Conversation.conversation = conversation;
	}

}
