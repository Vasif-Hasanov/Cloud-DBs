package config.models;

public class AddingOrRemovingResult {
	public String serverName;
	public boolean completed;
	public AddingOrRemovingResult(String serverName, boolean completed) {
		this.completed = completed;
		this.serverName = serverName;
	}

}
