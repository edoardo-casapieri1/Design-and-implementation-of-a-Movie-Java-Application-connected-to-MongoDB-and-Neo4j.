package task2.mongodb;

public class Platform {
	
	private String platform;
	float price;
	
	public Platform(String name, float price) {
		this.platform = name;
		this.price = price;
	}

	public String getPlatform() {
		return platform ;
	}

	public float getPrice() {
		return price;
	}
}
