package com.dooioo.app.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dooioo.mobile.check.update.R;

public class UpdateUtils
{

	private static final String DOWN_APK_NAME = "NewVersion.apk";
	private static final int HANDLER_DOWNLOAD_SPEED = 1;

	private static ProgressBar dooioo_update_prgbar;
	private static TextView dooioo_update_download_speed_tv;
	private static int localsize = 0;
	private static boolean isUpdate;
	private static Dialog dialog;

	/**
	 * The new version cue.
	 * 
	 * @param activity
	 * @param info
	 */
	public static void showUpdateDialog(final Activity activity,
			final UpdateInfo info)
	{

		if (activity.isFinishing())
			return;

		if (info == null)
			return;

		if (TextUtils.isEmpty(info.appNameCn)
				|| TextUtils.isEmpty(info.versionName)
				|| TextUtils.isEmpty(info.versionInfo)
				|| TextUtils.isEmpty(info.apkUrl))
			return;

		View view = (View) LayoutInflater.from(activity).inflate(
				R.layout.dooioo_update_dlg_layout, null);
		dialog = new AlertDialog.Builder(activity).create();
		dialog.show();
		dialog.getWindow().setContentView(view);

		TextView dooioo_lib_version_name = (TextView) view
				.findViewById(R.id.dooioo_lib_version_name);

		dooioo_lib_version_name
				.setText(info.appNameCn + " " + info.versionName);

		TextView dooioo_lib_description = (TextView) view
				.findViewById(R.id.dooioo_lib_description);

		StringBuilder sb = new StringBuilder();
		String[] strs = info.versionInfo.split(",");
		for (int i = 0; i < strs.length; i++)
		{
			if (i != strs.length - 1)
			{
				sb.append(strs[i]).append("\n");
			}
			else
			{
				sb.append(strs[i]);
			}
		}

		dooioo_lib_description.setText(sb.toString());

		view.findViewById(R.id.dooioo_lib_update).setOnClickListener(
				new android.view.View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						dialog.dismiss();

						if (TextUtils.isEmpty(info.apkUrl))
						{
							Toast.makeText(activity,
									"Download invalid (url is null)",
									Toast.LENGTH_LONG).show();
							return;
						}
						downloadFile(activity, info.apkUrl);
					}
				});

		view.findViewById(R.id.dooioo_lib_not_now).setOnClickListener(
				new android.view.View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						dialog.dismiss();
					}
				});
	}

	/**
	 * Download the file and showing pop-up boxes.
	 * 
	 * @param activity
	 * @param url
	 */
	private static void downloadFile(Activity activity, String url)
	{
		isUpdate = true;

		if (activity.isFinishing())
			return;

		View view = (View) LayoutInflater.from(activity).inflate(
				R.layout.dooioo_update_download_dlg_layout, null);
		dialog = new AlertDialog.Builder(activity).create();
		dialog.show();
		dialog.getWindow().setContentView(view);

		view.findViewById(R.id.dooioo_update_btn_cancel).setOnClickListener(
				new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						isUpdate = false;

						dialog.dismiss();
					}
				});

		dialog.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					isUpdate = false;

					dialog.dismiss();

					return true;
				}
				return false;
			}
		});

		dialog.setCanceledOnTouchOutside(false);

		dooioo_update_prgbar = (ProgressBar) view
				.findViewById(R.id.dooioo_update_prgbar);
		dooioo_update_download_speed_tv = (TextView) view
				.findViewById(R.id.dooioo_update_download_speed_tv);

		executeDownload(activity, url);

	}

	private static Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case HANDLER_DOWNLOAD_SPEED:

				int size = msg.getData().getInt("size");
				String s = (size - localsize) / 10 + "KB/s";
				localsize = size;
				dooioo_update_prgbar.setProgress(size);
				dooioo_update_download_speed_tv.setText(s);

				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	private static void executeDownload(final Activity activity,
			final String downloadUrl)
	{

		new Thread()
		{
			public void run()
			{
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(downloadUrl);
				HttpResponse response = null;
				try
				{
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					Long l = Long.valueOf(length);
					int max = l.intValue();
					dooioo_update_prgbar.setMax(max);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;

					if (is != null)
					{
						File file = new File(
								Environment.getExternalStorageDirectory(),
								DOWN_APK_NAME);

						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1 && isUpdate)
						{
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							Message msg = new Message();
							msg.what = HANDLER_DOWNLOAD_SPEED;
							if (true)
								interrupt();
							msg.getData().putInt("size", count);
							handler.sendMessage(msg);
							if (length > 0)
							{

							}
						}
					}

					if (fileOutputStream != null)
					{
						fileOutputStream.flush();
						fileOutputStream.close();
					}

					if (isUpdate)
					{
						downed(activity, response);
					}

				}
				catch (java.net.UnknownHostException e)
				{
					e.printStackTrace();

					handler.post(new Runnable()
					{
						public void run()
						{

							dialog.dismiss();

							Toast.makeText(activity, "Download invalid (Unknown Host)",
									Toast.LENGTH_LONG).show();
						}
					});
				}
				catch (ClientProtocolException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static void downed(final Activity activity,
			final HttpResponse response)
	{

		handler.post(new Runnable()
		{
			public void run()
			{

				dialog.dismiss();

				if (response != null)
				{
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						installNewVersion(activity);
						activity.finish();
					}
					else
					{
						Toast.makeText(
								activity,
								"Download invalid ("
										+ response.getStatusLine()
												.getStatusCode() + ")",
								Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					Toast.makeText(activity, "Download invalid",
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	private static void installNewVersion(Activity activity)
	{

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), DOWN_APK_NAME)),
				"application/vnd.android.package-archive");
		activity.startActivity(intent);
	}
}
