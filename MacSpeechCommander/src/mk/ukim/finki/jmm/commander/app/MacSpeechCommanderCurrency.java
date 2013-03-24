package mk.ukim.finki.jmm.commander.app;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mk.ukim.finki.jmm.commander.services.Currency;
import mk.ukim.finki.jmm.commander.services.CurrencyAdapter;
import mk.ukim.finki.jmm.commander.services.CurrencyService;
import mk.ukim.finki.jmm.commander.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class MacSpeechCommanderCurrency extends ListActivity {

	private List<Currency> currencyList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.currencies);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		new CurrencyServiceTask().execute();

	}

	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent parentActivityIntent = new Intent(this,
					MacSpeechCommanderStartActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	};

	private class CurrencyServiceTask extends AsyncTask<URL, Integer, String> {

		private ProgressDialog progressDialog;

		@Override
		protected String doInBackground(URL... params) {

			HashMap<String, ArrayList<Currency>> data = null;
			try {
				data = CurrencyService.getExchangeRateNBRM();

				String[] shortNames = new String[] { "EUR", "GBP", "USD",
						"CAD", "AUD", "DKK", "JPY", "NOK", "SEK", "CHF" };

				HashMap<Integer, Integer> images = new HashMap<Integer, Integer>();
				images.put(0, R.drawable.ic_eur);
				images.put(1, R.drawable.ic_gbp);
				images.put(2, R.drawable.ic_usd);
				images.put(3, R.drawable.ic_cad);
				images.put(4, R.drawable.ic_aud);
				images.put(5, R.drawable.ic_dkk);
				images.put(6, R.drawable.ic_jpy);
				images.put(7, R.drawable.ic_nok);
				images.put(8, R.drawable.ic_sek);
				images.put(9, R.drawable.ic_chf);
				images.put(10, R.drawable.ic_launcher);

				String[] names = new String[10];
				String[] values = new String[10];

				currencyList = new ArrayList<Currency>();

				for (int i = 0; i < 10; i++) {
					names[i] = data.get(shortNames[i]).get(0).getFullNameMac();
					values[i] = data.get(shortNames[i]).get(0).getAverage();
					currencyList.add(new Currency(shortNames[i], names[i],
							values[i], images.get(i)));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MacSpeechCommanderCurrency.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			CurrencyAdapter adapter = new CurrencyAdapter(
					MacSpeechCommanderCurrency.this,
					R.layout.rowlayout_currencies, currencyList);

			adapter.notifyDataSetChanged();
			setListAdapter(adapter);

			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}

	}
}
