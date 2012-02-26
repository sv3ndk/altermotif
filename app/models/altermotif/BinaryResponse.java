package models.altermotif;

public class BinaryResponse {
	private boolean sucess;

	public boolean isSucess() {
		return sucess;
	}

	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}

	public BinaryResponse(boolean sucess) {
		super();
		this.sucess = sucess;
	}

	public BinaryResponse() {
		super();
	}

}
