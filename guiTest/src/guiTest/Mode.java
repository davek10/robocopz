package guiTest;

public enum Mode {
	CONTROL{ 
		@Override
		public String toString() {
		return "Control";
		}
	}, 
	AUTO{ 
		@Override
		public String toString() {
		return "Auto";
		}
	};
}
