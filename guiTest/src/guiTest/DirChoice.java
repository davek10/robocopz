package guiTest;

public class DirChoice {
	DirChoice north,east,south,west;
	int dir, length;
	boolean open,disc;

	public DirChoice(int dir, int length, boolean open, boolean disc ) {
		this.dir = dir;
		this.length = length;
		this.west = null;
		this.east = null;
		this.north = null;
		this.south = null;
		this.open = open;
		this.disc = disc;
	}
	
	public void addDir(DirChoice o, int dir ){
		switch(dir){
		case 0:
			this.north = o;
			o.south = this;
			break;
		case 1:
			this.east = o;
			o.west = this;
			break;
		case 2:
			this.south = o;
			o.north = this;
			break;
		case 3:
			this.west = o;
			o.east = this;
			break;
		}


		
		
	}
	
	

}
