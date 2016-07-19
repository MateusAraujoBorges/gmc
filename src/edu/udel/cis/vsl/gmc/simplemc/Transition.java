package edu.udel.cis.vsl.gmc.simplemc;

public class Transition{
	private int offset;
	
	public Transition(int offset){
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	@Override
	public int hashCode() {
		return offset;
	}
}
