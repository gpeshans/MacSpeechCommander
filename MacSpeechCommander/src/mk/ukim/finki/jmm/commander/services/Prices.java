package mk.ukim.finki.jmm.commander.services;

import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import mk.ukim.finki.jmm.commander.R;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Prices {

	/**
	 * @param args
	 */

	public static HashMap<String, String> getPrices() {

		HashMap<String, Integer> productsMap = loadImages();

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

							if (!productsMap.containsKey(s))
								continue;

							key = s;
						} else if (cellNo == 3) {
							Float s = (float) cell.getNumericCellValue();
							value = s;
						}
				}
				if (rowNo > 6) {

					String valueFormatted = String.format("%.2f", value);
					if (!key.equals(""))
						products.put(key, valueFormatted);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return products;
	}

	public static HashMap<String, Integer> loadImages() {

		HashMap<String, Integer> images = new HashMap<String, Integer>();

		String[] products = { "Ориз", "Црвени домати", "Зелени пиперки",
				"Компири", "Грав", "Кромид", "Лук", "Зелка", "Спанаќ",
				"Краставици", "Морков", "Цвекло", "Кикиритки-зрно", "Круши",
				"Јаболка", "Ореви-лупени", "Киви", "Лимони", "Портокали",
				"Мандарини", "Банани", "Маслинки" };

		int[] icons = { R.drawable.ic_rice, R.drawable.ic_tomato,
				R.drawable.ic_green_pepper, R.drawable.ic_potato,
				R.drawable.ic_beans, R.drawable.ic_onion, R.drawable.ic_garlic,
				R.drawable.ic_cabbage, R.drawable.ic_spinach,
				R.drawable.ic_cucumber, R.drawable.ic_carrot,
				R.drawable.ic_beets, R.drawable.ic_peanuts, R.drawable.ic_pear,
				R.drawable.ic_apple, R.drawable.ic_walnuts, R.drawable.ic_kiwi,
				R.drawable.ic_lemon, R.drawable.ic_orange,
				R.drawable.ic_mandarin, R.drawable.ic_banana,
				R.drawable.ic_olives };

		for (int i = 0; i < icons.length; i++) {
			images.put(products[i], icons[i]);
		}

		return images;
	}

}
