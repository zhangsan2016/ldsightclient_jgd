package com.ldsight.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.ldsightclient_jgd.R;

public class CatalogAct extends Activity implements OnClickListener {

	private Button brighten;
	private Button illumination;
	private Button monitoring;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.catalog_activity);

		findViews();

		// 关闭照明功能
		illumination.setBackgroundColor(0xFF999999);


	}

	private void findViews() {
		brighten = (Button) findViewById(R.id.brighten);
		illumination = (Button) findViewById(R.id.illumination);
		monitoring = (Button) findViewById(R.id.monitoring);

		brighten.setOnClickListener(this);
		illumination.setOnClickListener(this);
		monitoring.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v == illumination) {  // 照明

			Intent intent = new Intent(CatalogAct.this, ParameterAct.class);
			intent.putExtra(ParameterAct.FRAGMENT_FLAG, ParameterAct.MAIN);
			startActivity(intent);

		} else if (v == brighten) { // 亮化

			Intent intent = new Intent(CatalogAct.this, BrightenMain.class);
			startActivity(intent);

		} else if (v == monitoring) {

			Intent intent = new Intent(CatalogAct.this, MonitorMainAct.class);
			startActivity(intent); // 监控

		}
	}

}
