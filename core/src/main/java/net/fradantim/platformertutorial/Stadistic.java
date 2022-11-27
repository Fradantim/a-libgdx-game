package net.fradantim.platformertutorial;

public class Stadistic {
	private String name;
	private String value;
	
	public Stadistic() { }
	
	public Stadistic(String name, Object val) {
		this.name = name;
		this.value = String.valueOf(val);
	}
	
	public Stadistic(Object obj, String name, Object val) {
		this(name,val);
		this.name = obj.getClass().getSimpleName()+"."+getName();
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
	
	public String toString() {
		return name+": "+value;
	}
}
