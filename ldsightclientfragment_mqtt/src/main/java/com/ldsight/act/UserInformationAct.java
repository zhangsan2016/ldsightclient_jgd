package com.ldsight.act;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.CheckUser;

public class UserInformationAct extends Activity {
	/**
	 * 提交
	 */
	private Button complete;
	/**
	 *  号码
	 */
	private EditText number;
	/*
	 *  密码
	 */
	private EditText userPassword;
	/*
	 *  确认密码
	 */
	private EditText checkUserPassword;
	/*
	 *  职称
	 */
	private EditText title;
	/*
	 * 工作单位
	 */
	private EditText workUnits;
	/*
	 *  联系方式
	 */
	private EditText contact;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_information);

		// 初始化视图
		InitView();

		// 获取当前用户信息
		CheckUser user = CheckUser.getInstance();
		// 设置当前用户名
		number.setText(user.getUserName());


		// 测试
		System.out.println("权限等级：" +  user.getJurisdiction() + "用户名：" + user.getUserName() + "用户密码：" + user.getUserpwd());

		complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				String mobiles = number.getText().toString().trim();
//				Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//				Matcher m = p.matcher(mobiles);
//				System.out.println(m.matches()+"---");

				System.out.println("号码：" + number.getText().toString() +
						"密码：" + userPassword.getText().toString() +
						"确认密码：" + checkUserPassword.getText().toString() +
						"职称:" + title.getText().toString()  +
						"工作单位：" + workUnits.getText().toString() +
						"联系方式" + contact.getText().toString());

				checkUserPassword.requestFocus();

				// 判断输入的两次密码是否一致
				String pwd = userPassword.getText().toString();
				String pwd2 = checkUserPassword.getText().toString();
				if(!pwd.equals(pwd2)){
					showToast("兩次密碼不一致");
					userPassword.requestFocus();
					return;
				}
			}
		});




	}

	/**
	 *  初始化View
	 */
	private void InitView(){
		complete = (Button) this.findViewById(R.id.complete);
		number = (EditText) this.findViewById(R.id.userName);
		userPassword = (EditText) this.findViewById(R.id.et_userPassword);
		checkUserPassword = (EditText) this.findViewById(R.id.et_checkUserPassword);
		title = (EditText) this.findViewById(R.id.et_title);
		workUnits = (EditText) this.findViewById(R.id.et_work_units);
		contact = (EditText) this.findViewById(R.id.et_contact);

	}

	private void showToast(String msg) {
		Toast.makeText(UserInformationAct.this, msg, Toast.LENGTH_LONG).show();
	}

}
