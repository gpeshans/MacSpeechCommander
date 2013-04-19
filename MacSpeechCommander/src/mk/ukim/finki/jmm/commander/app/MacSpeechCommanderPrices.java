package mk.ukim.finki.jmm.commander.app;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mk.ukim.finki.jmm.commander.R;
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
	private String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prices);

		if (savedInstanceState == null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);

			new PricesServiceTask().execute();
		} else {

			pricesList = new ArrayList<Product>();
			date = savedInstanceState.getString("date");

			ArrayList<String> name = savedInstanceState
					.getStringArrayList("name");
			ArrayList<String> values = savedInstanceState
					.getStringArrayList("values");
			ArrayList<Integer> images = savedInstanceState
					.getIntegerArrayList("images");

			for (int i = 0; i < name.size(); i++) {
				pricesList.add(new Product(name.get(i), values.get(i), images
						.get(i)));
			}

			getActionBar().setDisplayHomeAsUpEnabled(true);
			PricesAdapter adapter = new PricesAdapter(
					MacSpeechCommanderPrices.this, R.layout.rowlayout_prices,
					pricesList);

			adapter.notifyDataSetChanged();
			setListAdapter(adapter);

			TextView tvDate = (TextView) MacSpeechCommanderPrices.this
					.findViewById(R.id.Date);
			tvDate.setText(date);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (pricesList.isEmpty())
			return;
		
		outState.putBoolean("flag", true);

		ArrayList<String> name = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<Integer> images = new ArrayList<Integer>();
		for (int i = 0; i < pricesList.size(); i++) {
			name.add(pricesList.get(i).getName());
			values.add(pricesList.get(i).getValue());
			images.add(pricesList.get(i).getImage());
		}

		outState.putStringArrayList("name", name);
		outState.putStringArrayList("values", values);
		outState.putIntegerArrayList("images", images);
		outState.putString("date", date);

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

				HashMap<String, String> data = new HashMap<String, String>();
				date = "";

				pricesList = new ArrayList<Product>();
				data = Prices.getPrices();

				HashMap<String, Integer> images = Prices.loadImages();

				date = data.get("датум");
				data.remove("датум");

				Iterator iter = data.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry pairs = (Map.Entry) iter.next();
					pricesList.add(new Product((String) pairs.getKey(),
							(String) pairs.getValue(), images.get(pairs
									.getKey().toString())));
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
			progressDialog.setMessage("Процесира...");
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

			TextView tvDate = (TextView) MacSpeechCommanderPrices.this
					.findViewById(R.id.Date);
			tvDate.setText(date);

			PricesAdapter adapter = new PricesAdapter(
					MacSpeechCommanderPrices.this, R.layout.rowlayout_prices,
					pricesList);

			adapter.notifyDataSetChanged();
			setListAdapter(adapter);

			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}

	}
}
