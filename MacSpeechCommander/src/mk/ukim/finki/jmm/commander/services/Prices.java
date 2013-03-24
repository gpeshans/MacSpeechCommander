package mk.ukim.finki.jmm.commander.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Prices {

	/**
	 * @param args
	 */
	public static HashMap<String, String> getPrices() {

		String url = "http://www.stat.gov.mk/PrethodniSoopstenijaOblast.aspx?id=45&rbrObl=15";
		HashMap<String, String> products = null;
		try {
			Document doc = Jsoup.connect(url).get();
			String link = doc.select(
					"#ctl00_ContentPlaceHolder1_DataList4 a[href]")
					.attr("href");

			String redirect = "http://www.stat.gov.mk/" + link;
			doc = Jsoup.connect(redirect).get();
			link = doc.select(
					"#ctl00_ContentPlaceHolder1_FormView5_HyperLink10").attr(
					"href");

			String xmlUrl = "http://www.stat.gov.mk/" + link;

			/*
			 * FileInputStream file = new FileInputStream(new File(
			 * "C:\\Users\\DANIEL\\Downloads\\5.1.13.05.xls"));
			 */

			URL url1 = new URL(xmlUrl);
			URLConnection uc = url1.openConnection();
			// Get the workbook instance for XLS file
			// HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFWorkbook workbook = new HSSFWorkbook(uc.getInputStream());

			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows from first sheet
			int rowNo = 0;
			products = new HashMap<String, String>();

			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				rowNo++;
				int cellNo = 0;

				String key = "";
				Float value = (float) 0.0;

				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();
					cellNo++;

					if (rowNo == 5 && cellNo == 3) {
						ByteBuffer b = Charset.forName("UTF-8").encode(
								cell.getStringCellValue());
						String date = Charset.forName("UTF-8").decode(b)
								.toString();
						products.put("датум", date);

					}
					if (rowNo > 6)
						if (cellNo == 1) {
							ByteBuffer b = Charset.forName("UTF-8").encode(
									cell.getStringCellValue());
							String s = Charset.forName("UTF-8").decode(b)
									.toString();
							key = s;
						} else if (cellNo == 3) {
							Float s = (float) cell.getNumericCellValue();
							value = s;
						}
				}
				if (rowNo > 6) {
					products.put(key, value.toString());
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return products;
	}

}
