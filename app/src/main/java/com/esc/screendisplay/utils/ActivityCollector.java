package com.esc.screendisplay.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
	
	public static List<Activity> activities = new ArrayList<>();
	
	public static void addActivity(Activity activity){
		activities.add(activity);
	}
	
	public static void removeActivity(Activity activity){
		activities.remove(activity);
	}
	
	public static void finishAll(){
		for (Activity activity : activities) {
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}

	public static Activity getTopActivity()
	{
		Activity activity = null;
		if(activities.size() > 0)
		{
			return activities.get(activities.size() - 1);
		}
		return activity;
	}
}
