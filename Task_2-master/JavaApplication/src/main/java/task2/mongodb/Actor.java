package task2.mongodb;

public class Actor {
	
	private final String name;
	private final String role;
	
	public Actor(String name, String role) {
		this.name = name;
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public String getName() {
		return name;
	}
	
}
