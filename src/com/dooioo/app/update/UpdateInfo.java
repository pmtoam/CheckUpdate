package com.dooioo.app.update;

/**
 * Informations entity of application upgrade. 
 * 
 * @author PMTOAM
 *
 */
public final class UpdateInfo
{
	/**
	 * Name of android application.
	 */
	public String appName;

	/**
	 * Chinese name of android application.
	 */
	public String appNameCn;

	/**
	 * Version code of android application.
	 */
	public int versionCode;

	/**
	 * Version name of android application.
	 */
	public String versionName;

	/**
	 * Android application file download address.
	 */
	public String apkUrl;

	/**
	 * Update log and new function.<br>
	 * <br>
	 * like:[1.xxxxxxx,2.xxxxxxxxxx,3.xxxxxxxxxxx]
	 */
	public String versionInfo;

	@Override
	public String toString()
	{
		return "AppVersionInfoEntity [appName=" + appName + ", appNameCn="
				+ appNameCn + ", versionCode=" + versionCode + ", versionName="
				+ versionName + ", apkUrl=" + apkUrl + ", versionInfo="
				+ versionInfo + "]";
	}

}
