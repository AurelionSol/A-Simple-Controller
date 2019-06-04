package water.ustc.action;
import sc.ustc.entity.UserBean;

public class LoginAction   {
	private UserBean userBean;

	public String handleLogin(String actionPath) {
		// HttpServletRequest request = SimpleController.getRequest();
		// String userName = request.getParameter("userName");
		// String userPassword = request.getParameter("userPassword");
		// userBean=new UserBean();
		// userBean.setUserName(userName);
		// userBean.setUserPassword(userPassword);
		boolean success = userBean.signIn();
		String result = success ? "success" : "failure";
		return result;
		// System.out.println("Login Success!");
		// return "success";
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	

}
