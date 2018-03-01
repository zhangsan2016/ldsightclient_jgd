package com.ldsight.entity;

/**
 * 单例模式，用来验证用户权限
 * @author Administrator
 *
 */
public class CheckUser {
	private CheckUser(){}

	private  static CheckUser cacheUser;


	private String userName;
	private String userpwd;
	private int jurisdiction;


	public static CheckUser getInstance(){
		if(cacheUser != null){
			return cacheUser;
		}
		return cacheUser = new CheckUser();

	}



	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserpwd() {
		return userpwd;
	}


	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}


	public int getJurisdiction() {
		return jurisdiction;
	}


	public void setJurisdiction(int jurisdiction) {
		this.jurisdiction = jurisdiction;
	}






}
