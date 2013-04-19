package mk.ukim.finki.jmm.commander.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.ss.util.DateFormatConverter;

import mk.ukim.finki.jmm.commander.R;
import mk.ukim.finki.jmm.commander.pocketsphinx.RecognitionListener;
import mk.ukim.finki.jmm.commander.pocketsphinx.RecognizerTask;
import android.app.ActionBar;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import mk.ukim.finki.jmm.commander.actions.Actions;

public class MacSpeechCommanderStartActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static final String APP_NAME = "MacSpeechCommander";

	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File f = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/" + APP_NAME + "/model");
		if (!f.isDirectory())
			copyAssets();

		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);
		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between different app sections, select
						// the corresponding tab.
						// We can also use ActionBar.Tab#select() to do this if
						// we have a reference to the
						// Tab.
						actionBar.setSelectedNavigationItem(position);
					}
				});

		actionBar.addTab(actionBar.newTab().setText("Команди")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Сервиси")
				.setTabListener(this));
	}

	@Override
	protected void onStop() {
		super.onStop();
		deleteRawLogDirFiles();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearResources();
	}

	private void copyAssets() {
		File voiceCommander = new File(Environment
				.getExternalStorageDirectory().getPath()
				+ "/"
				+ APP_NAME
				+ "/model");

		voiceCommander.mkdirs();

		File rawLogDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + APP_NAME + "/rawLogDir");
		rawLogDir.mkdirs();

		AssetManager assetManager = getAssets();
		String[] files = { "commands.dic", "commands.lm.DMP", "feat.params",
				"mdef", "means", "mixture_weights", "noisedict",
				"transition_matrices", "variances" };

		for (int i = 2; i < 9; i++) {
			String filename = "";
			InputStream in = null;
			OutputStream out = null;
			try {
				filename = files[i];
				in = assetManager.open(filename);
				out = new FileOutputStream(Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ APP_NAME + "/model/" + filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}

		for (int i = 0; i < 2; i++) {
			String filename = "";
			InputStream in = null;
			OutputStream out = null;
			try {
				filename = files[i];
				in = assetManager.open(filename);
				out = new FileOutputStream(Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ APP_NAME + "/" + filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private void deleteRawLogDirFiles() {
		File rawLogDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + APP_NAME + "/rawLogDir");
		if (rawLogDir.isDirectory()) {
			String[] files = rawLogDir.list();
			for (String file : files) {
				File f = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/" + APP_NAME + "/rawLogDir/" + file);
				f.delete();
			}
		}
	}

	private void deleteModel() {
		File model = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + APP_NAME + "/model");
		if (model.isDirectory()) {
			String[] files = model.list();
			for (String file : files) {
				File f = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/" + APP_NAME + "/model/" + file);
				f.delete();
			}
		}
	}

	private void clearResources() {
		deleteRawLogDirFiles();
		deleteModel();

		File mainFolder = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + APP_NAME);
		if (mainFolder.isDirectory()) {
			String[] files = mainFolder.list();
			for (String file : files) {
				File f = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/" + APP_NAME + "/" + file);
				f.delete();
			}
		}

		mainFolder.delete();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				// The first section of the app is the most interesting -- it
				// offers
				// a launchpad into the other demonstrations in this example
				// application.
				return new LaunchpadSectionFragment();
			case 1:
				return new ServiceSectionFragment();
			default:
				// The other sections of the app are dummy placeholders.
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Section " + (position + 1);
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_section_dummy,
					container, false);
			Bundle args = getArguments();
			((TextView) rootView.findViewById(android.R.id.text1))
					.setText(getString(R.string.dummy_section_text,
							args.getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	public static class ServiceSectionFragment extends Fragment {

		private AlertDialog.Builder dialogBuilder;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.services,
					container, false);

			dialogBuilder = new AlertDialog.Builder(rootView.getContext())
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

			Button weatherButton = (Button) rootView.findViewById(R.id.Weather);
			weatherButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (isNetworkAvailable()) {
						Intent intent = new Intent(rootView.getContext(),
								MacSpeechCommanderWeather.class);

						startActivity(intent);
					} else {
						dialogBuilder.show();
					}

				}
			});

			Button currencyButton = (Button) rootView
					.findViewById(R.id.Currencies);
			currencyButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isNetworkAvailable()) {
						Intent intent = new Intent(rootView.getContext(),
								MacSpeechCommanderCurrency.class);

						startActivity(intent);
					} else {
						dialogBuilder.show();
					}
				}
			});

			Button pricesButton = (Button) rootView.findViewById(R.id.Prices);
			pricesButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isNetworkAvailable()) {
						Intent intent = new Intent(rootView.getContext(),
								MacSpeechCommanderPrices.class);

						startActivity(intent);
					} else {
						dialogBuilder.show();
					}
				}
			});

			return rootView;
		}

		public boolean isNetworkAvailable() {
			ConnectivityManager connectivityManager = (ConnectivityManager) getView()
					.getContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}

		BroadcastReceiver wifiReciever = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();

				if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
					NetworkInfo info = (NetworkInfo) intent
							.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
					if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
						Intent intentWeather = new Intent(getActivity()
								.getApplicationContext(),
								MacSpeechCommanderWeather.class);
						startActivity(intentWeather);
					}
				}
			};
		};

	}

	/*
	 * public static class HistorySectionFragment extends Fragment {
	 * 
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { View rootView = inflater
	 * .inflate(R.layout.history, container, false); return rootView; }
	 * 
	 * }
	 */
	public static class LaunchpadSectionFragment extends Fragment implements
			RecognitionListener {
		static {
			System.loadLibrary("pocketsphinx_jni");
		}

		/**
		 * Recognizer task, which runs in a worker thread.
		 */
		RecognizerTask rec;
		/**
		 * Thread in which the recognizer task runs.
		 */
		Thread rec_thread;
		/**
		 * Time at which current recognition started.
		 */
		Date start_date;
		/**
		 * Number of seconds of speech.
		 */
		float speech_dur;
		/**
		 * Are we listening?
		 */
		boolean listening;
		/**
		 * Progress dialog for final recognition.
		 */
		ProgressDialog rec_dialog;
		/**
		 * Performance counter view.
		 */
		TextView performance_text;
		/**
		 * Editable text view.
		 */
		EditText edit_text;

		TextView result_text;

		Button btnSpeak;

		Boolean flagSpeak;

		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_section_launchpad, container, false);

			this.btnSpeak = (Button) rootView.findViewById(R.id.BtnSpeak);
			this.btnSpeak.setOnClickListener(btnSpeakListener);			

			fillMap();

			this.start_date = new Date();

			this.rec = new RecognizerTask();

			this.listening = false;
			flagSpeak = false;

			this.performance_text = (TextView) rootView
					.findViewById(R.id.PerformanceText);

			this.edit_text = (EditText) rootView.findViewById(R.id.ResultEdit);

			this.rec.setRecognitionListener(this);

			this.rec_thread = new Thread(this.rec);

			this.rec_thread.start();

			return rootView;
		}

		public static HashMap<String, String> actionsMac;

		public static void fillMap() {

			actionsMac = new HashMap<String, String>();

			actionsMac.put("ALARM", "АЛАРМ");
			actionsMac.put("VREME", "ВРЕМЕ");
			actionsMac.put("VALUTI", "ВАЛУТИ");
			actionsMac.put("CENI", "ЦЕНИ");
			actionsMac.put("MARKET", "МАРКЕТ");
			actionsMac.put("MAPA", "МАПA");
			actionsMac.put("DZIPIES", "GPS");
			actionsMac.put("DATUM", "ДАТУМ");
			actionsMac.put("BLUTUT", "BLUETOOTH");
			actionsMac.put("VIFI", "Wi-Fi");
			actionsMac.put("PODESUVANJA", "ПОДЕСУВАЊА");
			actionsMac.put("POVIK", "ПОВИК");
			actionsMac.put("IMENIK", "ИМЕНИК");
			actionsMac.put("MEJL", "МЕJЛ");
			actionsMac.put("MUZIKA", "МУЗИКА");
			actionsMac.put("PORAKA", "ПОРАКA");
			actionsMac.put("INTERNET", "ИНТЕРНЕТ");
			actionsMac.put("GALERIJA", "ГАЛЕРИЈА");
			actionsMac.put("PREZEMANJA", "ПРЕЗЕМАЊА");
			actionsMac.put("KAMERA", "КАМЕРА");
			actionsMac.put("KALENDAR", "КАЛЕНДАР");
			actionsMac.put("KALKULATOR", "КАЛКУЛАТОР");
			actionsMac.put("JUTJUB", "YouTube");

		}

		@Override
		public void onStop() {
			super.onStop();
			rec.stop();
		}

		public void onPause() {
			super.onPause();
			rec.stop();
		};

		OnClickListener btnSpeakListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!flagSpeak) {
					start_date = new Date();
					listening = true;

					flagSpeak = true;
					edit_text.setText("Зборувајте...");

					rec.start();
				} else {

					Date end_date = new Date();
					long nmsec = end_date.getTime() - start_date.getTime();
					speech_dur = (float) nmsec / 1000;
					if (listening) {
						Log.d(getClass().getName(), "Showing Dialog");
						rec_dialog = ProgressDialog.show(
								getView().getContext(), "", "Процесира...",
								true);
						rec_dialog.setCancelable(false);
						listening = false;
						flagSpeak = false;
					}
					rec.stop();

				}
			}
		};

		@Override
		public void onPartialResults(Bundle b) {
			final LaunchpadSectionFragment that = this;
			final String hyp = b.getString("hyp");

			that.edit_text.post(new Runnable() {
				public void run() {

					if (hyp != null) {
						String tempRes = hyp;

						// removes words shorter than 3 letters
						tempRes = tempRes.replaceAll("\\b[\\w']{1,2}\\b", "");
						tempRes = tempRes.replaceAll("\\s{2,}", " ");

						String results[] = tempRes.split(" ");
						String finalRes = "";
						for (int i = 0; i < results.length; i++) {
							results[i] = actionsMac.get(results[i]);
							if (results[i] != null)
								finalRes += results[i] + " ";
						}

						that.edit_text.setText(finalRes);
					}

				}
			});

		}

		@Override
		public void onResults(Bundle b) {

			String tempRes = b.getString("hyp");

			if (tempRes != null) {

				// removes words shorter than 3 letters
				tempRes = tempRes.replaceAll("\\b[\\w']{1,2}\\b", "");
				tempRes = tempRes.replaceAll("\\s{2,}", " ");
			}

			String results[] = tempRes.split(" ");

			final String hyp;

			if (results.length > 0)
				hyp = results[results.length - 1];
			else
				hyp = "";

			final LaunchpadSectionFragment that = this;
			this.edit_text.post(new Runnable() {
				public void run() {

					that.edit_text.setText(actionsMac.get(hyp));
					Date end_date = new Date();
					long nmsec = end_date.getTime() - that.start_date.getTime();
					float rec_dur = (float) nmsec / 1000;
					that.performance_text.setText(String.format(
							"%.2f секунди %.2f xRT", that.speech_dur, rec_dur
									/ that.speech_dur));
					Log.d(getClass().getName(), "Hiding Dialog");

					if (that.rec_dialog != null)
						that.rec_dialog.dismiss();

					callAction(hyp);

				}
			});

		}

		@Override
		public void onError(int err) {
			final LaunchpadSectionFragment that = this;
			that.edit_text.post(new Runnable() {
				public void run() {
					that.rec_dialog.dismiss();
				}
			});

		}

		public void callAction(String action) {

			if (action != null) {

				rec.stop();
				flagSpeak = false;

				if (!actionsMac.containsKey(action))
					return;

				String toastText = actionsMac.get(action).toString();

				Toast.makeText(getActivity().getBaseContext(), toastText,
						Toast.LENGTH_LONG).show();

				Actions.executeAction(action, getView().getContext(),
						getActivity());

			}

		}

		/*
		 * private class ActionTask extends AsyncTask<URL, Integer, String> {
		 * 
		 * private ProgressDialog progressDialog;
		 * 
		 * @Override protected String doInBackground(URL... params) {
		 * 
		 * return ""; }
		 * 
		 * @Override protected void onPreExecute() { super.onPreExecute();
		 * progressDialog = new ProgressDialog(getView().getContext());
		 * progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 * progressDialog.setCancelable(false);
		 * progressDialog.setMessage("Процесира...");
		 * progressDialog.setIndeterminate(true); progressDialog.show(); }
		 * 
		 * @Override protected void onProgressUpdate(Integer... values) {
		 * super.onProgressUpdate(values); }
		 * 
		 * @Override protected void onPostExecute(String result) {
		 * super.onPostExecute(result); if (progressDialog.isShowing())
		 * progressDialog.dismiss(); } }
		 */
	}

}
