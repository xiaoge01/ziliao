package com.pro.android_exit;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;

public class MyActivity extends Application {

        //建立一个存放Activity的集合
	public static ArrayList<Activity> activitie = new ArrayList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
	}

        //关闭所有的Activity
	public static void exit() {
		for (int i = 0; i < activitie.size(); i++) {
			activitie.get(i).finish();
			i--;
		}
	}

}
