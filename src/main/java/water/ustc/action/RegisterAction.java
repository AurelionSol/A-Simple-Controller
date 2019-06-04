package water.ustc.action;

import javax.servlet.http.HttpServletRequest;

import sc.ustc.controller.SimpleController;

public class RegisterAction   {
	private String username;
	private String password;
	private String mail;
	
	public String handleRegister(String actionPath){
		System.out.println("Registe Success!");
		return "success";
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
}
