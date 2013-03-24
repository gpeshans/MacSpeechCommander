package mk.ukim.finki.jmm.commander.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import mk.ukim.finki.jmm.commander.R;
import mk.ukim.finki.jmm.commander.pocketsphinx.RecognitionListener;
import mk.ukim.finki.jmm.commander.pocketsphinx.RecognizerTask;
import android.app.ActionBar;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
		actionBar.addTab(actionBar.newTab().setText("Историја")
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
		String[] files = { "azbuka.dic", "azbuka.lm.DMP", "feat.params",
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
			case 2:
				return new HistorySectionFragment();
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
			return 3;
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

			/*
			 * rootView.getContext().registerReceiver(this.wifiReciever, new
			 * IntentFilter(wifiManager.NETWORK_STATE_CHANGED_ACTION));
			 */

			dialogBuilder = new AlertDialog.Builder(rootView.getContext())
					.setTitle("Исклучен Wi-fi")
					.setMessage("Грешка!!!")
					.setPositiveButton("Во ред",
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

	public static class HistorySectionFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater
					.inflate(R.layout.history, container, false);
			return rootView;
		}

	}

	public static class LaunchpadSectionFragment extends Fragment implements
			OnTouchListener, RecognitionListener {
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

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_section_launchpad, container, false);

			this.rec = new RecognizerTask();

			this.rec_thread = new Thread(this.rec);
			this.listening = false;
			Button b = (Button) rootView.findViewById(R.id.BtnSpeak);

			b.setOnTouchListener(this);
			this.performance_text = (TextView) rootView
					.findViewById(R.id.PerformanceText);

			this.edit_text = (EditText) rootView.findViewById(R.id.ResultEdit);

			this.rec.setRecognitionListener(this);
			this.rec_thread.start();

			return rootView;
		}

		@Override
		public void onPartialResults(Bundle b) {
			final LaunchpadSectionFragment that = this;
			final String hyp = b.getString("hyp");
			that.edit_text.post(new Runnable() {
				// that.result_text.post(new Runnable() {
				public void run() {
					that.edit_text.setText(hyp);
					// that.result_text.setText(hyp);
				}
			});
		}

		@Override
		public void onResults(Bundle b) {

			final String hyp = b.getString("hyp");
			final LaunchpadSectionFragment that = this;
			this.edit_text.post(new Runnable() {
				// this.result_text.post(new Runnable() {
				public void run() {
					that.edit_text.setText(hyp);
					// that.result_text.setText(hyp);

					Date end_date = new Date();
					long nmsec = end_date.getTime() - that.start_date.getTime();
					float rec_dur = (float) nmsec / 1000;
					that.performance_text.setText(String.format(
							"%.2f seconds %.2f xRT", that.speech_dur, rec_dur
									/ that.speech_dur));
					Log.d(getClass().getName(), "Hiding Dialog");

					/*
					 * if (hyp.equals("I")) { Intent intent = new
					 * Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
					 * startActivityForResult(intent, 1); }
					 * 
					 * if (hyp.equals("O")) { Intent intent = new Intent(
					 * MediaStore.ACTION_IMAGE_CAPTURE);
					 * startActivityForResult(intent, 2500); }
					 */
					that.rec_dialog.dismiss();
				}
			});

		}

		@Override
		public void onError(int err) {
			final LaunchpadSectionFragment that = this;
			that.edit_text.post(new Runnable() {
				// that.result_text.post(new Runnable() {
				public void run() {
					that.rec_dialog.dismiss();
				}
			});

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				start_date = new Date();
				this.listening = true;
				this.rec.start();
				break;
			case MotionEvent.ACTION_UP:
				Date end_date = new Date();
				long nmsec = end_date.getTime() - start_date.getTime();
				this.speech_dur = (float) nmsec / 1000;
				if (this.listening) {
					Log.d(getClass().getName(), "Showing Dialog");
					this.rec_dialog = ProgressDialog.show(getView()
							.getContext(), "", "Recognizing speech...", true);
					this.rec_dialog.setCancelable(false);
					this.listening = false;
				}
				this.rec.stop();
				break;
			default:
				;
			}
			return false;
		}
	}

}
