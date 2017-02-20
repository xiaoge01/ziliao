package com.pro.android_exit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ThridActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thrid);
                //每次进入Activity,都添加到集合中
		MyActivity.activitie.add(this);
	}

	 public void jump03(View v) {
	 startActivity(new Intent(this, SceondActivity.class));
	 }

        //点击退出关闭所有的Activity
	public void exit(View v) {
		MyActivity.exit();
	}
        //按返回键只将当前的Activity从集合中移除
	@Override
	public void finish() {
		super.finish();
		MyActivity.activitie.remove(this);
	}
}
