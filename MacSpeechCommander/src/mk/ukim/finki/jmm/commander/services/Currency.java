package mk.ukim.finki.jmm.commander.services;

public class Currency {

	private int flag;
	private String date;
	private String shortName;
	private String average;
	private String country;
	private String fullNameEng;
	private String fullNameMac;
	private String nameEng;

	public Currency() {
	}

	public Currency(String shortName, String fullName, String value, int image) {
		this.shortName = shortName;
		fullNameMac = fullName;
		average = value;
		this.flag = image;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFullNameEng() {
		return fullNameEng;
	}

	public void setFullNameEng(String fullNameEng) {
		this.fullNameEng = fullNameEng;
	}

	public String getFullNameMac() {
		return fullNameMac;
	}

	public void setFullNameMac(String fullNameMac) {
		this.fullNameMac = fullNameMac;
	}

	public String getNameEng() {
		return nameEng;
	}

	public void setNameEng(String nameEng) {
		this.nameEng = nameEng;
	}
			
}
