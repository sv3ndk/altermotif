package models.altermotif;

public class ResponseNode {

	String name;
	Object value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public ResponseNode(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	public ResponseNode() {
		super();
	}

}
