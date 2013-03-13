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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MacSpeechCommanderHome extends Activity implements
		OnTouchListener, RecognitionListener {
	static {
		System.loadLibrary("pocketsphinx_jni");
	}

	public static final String APP_NAME = "MacSpeechCommander";
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

	/**
	 * Respond to touch events on the Speak button.
	 * 
	 * This allows the Speak button to function as a "push and hold" button, by
	 * triggering the start of recognition when it is first pushed, and the end
	 * of recognition when it is released.
	 * 
	 * @param v
	 *            View on which this event is called
	 * @param event
	 *            Event that was triggered.
	 */
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
				this.rec_dialog = ProgressDialog.show(
						MacSpeechCommanderHome.this, "",
						"Препознава говор...", true);
				this.rec_dialog.setCancelable(false);
				this.listening = false;
			}
			this.rec.stop();
			break;
		default:
			;
		}
		/* Let the button handle its own state */
		return false;
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		File f = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/" + APP_NAME + "/model");
		if (!f.isDirectory())
			copyAssets();

		this.rec = new RecognizerTask();
		this.rec_thread = new Thread(this.rec);
		this.listening = false;
		Button b = (Button) findViewById(R.id.Button01);
		b.setOnTouchListener(this);
		this.performance_text = (TextView) findViewById(R.id.PerformanceText);
		this.edit_text = (EditText) findViewById(R.id.EditText01);
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();
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

	/** Called when partial results are generated. */
	public void onPartialResults(Bundle b) {
		final MacSpeechCommanderHome that = this;
		final String hyp = b.getString("hyp");
		that.edit_text.post(new Runnable() {
			public void run() {
				that.edit_text.setText(hyp);
			}
		});
	}

	/** Called with full results are generated. */
	public void onResults(Bundle b) {
		final String hyp = b.getString("hyp");
		final MacSpeechCommanderHome that = this;
		this.edit_text.post(new Runnable() {
			public void run() {
				that.edit_text.setText(hyp);
				Date end_date = new Date();
				long nmsec = end_date.getTime() - that.start_date.getTime();
				float rec_dur = (float) nmsec / 1000;
				that.performance_text.setText(String.format(
						"%.2f секунди %.2f xRT", that.speech_dur, rec_dur
								/ that.speech_dur));
				Log.d(getClass().getName(), "Hiding Dialog");
				that.rec_dialog.dismiss();
			}
		});
	}

	public void onError(int err) {
		final MacSpeechCommanderHome that = this;
		that.edit_text.post(new Runnable() {
			public void run() {
				that.rec_dialog.dismiss();
			}
		});
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
	
	private void deleteModel()
	{
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
}