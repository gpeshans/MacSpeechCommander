package mk.ukim.finki.jmm.commander.services;

import java.io.StringReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CurrencyService {
	
	public static HashMap<String, ArrayList<Currency>> getExchangeRateNBRM()
			throws Exception {

		String wsURL = "http://www.nbrm.mk/klservice/kurs.asmx";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(calendar.getTime());
		String start = sdf.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, -29);
		Date days30 = calendar.getTime();
		String end = sdf.format(days30);

		SoapObject request = new SoapObject("http://www.nbrm.mk/klservice/",
				"GetExchangeRate");
		request.addProperty("StartDate", start);
		request.addProperty("EndDate", end);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		HttpTransportSE transport = new HttpTransportSE(wsURL);
		transport.debug = true;
		transport
				.call("http://www.nbrm.mk/klservice/GetExchangeRate", envelope);

		Object response = envelope.getResponse();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(
				response.toString())));

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression average = xpath.compile("//KursZbir/Sreden");
		XPathExpression date = xpath.compile("//KursZbir/Datum");
		XPathExpression shortName = xpath.compile("//KursZbir/Oznaka");
		XPathExpression country = xpath.compile("//KursZbir/Drzava");
		XPathExpression countryEng = xpath.compile("//KursZbir/DrzavaAng");
		XPathExpression fullNameMac = xpath.compile("//KursZbir/NazivMak");
		XPathExpression fullNameEng = xpath.compile("//KursZbir/NazivAng");

		NodeList averageCurrency = (NodeList) average.evaluate(doc,
				XPathConstants.NODESET);
		NodeList dateCurrency = (NodeList) date.evaluate(doc,
				XPathConstants.NODESET);
		NodeList shortNameCurrency = (NodeList) shortName.evaluate(doc,
				XPathConstants.NODESET);
		NodeList countryCurrency = (NodeList) country.evaluate(doc,
				XPathConstants.NODESET);
		NodeList countryEngCurrency = (NodeList) countryEng.evaluate(doc,
				XPathConstants.NODESET);
		NodeList fullNameMacCurrency = (NodeList) fullNameMac.evaluate(doc,
				XPathConstants.NODESET);
		NodeList fullNameEngCurrency = (NodeList) fullNameEng.evaluate(doc,
				XPathConstants.NODESET);

		HashMap<String, ArrayList<Currency>> data = new HashMap<String, ArrayList<Currency>>();

		for (int i = 0; i < averageCurrency.getLength(); i++) {

			Currency val = new Currency();
			val.setDate(dateCurrency.item(i).getTextContent());
			val.setCountry(countryCurrency.item(i).getTextContent());
			val.setNameEng(countryEngCurrency.item(i).getTextContent());
			val.setFullNameEng(fullNameEngCurrency.item(i).getTextContent());
			val.setFullNameMac(fullNameMacCurrency.item(i).getTextContent());
			val.setShortName(shortNameCurrency.item(i).getTextContent());
			val.setAverage(averageCurrency.item(i).getTextContent());

			String key = val.getShortName();

			if (!data.containsKey(key))
				data.put(key, new ArrayList<Currency>());

			data.get(key).add(val);
		}

		return data;
	}

}