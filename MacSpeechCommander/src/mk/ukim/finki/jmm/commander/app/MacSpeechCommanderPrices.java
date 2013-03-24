package mk.ukim.finki.jmm.commander.app;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mk.ukim.finki.jmm.commander.R;
import mk.ukim.finki.jmm.commander.services.Currency;
import mk.ukim.finki.jmm.commander.services.CurrencyAdapter;
import mk.ukim.finki.jmm.commander.services.CurrencyService;
import mk.ukim.finki.jmm.commander.services.Prices;
import mk.ukim.finki.jmm.commander.services.PricesAdapter;
import mk.ukim.finki.jmm.commander.services.Product;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class MacSpeechCommanderPrices extends ListActivity {

	private List<Product> pricesList;
	//private HashMap<String, String> data;
	private String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prices);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		new PricesServiceTask().execute();
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

	private class PricesServiceTask extends AsyncTask<URL, Integer, String> {

		private ProgressDialog progressDialog;

		@Override
		protected String doInBackground(URL... params) {

			try {

				/*
				 * String[] shortNames = new String[] { "EUR", "GBP", "USD",
				 * "CAD", "AUD", "DKK", "JPY", "NOK", "SEK", "CHF" };
				 * 
				 * HashMap<Integer, Integer> images = new HashMap<Integer,
				 * Integer>(); images.put(0, R.drawable.ic_eur); images.put(1,
				 * R.drawable.ic_gbp); images.put(2, R.drawable.ic_usd);
				 * images.put(3, R.drawable.ic_cad); images.put(4,
				 * R.drawable.ic_aud); images.put(5, R.drawable.ic_dkk);
				 * images.put(6, R.drawable.ic_jpy); images.put(7,
				 * R.drawable.ic_nok); images.put(8, R.drawable.ic_sek);
				 * images.put(9, R.drawable.ic_chf); images.put(10,
				 * R.drawable.ic_launcher);
				 * 
				 * String[] names = new String[10]; String[] values = new
				 * String[10];
				 */

				HashMap<String, String> data = new HashMap<String, String>();
				date = "";

				pricesList = new ArrayList<Product>();
				data = Prices.getPrices();

				date = data.get("датум");
				data.remove("датум");
				
				Iterator iter = data.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry pairs = (Map.Entry) iter.next();
					pricesList.add(new Product((String) pairs.getKey(),
							(String) pairs.getValue()));
				}								

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MacSpeechCommanderPrices.this);
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

			TextView tvDate = (TextView) MacSpeechCommanderPrices.this.findViewById(R.id.Date);
			tvDate.setText(date);

			PricesAdapter adapter = new PricesAdapter(
					MacSpeechCommanderPrices.this, R.layout.rowlayout_prices,
					pricesList);
			
			List<Product> p = pricesList;

			adapter.notifyDataSetChanged();
			setListAdapter(adapter);

			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}

	}
}
