package mk.ukim.finki.jmm.commander.app;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import mk.ukim.finki.jmm.commander.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class MacSpeechCommanderWeather extends Activity {

	private URL requestUrl;
	private Location location = null;
	private LocationManager locationManager = null;
	private String q;
	private String lat;
	private String lon;
	public static HashMap<String, String> weatherInfo;
	private long time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		weatherInfo = new HashMap<String, String>();
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		if (location != null) {
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			lat = Double.toString(latitude);
			lon = Double.toString(longitude);
			q = "lat=" + lat + ";lon=" + lon;

		} else {
			location = locationManager
					.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
			lat = Double.toString(latitude);
			lon = Double.toString(longitude);
			q = "lat=" + lat + ";lon=" + lon;

		}

		try {
			requestUrl = new URL(
					"http://api.yr.no/weatherapi/locationforecast/1.8/?" + q);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		URL[] urls = { requestUrl };

		new WeatherService().execute(urls);
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

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			makeUseOfNewLocation(location);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	public void makeUseOfNewLocation(Location location) {
		this.location = location;
	}

	private class WeatherService extends AsyncTask<URL, Integer, String> {

		private ProgressDialog progressDialog;

		@Override
		protected String doInBackground(URL... params) {

			String result = "";
			String url = params[0].toString();
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			ResponseHandler<String> handler = new BasicResponseHandler();

			try {
				result = httpclient.execute(request, handler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				httpclient.getConnectionManager().shutdown();
				time = System.currentTimeMillis();
				setWeatherInfo(result);
			}

			String resultsMap = "Temperature: "
					+ weatherInfo.get("temperature") + "\n"
					+ "Wind Direction: " + weatherInfo.get("windDirection")
					+ "\n" + "WindSpeed: " + weatherInfo.get("windSpeed")
					+ "\n" + "Humidity: " + weatherInfo.get("humidity") + "\n"
					+ "Pressure: " + weatherInfo.get("pressure") + "\n"
					+ "Cloudiness: " + weatherInfo.get("cloudiness") + "\n"
					+ "Fog: " + weatherInfo.get("fog") + "\n" + "Time: "
					+ Long.toString(System.currentTimeMillis() - time);

			result = resultsMap;

			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MacSpeechCommanderWeather.this);
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

			TextView tv = (TextView) findViewById(R.id.WeatherResult);
			tv.setText(result);

			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}

		protected void setWeatherInfo(String result) {

			try {
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory
						.newInstance();
				builderFactory.setNamespaceAware(true);
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				String content = result;
				InputSource source = new InputSource(new StringReader(content));
				Document document;
				document = builder.parse(source);

				XPath xpath = XPathFactory.newInstance().newXPath();

				XPathExpression time = xpath
						.compile("//product/time[1]/location");
				Node timeNode = (Node) time.evaluate(document,
						XPathConstants.NODE);

				NodeList weatherNodes = timeNode.getChildNodes();

				NamedNodeMap attributes = null;

				attributes = weatherNodes.item(1).getAttributes();
				String temperatureRes = attributes.getNamedItem("value")
						.getNodeValue();

				attributes = weatherNodes.item(3).getAttributes();
				String windDirectionRes = attributes.getNamedItem("name")
						.getNodeValue();

				attributes = weatherNodes.item(5).getAttributes();
				String windSpeedRes = attributes.getNamedItem("mps")
						.getNodeValue();

				attributes = weatherNodes.item(7).getAttributes();
				String humidityRes = attributes.getNamedItem("value")
						.getNodeValue();

				attributes = weatherNodes.item(9).getAttributes();
				String pressureRes = attributes.getNamedItem("value")
						.getNodeValue();

				attributes = weatherNodes.item(11).getAttributes();
				String cloudinessRes = attributes.getNamedItem("percent")
						.getNodeValue();

				attributes = weatherNodes.item(13).getAttributes();
				String fogRes = attributes.getNamedItem("percent")
						.getNodeValue();

				weatherInfo.put("temperature", temperatureRes);
				weatherInfo.put("windDirection", windDirectionRes);
				weatherInfo.put("windSpeed", windSpeedRes);
				weatherInfo.put("humidity", humidityRes);
				weatherInfo.put("pressure", pressureRes);
				weatherInfo.put("cloudiness", cloudinessRes);
				weatherInfo.put("fog", fogRes);

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
