package edu.udel.cis.vsl.gmc.simplemc;

public class Transition{
	private int id;
	
	public Transition(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
