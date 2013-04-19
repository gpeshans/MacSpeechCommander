package mk.ukim.finki.jmm.commander.app;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import mk.ukim.finki.jmm.commander.R;
import mk.ukim.finki.jmm.commander.services.SunriseSunset;
import android.R.raw;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Element;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class MacSpeechCommanderWeather extends Activity {

	private URL requestUrl;
	private Location location = null;
	private LocationManager locationManager = null;
	private String q;
	private String lat;
	private String lon;
	public static HashMap<String, String> weatherInfo;
	public static HashMap<String, Integer> weatherIconSet;
	public static HashMap<String, Integer> weatherForecastIconSet;
	public static HashMap<String, String> weatherInfoForecast;
	public static HashMap<String, String> weatherForecastMac;
	private String place;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		weatherInfo = new HashMap<String, String>();
		weatherIconSet = new HashMap<String, Integer>();
		weatherForecastIconSet = new HashMap<String, Integer>();
		weatherInfoForecast = new HashMap<String, String>();
		weatherForecastMac = new HashMap<String, String>();

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1800000, 0, locationListener);

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

		fillWeatherIconSet();
		fillWeatherForecastIconSet();
		fillWeatherForecastMac();

		if (savedInstanceState == null) {

			try {
				requestUrl = new URL(
						"http://api.yr.no/weatherapi/locationforecast/1.8/?"
								+ q);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			URL[] urls = { requestUrl };

			new WeatherService().execute(urls);
		} else {

			String weatherInfoArray[] = savedInstanceState
					.getStringArray("weather_info");

			weatherInfo.put("temperature", weatherInfoArray[0]);
			weatherInfo.put("windDirection", weatherInfoArray[1]);
			weatherInfo.put("windSpeed", weatherInfoArray[2]);
			weatherInfo.put("humidity", weatherInfoArray[3]);
			weatherInfo.put("pressure", weatherInfoArray[4]);
			weatherInfo.put("cloudiness", weatherInfoArray[5]);
			weatherInfo.put("fog", weatherInfoArray[6]);
			weatherInfo.put("forecast", weatherInfoArray[7]);
			weatherInfo.put("place", weatherInfoArray[8]);
			weatherInfo.put("windDirectionAngle", weatherInfoArray[9]);

			place = weatherInfo.get("place");

			String weatherInfoForecastArray[] = savedInstanceState
					.getStringArray("weather_info_forecast");

			weatherInfoForecast.put("temperature", weatherInfoForecastArray[0]);
			weatherInfoForecast.put("windDirection",
					weatherInfoForecastArray[1]);
			weatherInfoForecast.put("windSpeed", weatherInfoForecastArray[2]);
			weatherInfoForecast.put("humidity", weatherInfoForecastArray[3]);
			weatherInfoForecast.put("pressure", weatherInfoForecastArray[4]);
			weatherInfoForecast.put("cloudiness", weatherInfoForecastArray[5]);
			weatherInfoForecast.put("fog", weatherInfoForecastArray[6]);
			weatherInfoForecast.put("forecast", weatherInfoForecastArray[7]);

			fillViews();

		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (weatherInfo.isEmpty())
			return;
		
		String weatherInfoArray[] = new String[10];
		weatherInfoArray[0] = weatherInfo.get("temperature").toString();
		weatherInfoArray[1] = weatherInfo.get("windDirection").toString();
		weatherInfoArray[2] = weatherInfo.get("windSpeed").toString();
		weatherInfoArray[3] = weatherInfo.get("humidity").toString();
		weatherInfoArray[4] = weatherInfo.get("pressure").toString();
		weatherInfoArray[5] = weatherInfo.get("cloudiness").toString();
		weatherInfoArray[6] = weatherInfo.get("fog").toString();
		weatherInfoArray[7] = weatherInfo.get("forecast").toString();
		weatherInfoArray[8] = weatherInfo.get("place").toString();
		weatherInfoArray[9] = weatherInfo.get("windDirectionAngle").toString();

		outState.putStringArray("weather_info", weatherInfoArray);

		String weatherInfoForecastArray[] = new String[8];
		weatherInfoForecastArray[0] = weatherInfoForecast.get("temperature")
				.toString();
		weatherInfoForecastArray[1] = weatherInfoForecast.get("windDirection")
				.toString();
		weatherInfoForecastArray[2] = weatherInfoForecast.get("windSpeed")
				.toString();
		weatherInfoForecastArray[3] = weatherInfoForecast.get("humidity")
				.toString();
		weatherInfoForecastArray[4] = weatherInfoForecast.get("pressure")
				.toString();
		weatherInfoForecastArray[5] = weatherInfoForecast.get("cloudiness")
				.toString();
		weatherInfoForecastArray[6] = weatherInfoForecast.get("fog").toString();
		weatherInfoForecastArray[7] = weatherInfoForecast.get("forecast")
				.toString();

		outState.putStringArray("weather_info_forecast",
				weatherInfoForecastArray);
	}

	private void fillWeatherForecastMac() {

		if (isDay(location)) {
			weatherForecastMac.put("SUN", "Сончево");
			weatherForecastMac.put("LIGHTRAINSUN", "Сончево со слаб дожд");
			weatherForecastMac.put("SLEETSUN", "Сончево со леден дожд");
			weatherForecastMac.put("SNOWSUN", "Сончево со снег");
			weatherForecastMac.put("SLEETSUNTHUNDER",
					"Сончево со леден дожд и грмотевици");
			weatherForecastMac.put("SNOWSUNTHUNDER",
					"Сончево со снег и грмотевици");
		} else {
			weatherForecastMac.put("SUN", "Ведро");
			weatherForecastMac.put("LIGHTRAINSUN", "Ведро со слаб дожд");
			weatherForecastMac.put("SLEETSUN", "Ведро со леден дожд");
			weatherForecastMac.put("SNOWSUN", "Ведро со снег");
			weatherForecastMac.put("SLEETSUNTHUNDER",
					"Ведро со леден дожд и грмотевици");
			weatherForecastMac.put("SNOWSUNTHUNDER",
					"Ведро со снег и грмотевици");
		}

		weatherForecastMac.put("LIGHTCLOUD", "Умерено облачно");
		weatherForecastMac.put("PARTLYCLOUD", "Умерено облачно");
		weatherForecastMac.put("CLOUD", "Облачно");

		weatherForecastMac.put("LIGHTRAINTHUNDERSUN",
				"Сончево со слаб дожд и грмотевици");

		weatherForecastMac.put("LIGHTRAIN", "Слаб дожд");
		weatherForecastMac.put("RAIN", "Дожд");
		weatherForecastMac.put("RAINTHUNDER", "Дожд со грмотевици");
		weatherForecastMac.put("SLEET", "Леден дожд");
		weatherForecastMac.put("SNOW", "Снег");
		weatherForecastMac.put("SNOWTHUNDER", "Снег со грмотевици");
		weatherForecastMac.put("FOG", "Магла");

		weatherForecastMac.put("LIGHTRAINTHUNDER", "Слаб дожд со грмотевици");
		weatherForecastMac.put("SLEETTHUNDER", "Леден дожд со грмотевици");
	}

	private void fillWeatherIconSet() {

		if (isDay(location)) {
			weatherIconSet.put("SUN", R.drawable.ic_sunny);
			weatherIconSet.put("LIGHTCLOUD", R.drawable.ic_sunny_cloudy);
			weatherIconSet.put("PARTLYCLOUD", R.drawable.ic_sunny_cloudy);
			weatherIconSet.put("SLEETSUN", R.drawable.ic_sunny);
			weatherIconSet.put("SLEET", R.drawable.ic_sunny_cloudy);
		} else {
			weatherIconSet.put("SUN", R.drawable.ic_moon);
			weatherIconSet.put("LIGHTCLOUD", R.drawable.ic_moon_cloudy);
			weatherIconSet.put("PARTLYCLOUD", R.drawable.ic_moon_cloudy);
			weatherIconSet.put("SLEETSUN", R.drawable.ic_moon);
			weatherIconSet.put("SLEET", R.drawable.ic_moon_cloudy);
		}

		weatherIconSet.put("CLOUD", R.drawable.ic_cloudy);
		weatherIconSet.put("LIGHTRAINSUN", R.drawable.ic_rainy);
		weatherIconSet.put("LIGHTRAINTHUNDERSUN", R.drawable.ic_rainy);

		weatherIconSet.put("SNOWSUN", R.drawable.ic_snowy);
		weatherIconSet.put("LIGHTRAIN", R.drawable.ic_rainy);
		weatherIconSet.put("RAIN", R.drawable.ic_rainy);
		weatherIconSet.put("RAINTHUNDER", R.drawable.ic_thunder);

		weatherIconSet.put("SNOW", R.drawable.ic_snowy);
		weatherIconSet.put("SNOWTHUNDER", R.drawable.ic_snowy);
		weatherIconSet.put("FOG", R.drawable.ic_fog);
		weatherIconSet.put("SLEETSUNTHUNDER", R.drawable.ic_thunder);
		weatherIconSet.put("SNOWSUNTHUNDER", R.drawable.ic_thunder);
		weatherIconSet.put("LIGHTRAINTHUNDER", R.drawable.ic_rainy);
		weatherIconSet.put("SLEETTHUNDER", R.drawable.ic_thunder);
	}

	private void fillWeatherForecastIconSet() {

		if (isDayAfter3Hours(location)) {
			weatherForecastIconSet.put("SUN", R.drawable.ic_sunny);
			weatherForecastIconSet
					.put("LIGHTCLOUD", R.drawable.ic_sunny_cloudy);
			weatherForecastIconSet.put("PARTLYCLOUD",
					R.drawable.ic_sunny_cloudy);
			weatherForecastIconSet.put("SLEETSUN", R.drawable.ic_sunny);
			weatherForecastIconSet.put("SLEET", R.drawable.ic_sunny_cloudy);
		} else {
			weatherForecastIconSet.put("SUN", R.drawable.ic_moon);
			weatherForecastIconSet.put("LIGHTCLOUD", R.drawable.ic_moon_cloudy);
			weatherForecastIconSet
					.put("PARTLYCLOUD", R.drawable.ic_moon_cloudy);
			weatherForecastIconSet.put("SLEETSUN", R.drawable.ic_moon);
			weatherForecastIconSet.put("SLEET", R.drawable.ic_moon_cloudy);
		}

		weatherForecastIconSet.put("CLOUD", R.drawable.ic_cloudy);
		weatherForecastIconSet.put("LIGHTRAINSUN", R.drawable.ic_rainy);
		weatherForecastIconSet.put("LIGHTRAINTHUNDERSUN", R.drawable.ic_rainy);

		weatherForecastIconSet.put("SNOWSUN", R.drawable.ic_snowy);
		weatherForecastIconSet.put("LIGHTRAIN", R.drawable.ic_rainy);
		weatherForecastIconSet.put("RAIN", R.drawable.ic_rainy);
		weatherForecastIconSet.put("RAINTHUNDER", R.drawable.ic_thunder);

		weatherForecastIconSet.put("SNOW", R.drawable.ic_snowy);
		weatherForecastIconSet.put("SNOWTHUNDER", R.drawable.ic_snowy);
		weatherForecastIconSet.put("FOG", R.drawable.ic_fog);
		weatherForecastIconSet.put("SLEETSUNTHUNDER", R.drawable.ic_thunder);
		weatherForecastIconSet.put("SNOWSUNTHUNDER", R.drawable.ic_thunder);
		weatherForecastIconSet.put("LIGHTRAINTHUNDER", R.drawable.ic_rainy);
		weatherForecastIconSet.put("SLEETTHUNDER", R.drawable.ic_thunder);
	}

	protected Boolean isDayAfter3Hours(Location location) {
		TimeZone tz = TimeZone.getDefault();
		int rawOffset = tz.getRawOffset();
		double offset = rawOffset / 3600000.0;

		Log.i("offset", Double.toString(offset));

		Date date = new Date(System.currentTimeMillis() + 3 * 3600 * 1000);
		SunriseSunset ss = new SunriseSunset(location.getLatitude(),
				location.getLongitude(), date, offset);
		if (ss.isDaytime())
			return true;

		return false;
	}

	protected Boolean isDay(Location location) {

		TimeZone tz = TimeZone.getDefault();
		int rawOffset = tz.getRawOffset();
		double offset = rawOffset / 3600000.0;

		Log.i("offset", Double.toString(offset));

		Date date = new Date(System.currentTimeMillis());
		SunriseSunset ss = new SunriseSunset(location.getLatitude(),
				location.getLongitude(), date, offset);
		if (ss.isDaytime())
			return true;

		return false;
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

	public void fillViews() {
		String info = String.format(
				"\n\n Влажност: %s \n\n Притисок: %s hPa \n\n Облачност: %s",
				weatherInfo.get("humidity").toString() + "%",
				weatherInfo.get("pressure").toString(),
				weatherInfo.get("cloudiness").toString() + "%");

		TextView tvWeatherResults = (TextView) findViewById(R.id.WeatherResults);
		tvWeatherResults.setText(info);

		Spanned temperature = Html
				.fromHtml("<b>"
						+ weatherInfo.get("temperature").toString()
						+ "</b>"
						+ "<sup><small><sup><small> o</small></sup><b>C</b></small></sup>");

		TextView tvTemperature = (TextView) findViewById(R.id.WeatherTemperature);
		tvTemperature.setText(temperature);

		String forecast = weatherForecastMac.get(
				weatherInfo.get("forecast").toString()).toString();
		TextView tvForecast = (TextView) findViewById(R.id.WeatherForecast);
		tvForecast.setText("\n" + forecast);

		String[] pomPlace = place.split(", ");
		String municipality = pomPlace[0];
		String country = pomPlace[1];

		TextView tvLocation = (TextView) findViewById(R.id.LocationInfo);
		tvLocation.setText(municipality + ",\n" + country);

		String fog = weatherInfo.get("fog").toString();
		fog = fog.split("\\.")[0] + "%";
		TextView tvFog = (TextView) findViewById(R.id.FogInfo);
		tvFog.setText(fog);

		/*
		 * String forecast3 = weatherInfoForecast.get("forecast").toString();
		 * TextView tvForecast3 = (TextView) findViewById(R.id.Forecast3Info);
		 * tvForecast3.setText(weatherForecastMac.get(forecast3));
		 */

		TextView tvWindInfo = (TextView) findViewById(R.id.WindInfo);
		tvWindInfo.setText("Ветер: " + weatherInfo.get("windSpeed").toString()
				+ "mps " + weatherInfo.get("windDirection"));

		ImageView ivCompass = (ImageView) findViewById(R.id.CompassIcon);
		ivCompass.setImageResource(R.drawable.ic_compass_base);

		ImageView ivCompassArrow = (ImageView) findViewById(R.id.CompassArrowIcon);
		ivCompassArrow.setImageResource(R.drawable.ic_compass_arrow);

		float angle = Float.parseFloat(weatherInfo.get("windDirectionAngle")
				.toString());

		Matrix matrix = new Matrix();

		BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
				R.drawable.ic_compass_arrow);
		int height = bd.getBitmap().getHeight();
		int width = bd.getBitmap().getWidth();

		ivCompassArrow.setScaleType(ScaleType.MATRIX); // required
		matrix.postRotate(angle, width / 2, height / 2);
		ivCompassArrow.setImageMatrix(matrix);

		Spanned forecastTemperature = Html
				.fromHtml("<b>"
						+ weatherInfoForecast.get("temperature").toString()
						+ "</b>"
						+ "<sup><small><sup><small> o</small></sup><b>C</b></small></sup>");

		TextView tvForecastTemperature = (TextView) findViewById(R.id.ForecastTemperature);
		tvForecastTemperature.setText(forecastTemperature);

		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY) + 3);
		TextView tvForecastTitle = (TextView) findViewById(R.id.ForecastTitle);
		Date timeDate = time.getTime();

		String hours = Integer.toString(timeDate.getHours());
		if (timeDate.getHours() < 10)
			hours = "0" + hours;

		String minutes = Integer.toString(timeDate.getMinutes());
		if (timeDate.getMinutes() < 10)
			minutes = "0" + minutes;

		String forecastTime = "Прогноза до " + hours + ":" + minutes + " h";
		tvForecastTitle.setText(forecastTime);

		View weatherBackgroundView = (View) findViewById(R.id.ViewWeatherInfo);
		weatherBackgroundView.setBackgroundColor(getResources().getColor(
				R.color.sky));

		ImageView ivWeatherIcon = (ImageView) findViewById(R.id.WeatherIcon);
		ivWeatherIcon.setImageResource(weatherIconSet.get(weatherInfo
				.get("forecast")));

		ImageView ivForecastIcon = (ImageView) findViewById(R.id.ForecastIcon);
		ivForecastIcon.setImageResource(weatherForecastIconSet
				.get(weatherInfoForecast.get("forecast")));

		ImageView ivPlaceIcon = (ImageView) findViewById(R.id.PlaceIcon);
		ivPlaceIcon.setImageResource(R.drawable.ic_pin);

		ImageView ivFogIcon = (ImageView) findViewById(R.id.FogIcon);
		ivFogIcon.setImageResource(R.drawable.ic_fog);
	}

	private class WeatherService extends AsyncTask<URL, Integer, String> {

		private ProgressDialog progressDialog;
		private String placeUrl = "http://maps.googleapis.com/maps/api/geocode/json?";

		@Override
		protected String doInBackground(URL... params) {

			String result = "";
			String placeResult = "";

			String url = params[0].toString();
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			ResponseHandler<String> handler = new BasicResponseHandler();

			HttpClient httpclientPlace = new DefaultHttpClient();
			placeUrl += "latlng=" + lat.toString() + "," + lon.toString()
					+ "&sensor=false";
			HttpGet requestPlace = new HttpGet(placeUrl);
			ResponseHandler<String> handlerPlace = new BasicResponseHandler();

			try {

				result = httpclient.execute(request, handler);

				placeResult = httpclientPlace.execute(requestPlace,
						handlerPlace);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			finally {
				httpclient.getConnectionManager().shutdown();
				setWeatherInfo(result, placeResult);
			}

			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MacSpeechCommanderWeather.this);
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

			fillViews();
			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}

		protected void setWeatherInfo(String result, String placeResult) {

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

				String windDirectionAngleRes = attributes.getNamedItem("deg")
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
				weatherInfo.put("windDirectionAngle", windDirectionAngleRes);
				weatherInfo.put("windSpeed", windSpeedRes);
				weatherInfo.put("humidity", humidityRes);
				weatherInfo.put("pressure", pressureRes);
				weatherInfo.put("cloudiness", cloudinessRes);
				weatherInfo.put("fog", fogRes);

				XPathExpression timeF = xpath
						.compile("//product/time[2]/location/symbol");
				Node timeNodeF = (Node) timeF.evaluate(document,
						XPathConstants.NODE);

				NamedNodeMap attributesF = null;

				attributesF = timeNodeF.getAttributes();
				String forecastF = attributesF.item(0).getNodeValue();

				weatherInfo.put("forecast", forecastF);

				XPathExpression timeForecast = xpath
						.compile("//product/time[4]/location");
				Node timeNodeForecast = (Node) timeForecast.evaluate(document,
						XPathConstants.NODE);

				NodeList weatherNodesForecast = timeNodeForecast
						.getChildNodes();

				NamedNodeMap attributesForecast = null;

				attributesForecast = weatherNodesForecast.item(1)
						.getAttributes();
				String temperatureResForecast = attributesForecast
						.getNamedItem("value").getNodeValue();

				attributesForecast = weatherNodesForecast.item(3)
						.getAttributes();
				String windDirectionResForecast = attributesForecast
						.getNamedItem("name").getNodeValue();

				attributesForecast = weatherNodesForecast.item(5)
						.getAttributes();
				String windSpeedResForecast = attributesForecast.getNamedItem(
						"mps").getNodeValue();

				attributesForecast = weatherNodesForecast.item(7)
						.getAttributes();
				String humidityResForecast = attributesForecast.getNamedItem(
						"value").getNodeValue();

				attributesForecast = weatherNodesForecast.item(9)
						.getAttributes();
				String pressureResForecast = attributesForecast.getNamedItem(
						"value").getNodeValue();

				attributesForecast = weatherNodesForecast.item(11)
						.getAttributes();
				String cloudinessResForecast = attributesForecast.getNamedItem(
						"percent").getNodeValue();

				attributesForecast = weatherNodesForecast.item(13)
						.getAttributes();
				String fogResForecast = attributesForecast.getNamedItem(
						"percent").getNodeValue();

				weatherInfoForecast.put("temperature", temperatureResForecast);
				weatherInfoForecast.put("windDirection",
						windDirectionResForecast);
				weatherInfoForecast.put("windSpeed", windSpeedResForecast);
				weatherInfoForecast.put("humidity", humidityResForecast);
				weatherInfoForecast.put("pressure", pressureResForecast);
				weatherInfoForecast.put("cloudiness", cloudinessResForecast);
				weatherInfoForecast.put("fog", fogResForecast);

				XPathExpression timeFF = xpath
						.compile("//product/time[5]/location/symbol");
				Node timeNodeFF = (Node) timeFF.evaluate(document,
						XPathConstants.NODE);

				NamedNodeMap attributesFF = null;

				attributesFF = timeNodeFF.getAttributes();
				String forecastFF = attributesFF.item(0).getNodeValue();

				weatherInfoForecast.put("forecast", forecastFF);

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

			try {
				JSONObject placeJson = new JSONObject(placeResult);

				JSONArray placeArray = placeJson.getJSONArray("results");

				place = placeArray.getJSONObject(1).getString(
						"formatted_address");

				place = place.replace("(FYROM)", "");
				weatherInfo.put("place", place);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
