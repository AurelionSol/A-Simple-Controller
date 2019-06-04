package sc.ustc.entity;


import net.sf.cglib.proxy.Enhancer;
import sc.ustc.dao.Conversation;
import sc.ustc.dao.UserDAO;
import sc.ustc.dao.UserLazyLoader;

public class UserBean {
	private String userId;
	private String userName;
	private String userPassword;
	private UserDAO userDAO;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return createPassword().toString();
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	 protected Object createPassword(){  
	        Enhancer enhancer=new Enhancer();  
	        enhancer.setSuperclass(Object.class);  
	        return enhancer.create(Object.class,new UserLazyLoader(this));  
	    }  

	public boolean signIn() {
		boolean isSuccess = false;
		userDAO = new UserDAO();
//		 UserBean userBean = userDAO.query("select * from user where userName='" + userName+"'");
		UserBean userBean = new UserBean();
		userBean.setUserName(userName);
		userBean = userDAO.getObject(userBean);
		if (null != userBean && this.userPassword.equals(userBean.getUserPassword())) {
			isSuccess = true;
		}
		return isSuccess;
	}
	

}
