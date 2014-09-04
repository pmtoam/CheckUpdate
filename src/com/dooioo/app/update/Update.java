package com.dooioo.app.update;

import com.dooioo.app.update.UpdateHandler.UpdateCallback;

import android.app.Activity;

public class Update
{

	/**
	 * Check upgrade.
	 * 
	 * @param activity
	 * @param string
	 */
	public static void check(final Activity activity, String string)
	{
		UpdateHandler updateHandler = new UpdateHandler(activity,
				new UpdateCallback()
				{

					@Override
					public void response(boolean isHasNewVersion,
							UpdateInfo info)
					{
						if (isHasNewVersion)
						{
							UpdateUtils.showUpdateDialog(activity, info);
						}
					}
				});
		updateHandler.execute("MobileWorkbench");
	}

}
