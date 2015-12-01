package guiTest;


public enum Mode {
	CONTROL((byte) 9, "Control"),
	AUTO((byte) 8, "Auto");
	private byte mode;
	private String modeName;

	private Mode(byte mode, String modeName){
		this.mode = mode;
		this.modeName = modeName;
	}
	byte getInt(){
		return mode;
	}
	String getString(){
		return modeName;
	}

}
