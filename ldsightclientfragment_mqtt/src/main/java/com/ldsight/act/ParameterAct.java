package com.ldsight.act;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ldsightclient_jgd.R;
import com.ldsight.entity.xinjiang.LoginJson;
import com.ldsight.fragment.BaiDuMapFragment;
import com.ldsight.fragment.MainFragment;
import com.ldsight.fragment.SettingFragment;
import com.ldsight.fragment.SystemLogFragment;
import com.ldsight.fragment.TestPatternFragment;
import com.ldsight.service.ZkyOnlineService;

public class ParameterAct extends FragmentActivity {
	public static int SYSTEM_LOG = 1;
	public static int TEST_PATTERN = 2;
	public static int SETTING = 3;
	public static int MAIN = 4;
	public static int MAP = 5;
	public static String FRAGMENT_FLAG = "fragment_flag";
	private ImageView mOneImage, mTwoImage, mThreeImage, mFourImage,
			mFiveImage;
	private TextView mOneText, mTwoText, mThreeText, mFourText, mFiveText;
	// Fragment界面
	private SystemLogFragment systemLogFragment;
	private TestPatternFragment testPatternFragment;
	private SettingFragment settingFragment;
	private MainFragment mainFragment;
	private BaiDuMapFragment mapFragment;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.parameter_main);
		// 初始化视图
		initView();


		// 获取传递过来的参数
		int fragmentFlag = getIntent().getIntExtra(FRAGMENT_FLAG, SYSTEM_LOG);
		final LoginJson loginInfo = (LoginJson) getIntent().getSerializableExtra("loginInfo");

		if (fragmentFlag == SYSTEM_LOG) {
			// 在程序中加入Fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			if (systemLogFragment == null) {
				systemLogFragment = new SystemLogFragment();
			}
			fragmentTransaction.add(R.id.fragment_layout, systemLogFragment);
			fragmentTransaction.commit();
		} else if (fragmentFlag == TEST_PATTERN) {
			// 在程序中加入Fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			if (testPatternFragment == null) {
				testPatternFragment = (TestPatternFragment) TestPatternFragment.newInstance(ParameterAct.this,loginInfo);
			}
			// 传递登录对象到fragment
			/*Bundle args = new Bundle();
			args.putSerializable("loginInfo", loginInfo);*/
			fragmentTransaction.add(R.id.fragment_layout, testPatternFragment);
			fragmentTransaction.commit();
		} else if (fragmentFlag == SETTING) {
			// 在程序中加入Fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			if (settingFragment == null) {
				settingFragment = new SettingFragment();
			}

			fragmentTransaction.add(R.id.fragment_layout, settingFragment);
			fragmentTransaction.commit();
		} else if (fragmentFlag == MAIN) {
			// 重置图片和字体颜色
			resetImageAndText();
			mOneImage.setImageResource(R.drawable.parameter_main1);
			mOneText.setTextColor(Color.parseColor("#00B28A"));
			// 在程序中加入Fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			if (mainFragment == null) {
				mainFragment = new MainFragment();
			}
			// 传递登录对象到fragment
			Bundle args = new Bundle();
			args.putSerializable("loginInfo", loginInfo);
			mainFragment.setArguments(args);

			fragmentTransaction.add(R.id.fragment_layout, mainFragment);
			fragmentTransaction.commit();
		} else if (fragmentFlag == MAP) {
			// 重置图片和字体颜色
			resetImageAndText();
			mTwoImage.setImageResource(R.drawable.parameter_main2);
			mTwoText.setTextColor(Color.parseColor("#00B28A"));
			// 在程序中加入Fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			if (mapFragment == null) {
				mapFragment = new BaiDuMapFragment();
			}

			fragmentTransaction.add(R.id.fragment_layout, mapFragment);
			fragmentTransaction.commit();
		}

		LinearLayout systemLogLayout = (LinearLayout) findViewById(R.id.system_log_layout);
		systemLogLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 重置图片和字体颜色
				resetImageAndText();
				mFourImage.setImageResource(R.drawable.parameter_log1);
				mFourText.setTextColor(Color.parseColor("#00B28A"));
				SystemLogFragment systemLogFragment = new SystemLogFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_layout, systemLogFragment)
						.commit();
			}
		});

		LinearLayout testPatternLayout = (LinearLayout) findViewById(R.id.test_pattern_layout);
		testPatternLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 重置图片和字体颜色
				resetImageAndText();
				mThreeImage.setImageResource(R.drawable.parameter_test1);
				mThreeText.setTextColor(Color.parseColor("#00B28A"));
				if(testPatternFragment == null){
					testPatternFragment = (TestPatternFragment) TestPatternFragment.newInstance(ParameterAct.this,loginInfo);
				}
			//	TestPatternFragment testPatternFragment = new TestPatternFragment(ParameterAct.this);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_layout, testPatternFragment)
						.commit();
			}
		});
		LinearLayout settingLayout = (LinearLayout) findViewById(R.id.setting_layout);
		settingLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 重置图片和字体颜色
				resetImageAndText();
				mFiveImage.setImageResource(R.drawable.parameter_setting1);
				mFiveText.setTextColor(Color.parseColor("#00B28A"));
				SettingFragment settingFragment = new SettingFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_layout, settingFragment)
						.commit();
			}
		});

		LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_pattern_layout);
		main_layout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 重置图片和字体颜色
				resetImageAndText();

				mOneImage.setImageResource(R.drawable.parameter_main1);
				mOneText.setTextColor(Color.parseColor("#00B28A"));
				MainFragment mainFragment = new MainFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_layout, mainFragment).commit();
			}
		});

		LinearLayout map_layout = (LinearLayout) findViewById(R.id.map_pattern_layout);
		map_layout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 重置图片和字体颜色
				resetImageAndText();
				mTwoImage.setImageResource(R.drawable.parameter_location1);
				mTwoText.setTextColor(Color.parseColor("#00B28A"));
				BaiDuMapFragment mapFragment = new BaiDuMapFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_layout, mapFragment).commit();
			}
		});
	}

	private void initView() {
		mOneImage = (ImageView) this.findViewById(R.id.iv_tab_1);
		mTwoImage = (ImageView) this.findViewById(R.id.iv_tab_2);
		mThreeImage = (ImageView) this.findViewById(R.id.iv_tab_3);
		mFourImage = (ImageView) this.findViewById(R.id.iv_tab_4);
		mFiveImage = (ImageView) this.findViewById(R.id.iv_tab_5);

		mOneText = (TextView) this.findViewById(R.id.tv_tab_1);
		mTwoText = (TextView) this.findViewById(R.id.tv_tab_2);
		mThreeText = (TextView) this.findViewById(R.id.tv_tab_3);
		mFourText = (TextView) this.findViewById(R.id.tv_tab_4);
		mFiveText = (TextView) this.findViewById(R.id.tv_tab_5);

	}

	// 重置图片为暗色
	private void resetImageAndText() {
		mOneImage.setImageResource(R.drawable.parameter_main2);
		mOneText.setTextColor(Color.parseColor("#C3C3C3"));

		mTwoImage.setImageResource(R.drawable.parameter_location2);
		mTwoText.setTextColor(Color.parseColor("#C3C3C3"));

		mThreeImage.setImageResource(R.drawable.parameter_test2);
		mThreeText.setTextColor(Color.parseColor("#C3C3C3"));

		mFourImage.setImageResource(R.drawable.parameter_log2);
		mFourText.setTextColor(Color.parseColor("#C3C3C3"));

		mFiveImage.setImageResource(R.drawable.parameter_setting2);
		mFiveText.setTextColor(Color.parseColor("#C3C3C3"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭心跳服务
		Intent stopIntent = new Intent(this,ZkyOnlineService.class);
		stopService(stopIntent);
	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			flag = true;
			super.handleMessage(msg);
		}
	};

	public boolean flag = true;
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == event.KEYCODE_BACK) {

			if (flag) {
				flag = false;
				handler.sendEmptyMessageDelayed(0, 2000);
				Toast.makeText(this,"再按一次退出！",Toast.LENGTH_SHORT).show();
				return true;
			}

		}


		return super.onKeyUp(keyCode, event);
	}


}