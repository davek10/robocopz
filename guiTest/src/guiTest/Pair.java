package guiTest;

public class Pair {

	private final String left;
	private final int right;

	public Pair(String left, int right) {
		this.left = left;
		this.right = right;
	}

	public String getLeft() { return left; }
	public int getRight() { return right; }
	
	public String toString(){
		return left + "\t" + right ;
		
	}
}