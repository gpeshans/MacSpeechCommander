package mk.ukim.finki.jmm.commander.services;

public class Product {

	private int image;
	private String name;
	private String value;

	public Product() {
	}

	public Product(String name, String value, int image) {
		this.name = name;
		this.value = value;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setImage(int image) {
		this.image = image;
	}
	
	public int getImage() {
		return image;
	}
}
