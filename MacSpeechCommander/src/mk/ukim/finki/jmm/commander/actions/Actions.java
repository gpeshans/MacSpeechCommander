package mk.ukim.finki.jmm.commander.actions;

import mk.ukim.finki.jmm.commander.app.MacSpeechCommanderCurrency;
import mk.ukim.finki.jmm.commander.app.MacSpeechCommanderPrices;
import mk.ukim.finki.jmm.commander.app.MacSpeechCommanderWeather;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class Actions {

	public static final String IMENIK = "IMENIK";
	public static final String KAMERA = "KAMERA";
	public static final String ALARM = "ALARM";
	public static final String POVIK = "POVIK";
	public static final String DATUM = "DATUM";
	public static final String PODESUVANJA = "PODESUVANJA";
	public static final String MEJL = "MEJL";
	public static final String INTERNET = "INTERNET";
	public static final String WIFI = "VIFI";
	public static final String MUZIKA = "MUZIKA";
	public static final String KALKULATOR = "KALKULATOR";
	public static final String GALERIJA = "GALERIJA";
	public static final String MAPA = "MAPA";
	public static final String MARKET = "MARKET";
	public static final String BLUTUT = "BLUTUT";
	public static final String KALENDAR = "KALENDAR";
	public static final String GPS = "DZIPIES";
	public static final String PREZEMANJA = "PREZEMANJA";
	public static final String PORAKA = "PORAKA";
	public static final String YOUTUBE = "JUTJUB";
	public static final String VREME = "VREME";
	public static final String VALUTI = "VALUTI";
	public static final String CENI = "CENI";

	public static Intent intent;
	public static Context context;
	public static FragmentActivity activity;
	public static AlertDialog.Builder dialogBuilder;

	public static boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static void executeAction(String action, Context c,
			FragmentActivity a) {

		context = c;
		activity = a;

		dialogBuilder = new AlertDialog.Builder(context)
				.setTitle("Грешка!")
				.setMessage("Не е пронајдена интернет конекција.")
				.setCancelable(false)
				.setPositiveButton("Назад",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

		if (action.equals(Actions.IMENIK)) {
			startImenik();
		} else if (action.equals(Actions.KAMERA)) {
			startKamera();
		} else if (action.equals(Actions.ALARM)) {
			startAlarm();
		} else if (action.equals(Actions.POVIK)) {
			startPovik();
		} else if (action.equals(Actions.DATUM)) {
			startDatum();
		} else if (action.equals(Actions.PODESUVANJA)) {
			startPodesuvanja();
		} else if (action.equals(Actions.MEJL)) {
			startMejl();
		} else if (action.equals(Actions.INTERNET)) {
			startInternet();
		} else if (action.equals(Actions.WIFI)) {
			startWifi();
		} else if (action.equals(Actions.MUZIKA)) {
			startMuzika();
		} else if (action.equals(Actions.KALKULATOR)) {
			startKalkulator();
		} else if (action.equals(Actions.GALERIJA)) {
			startGalerija();
		} else if (action.equals(Actions.MAPA)) {
			startMapa();
		} else if (action.equals(Actions.MARKET)) {
			startMarket();
		} else if (action.equals(Actions.BLUTUT)) {
			startBlutut();
		} else if (action.equals(Actions.KALENDAR)) {
			startKalendar();
		} else if (action.equals(Actions.GPS)) {
			startGps();
		} else if (action.equals(Actions.PREZEMANJA)) {
			startPrezemanja();
		} else if (action.equals(Actions.PORAKA)) {
			startPoraka();
		} else if (action.equals(Actions.YOUTUBE)) {
			startYoutube();
		} else if (action.equals(Actions.VREME)) {
			startVreme();
		} else if (action.equals(Actions.VALUTI)) {
			startValuti();
		} else if (action.equals(Actions.CENI)) {
			startCeni();
		}

	}

	public static void startImenik() {

		intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("content://contacts/people/"));
		activity.startActivity(intent);

	}

	public static void startKamera() {

		intent = new Intent("android.media.action.IMAGE_CAPTURE");
		activity.startActivityForResult(intent, 0);

	}

	public static void startAlarm() {

		PackageManager packageManager = context.getPackageManager();
		Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER);

		// Verify clock implementation
		String clockImpls[][] = {
				{ "HTC Alarm Clock", "com.htc.android.worldclock",
						"com.htc.android.worldclock.WorldClockTabControl" },
				{ "Standar Alarm Clock", "com.android.deskclock",
						"com.android.deskclock.AlarmClock" },
				{ "Froyo Nexus Alarm Clock", "com.google.android.deskclock",
						"com.android.deskclock.DeskClock" },
				{ "Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",
						"com.motorola.blur.alarmclock.AlarmClock" },
				{ "Standar Alarm Clock2", "com.google.android.deskclock",
						"com.android.deskclock.AlarmClock" },
				{ "Samsung Galaxy Clock", "com.sec.android.app.clockpackage",
						"com.sec.android.app.clockpackage.ClockPackage" } };

		boolean foundClockImpl = false;

		for (int i = 0; i < clockImpls.length; i++) {
			String vendor = clockImpls[i][0];
			String packageName = clockImpls[i][1];
			String className = clockImpls[i][2];
			try {
				ComponentName cn = new ComponentName(packageName, className);
				ActivityInfo aInfo = packageManager.getActivityInfo(cn,
						PackageManager.GET_META_DATA);
				alarmClockIntent.setComponent(cn);

				foundClockImpl = true;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (foundClockImpl) {
			activity.startActivity(alarmClockIntent);
		}
	}

	public static void startPovik() {

		intent = new Intent("android.intent.action.DIAL");
		activity.startActivityForResult(intent, 0);

	}

	public static void startDatum() {

		intent = new Intent(android.provider.Settings.ACTION_DATE_SETTINGS);
		activity.startActivityForResult(intent, 0);

	}

	public static void startPodesuvanja() {

		intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		activity.startActivityForResult(intent, 0);

	}

	public static void startMejl() {

		intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("application/octet-stream");
		activity.startActivityForResult(intent, 0);

	}

	public static void startInternet() {

		String url = "http://www.google.com";
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		activity.startActivity(intent);

	}

	public static void startWifi() {

		intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
		activity.startActivityForResult(intent, 0);

	}

	public static void startMuzika() {

		intent = new Intent("android.intent.action.MUSIC_PLAYER");
		activity.startActivityForResult(intent, 0);

	}

	public static void startKalkulator() {

		intent = new Intent();
		intent.setClassName("com.android.calculator2",
				"com.android.calculator2.Calculator");
		activity.startActivity(intent);

	}

	public static void startGalerija() {

		intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, 1);

	}

	public static void startMapa() {

		Location location = null;
		LocationManager locationManager = null;

		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		location = locationManager
				.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();

		intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("geo:" + Double.toString(latitude) + ","
						+ Double.toString(longitude) + "?z=16"));
		activity.startActivity(intent);

	}

	public static void startMarket() {

		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
				.parse("market://search?q=")));

	}

	public static void startBlutut() {

		intent = new Intent();
		intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
		activity.startActivity(intent);

	}

	public static void startKalendar() {

		intent = context.getPackageManager().getLaunchIntentForPackage(
				"com.android.calendar");

		activity.startActivity(intent);

	}

	public static void startGps() {

		intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		activity.startActivityForResult(intent, 0);

	}

	public static void startPrezemanja() {

		intent = context.getPackageManager().getLaunchIntentForPackage(
				"com.android.providers.downloads.ui");
		activity.startActivity(intent);

	}

	public static void startPoraka() {

		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("sms:"));
		activity.startActivityForResult(intent, 1);

	}

	public static void startYoutube() {

		intent = context.getPackageManager().getLaunchIntentForPackage(
				"com.google.android.youtube");
		activity.startActivity(intent);

	}

	public static void startVreme() {

		if (isNetworkAvailable()) {

			intent = new Intent(context, MacSpeechCommanderWeather.class);

			activity.startActivity(intent);
		} else {
			dialogBuilder.show();
		}

	}

	public static void startValuti() {

		if (isNetworkAvailable()) {

			intent = new Intent(context, MacSpeechCommanderCurrency.class);

			activity.startActivity(intent);
		} else {
			dialogBuilder.show();
		}

	}

	public static void startCeni() {

		if (isNetworkAvailable()) {

			intent = new Intent(context, MacSpeechCommanderPrices.class);

			activity.startActivity(intent);
		} else {
			dialogBuilder.show();
		}

	}

}
