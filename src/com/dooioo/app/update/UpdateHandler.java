package com.dooioo.app.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * 
 * Common class for check update.
 * 
 * @author PMTOAM
 * 
 */
public final class UpdateHandler extends AsyncTask<String, Integer, UpdateInfo>
{

	private static final String REQUEST_URL = "http://myclients.duapp.com/mysqlquery2/RemoteVersionInfo?appName=";

	private Context context;
	private UpdateCallback updateCallback;

	public UpdateHandler(Context context, UpdateCallback updateCallback)
	{
		this.context = context;
		this.updateCallback = updateCallback;
	}

	@Override
	protected UpdateInfo doInBackground(String... params)
	{
		UpdateInfo appVersionInfoEntity = getRemoteVersionInfo(context,
				params[0]);
		return appVersionInfoEntity;
	}

	@Override
	protected void onPostExecute(UpdateInfo result)
	{
		super.onPostExecute(result);

		if (result != null)
		{

			try
			{
				PackageManager pm = context.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

				if (pi.versionCode < result.versionCode)
				{
					updateCallback.response(true, result);
				}
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	private UpdateInfo getRemoteVersionInfo(Context context, String appName)
	{
		String urlStr = REQUEST_URL + appName;

		UpdateInfo appVersionInfoEntity = null;
		BufferedReader br = null;

		try
		{
			URL url = new URL(urlStr);
			InputStreamReader isr = new InputStreamReader(url.openStream());
			br = new BufferedReader(isr);

			String str = null;
			while ((str = br.readLine()) != null)
			{

				if (!TextUtils.isEmpty(str) && str.length() > 10)
				{
					String[] appVersionInfos = str.split(" ");

					if (appVersionInfos.length > 0)
					{
						appVersionInfoEntity = new UpdateInfo();
						appVersionInfoEntity.appName = appVersionInfos[0];
						appVersionInfoEntity.appNameCn = appVersionInfos[1];
						appVersionInfoEntity.versionCode = Integer
								.parseInt(appVersionInfos[2]);
						appVersionInfoEntity.versionName = appVersionInfos[3];
						appVersionInfoEntity.apkUrl = appVersionInfos[4];
						appVersionInfoEntity.versionInfo = appVersionInfos[5];
					}
				}
			}

			isr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return appVersionInfoEntity;
	}

	public interface UpdateCallback
	{
		/**
		 * The version update return information.
		 * 
		 * @param isHasNewVersion
		 *            Has new version's application.
		 * @param info
		 *            Some applications include relevant information needed to
		 *            upgrade.
		 */
		public void response(boolean isHasNewVersion, UpdateInfo info);
	}

}
