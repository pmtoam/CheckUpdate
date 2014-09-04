package com.dooioo.mobile.check.update;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.dooioo.app.update.Update;

public class MainActivity extends Activity
{

	Activity activity = this;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Update.check(activity, "MobileWorkbench");

	}

}
