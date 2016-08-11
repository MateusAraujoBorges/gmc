package edu.udel.cis.vsl.gmc.concurrent.util;

public class Pair<L, R> {
	private L left;
	private R right;
	
	public Pair(L left, R right){
		this.left = left;
		this.right = right;
	}
	
	public L getLeft(){
		return left;
	}
	
	public R getRight(){
		return right;
	}
	
	public void setRight(R right){
		this.right = right;
	}
	
	@Override
	public int hashCode() {
		return left.hashCode();
	}
}
