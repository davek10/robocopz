package guiTest;

public class Pair {

	private final String left;
	private final double right;

	public Pair(String left, double right) {
		this.left = left;
		this.right = right;
	}

	public String getLeft() { return left; }
	public double getRight() { return right; }
	
	public String toString(){
		return left + "\t" + right ;
		
	}
}