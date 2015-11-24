package guiTest;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GUI extends JFrame {
	// Height of title bar at the top of window
	int titleHeight = 43;
	// size of border
	int border = 5;
	// width of window
	int width = 640;
	// height of windows
	int height = 480;
	// height of header
	int headHeight = 50;
	//xWindow = total number of snap-points in x
	int xWindow = 3;
	//yWindow = total number of snap-points in y
	int yWindow = 5;
	// Global variable for testing update of GUI
	int test = 0;
	// ArrayList for path
	public static ArrayList<DirChoice> path = new ArrayList<DirChoice>();
	// Bluetooth data from the communications unit
	public static byte[] btData = new byte[25];
	// ArrayList used for the keyboard inputs
	public static ArrayList<Pair> kInput = new ArrayList<Pair>();
	// ArrayList for decisions made by the robot. TODO add special datatype for this.
	public static ArrayList<String> decisionsList = new ArrayList<String>();
	 // Mode for the robot
	static Mode robotMode = Mode.AUTO;
	// Text pane for the head
	static JTextPane head = new JTextPane();
	//Text pane for the servos data
	static JTextPane servos = new JTextPane();
	//Text pane for the sensors data
	static JTextPane sensors = new JTextPane();
	//Text pane for the keyboard inputs
	static JTextPane inputs = new JTextPane();
	// Text pane for robot decisions
	static JTextPane decisions = new JTextPane();
	// Panel for minimap
	JPanel minimap = new JPanel();
	// String for the Servos pane
	static String servoText = "Servo \t Value \n";
	// String for the inputs pane
	static String inputText = "Button \t Duration \n";
	// String for the sensors pane
	static String sensorText = "Sensor \t Value \n";
	// String for the modeButton
	static String modeButtonText = robotMode.toString();
	// String for the head pane
	static String headText = "\t\t ROBOKAPPA";
	// String for the decisions pane
	static String decisionsText = "Decisions \n";
	// String for the log file button
	static String fileActionText = "Print log";
	// Timer for counting time button is pressed
	long startTime;
	// Duration of button pressed
	double duration;
	// Mapping integers to sensor positions
	private static final Map<Integer, String> sensorMap;
	static
	{
		sensorMap = new HashMap<Integer, String>();
		sensorMap.put(0, "Front");
		sensorMap.put(1, "Front Right");
		sensorMap.put(2, "Back Right");
		sensorMap.put(3, "Back");
		sensorMap.put(4, "Back Left");
		sensorMap.put(5, "Front Left");
	}
	private static final Map<Integer, String> decisionsMap;
	static
	{
		decisionsMap = new HashMap<Integer, String>();
		decisionsMap.put(0, "Nothing");
		decisionsMap.put(1, "Forward");
		decisionsMap.put(2, "Backward");
		decisionsMap.put(3, "Rotate Left");
		decisionsMap.put(4, "Rotate Right");
		decisionsMap.put(5, "Turn Left");
		decisionsMap.put(6, "Turn Right");
		decisionsMap.put(7, "Stop");

	}
	// Button for mode
	JButton modeButton = new JButton();
	// Button for printing file
	JButton fileButton = new JButton();
	// The windows panel
	private JPanel panel = new JPanel();
	//screen height - without border, height of title bar and header
	int fullHeight = height-headHeight-2*border-titleHeight;
	// width for 1 screen-snap-unit. 
	int winWidth = (width-(xWindow+4)*border)/xWindow;
	// Boolean for checking if a button is currently pressed down
	boolean buttonPressed = false;
	// Char for storing pressed down button
	char button;


	// Returns the bounds of a pane given start and end. 
	public int[] getBound(int xStart,int yStart,int xEnd,int yEnd){
		// The bounds of a window pane
		int[] bound = new int[4];
		// x position for pane
		bound[0] = border+xStart*(border+winWidth);
		// y position for pane
		bound[1] = headHeight+(2*border+(yStart)*(border+fullHeight/yWindow));
		// width of pane
		bound[2] = (xEnd-xStart)*winWidth;
		// height of pane
		bound[3] = fullHeight*(yEnd-yStart)/yWindow + (yEnd - yStart - 1) *border;

		return bound;
	}
	public void updatePanel(){
		width = panel.getWidth();
		height = panel.getHeight();
		fullHeight = height-headHeight-2*border-titleHeight;
		winWidth = (width-(xWindow+4)*border)/xWindow;
		head.setBounds(
				border,
				border,
				width,
				headHeight);
		int[] bound;
		bound = getBound(0, 0, 1, 4);
		servos.setBounds(bound[0],bound[1],bound[2],bound[3]);

		bound = getBound(1, 0, 2, 2);
		inputs.setBounds(bound[0],bound[1],bound[2],bound[3]);

		bound = getBound(2, 0, 3, 2);
		sensors.setBounds(bound[0],bound[1],bound[2],bound[3]);

		bound = getBound(1, 2, 2, 4);
		decisions.setBounds(bound[0],bound[1],bound[2],bound[3]);

		bound = getBound(2, 2, 3, 3);
		modeButton.setBounds(bound[0],bound[1],bound[2],bound[3]);
		
		bound = getBound(2, 3, 3, 4);
		fileButton.setBounds(bound[0],bound[1],bound[2],bound[3]);

		bound = getBound(2, 3, 3, 5);
		minimap.setBounds(bound[0], bound[1], bound[2], bound[3]);

		
	}

	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, width, height);

		panel.setBorder(new EmptyBorder(border, border, border, border));
		setContentPane(panel);
		panel.setLayout(null);
		panel.addComponentListener(new ComponentAdapter() {
	        public void componentResized(ComponentEvent comp) {
	        	updatePanel();
	        }
	      });
		updatePanel();
		
		head.setFont(new Font("Arial", Font.PLAIN, 43));
		head.setBounds(
				border,
				border,
				width,
				headHeight);
		head.setText(headText);
		panel.add(head);
		// Center text in header
		StyledDocument doc = head.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		servos.setText(servoText);
		servos.setEditable(false);
		panel.add(servos);

		inputs.setText(inputText);
		inputs.setEditable(false);
		panel.add(inputs);

		sensors.setText(sensorText);
		sensors.setEditable(false);
		panel.add(sensors);

		decisions.setText(decisionsText);
		decisions.setEditable(false);
		panel.add(decisions);

		modeButton.setAction(ModeAction);
		modeButton.setText(modeButtonText);
		panel.add(modeButton);
		
		fileButton.setAction(fileAction);
		fileButton.setText(fileActionText);
		panel.add(fileButton);

		panel.add(minimap);


		// Setting up keystrokes for W,A,S,D. Not finished currently
		panel.getInputMap().put(KeyStroke.getKeyStroke("W"), "forward");
		panel.getActionMap().put("forward", forward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("A"), "rotate left");
		panel.getActionMap().put("rotate left", rotateLeft);
		panel.getInputMap().put(KeyStroke.getKeyStroke("S"), "backward");
		panel.getActionMap().put("backward", backward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("D"), "rotate right");
		panel.getActionMap().put("rotate right", rotateRight);
		
		panel.getInputMap().put(KeyStroke.getKeyStroke("U"), "update");
		panel.getActionMap().put("update", updateBT);

		//Setting up keystrokes for releasing W,A,S,D
		panel.getInputMap().put(KeyStroke.getKeyStroke("released W"), "releasedForward");
		panel.getActionMap().put("releasedForward", releasedForward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released A"), "stop left");
		panel.getActionMap().put("stop left", stopLeft);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released S"), "releasedBackward");
		panel.getActionMap().put("releasedBackward", releasedBackward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released D"), "stop right");
		panel.getActionMap().put("stop right", stopRight);
		
		for(int i = 0; i < 24; i++){
			btData[i] = (byte)(i*2);
		}
		btData[24] = 1;

	}

	public static void update(byte[] FireFlyData){
		btData = FireFlyData;
		updateServo(btData);
		updateSensor(btData);
		updateInput();
		updateDecisions(btData[24]);
	}
	static private void updateServo(byte[] data){
		servoText = "Servo \t Value \n";
		// First 18 bits of the Bluetooth data, bit 0-17. Servo information
		for (int j = 0; j < 18; j++){
			servoText += (j + 1) + ": \t" + data[j] + "\n";
		}		
		servos.setText(servoText);

	}
	static private void updateSensor(byte[] data){
		sensorText = "Sensor \t Value \n";
		// Bit 18-24 of Bluetooth data. Sensor information 
		for( int l = 18; l < 24; l++){
			sensorText += sensorMap.get(l-18) + ": \t" + data[l] + "\n";
		}
		sensors.setText(sensorText);

	}
	String logOutput(){
		String output = "Decisions: ";
		for(String s: decisionsList){
			output += (s + ",");
		}
		output += "\r\nInputs: ";
		for(Pair e: kInput){
			output += "(" + e.getLeft() + "," + e.getRight() + ") \n";
		}
		return output;
	}
	static private void updateInput(){
		inputText = "Button \t Duration \n";
		int size = kInput.size();
		if(size > 13){
			size = 13;
		}
		// Iterate through last 14 keyboard input typed
		for(int i = 0; i < size; i++){
			String element = kInput.get(i).getLeft();
			double duration = kInput.get(i).getRight();
			inputText += element + "\t" + duration + "\n";
		}	
		inputs.setText(inputText);
	}
	
	static private void updateDecisions(byte data){
		decisionsText = "Decisions \n";
		int dataI = data;
		// If it is a valid key, representing the correct movement. 
		if(decisionsMap.containsKey(dataI)){
			decisionsList.add(0,decisionsMap.get(dataI));
		}else{
			decisionsList.add(0, "Invalid: " + dataI);
		}
		int size = decisionsList.size();
		if(size > 13){
			size = 13;
		}
		// Iterate through last 14 keyboard input typed
		for(int i = 0; i < size; i++){
			String element = decisionsList.get(i);
			decisionsText += element + "\n";
		}	
		// Iterate every decision in decisionsList
		decisions.setText(decisionsText);

	}

	// Action for button mode
	Action ModeAction = new AbstractAction() {
		int counter = 1;
		byte mode = 0;
		public void actionPerformed(ActionEvent e) {
			counter*=-1;
			// TODO add output when changing mode. 
			if (counter>0){
				mode = 8;
				//FireFly.toRobot(mode);
				robotMode = Mode.AUTO;				
			}else{
				mode = 9;
				//FireFly.toRobot(mode);
				robotMode = Mode.CONTROL;
			}
			modeButton.setText(robotMode.toString());
			panel.grabFocus();
		}
	};
	// Action for button mode
		Action fileAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				PrintWriter out;
				try {
					out = new PrintWriter("output.txt");
					out.println(logOutput());
					out.close();
					System.out.println("Log printed");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				panel.grabFocus();
			}
		};
	
	Action updateBT = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			for(int i = 0; i < 24; i++){
				btData[i] = (byte)(test*3);
			}
			btData[24] = (byte)(test);
			test++;
			if(test > 9){
				test = 0;
			}
			update(btData);
			System.out.println("update");
		}
	};

	// Action for every movement of the robot
	Action forward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to go forwards (until we tell it to stop)
				byte send = 1;
				FireFly.toRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = 'W';
			}
		}
	};
	Action backward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to go backwards (until we tell it to stop)
				byte send = 2;
				FireFly.toRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = 'S';
			}
		}
	};
	Action rotateLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to rotate left (until we tell it to stop)
				byte send = 3;
				FireFly.toRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = 'A';
			}
		}
	};
	Action rotateRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to rotate right (until we tell it to stop)
				byte send = 4;
				FireFly.toRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = 'D';
			}
		}
	};
	// Action for stopping the robot
	Action releasedForward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button == 'W'){
				// Tell robot to stop
				byte send = 5;
				FireFly.toRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair("W" , (double) (2*duration)));
				updateInput();
				buttonPressed = false;
			}
		}
	};
	Action releasedBackward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button == 'S'){
				// Tell robot to stop
				byte send = 5;
				FireFly.toRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair("S" , (double) (2*duration)));
				updateInput();
				buttonPressed = false;
			}
		}
	};
	Action stopLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button == 'A'){
				// Tell robot to stop
				byte send = 5;
				FireFly.toRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair("A" , (double) (2*duration)));
				updateInput();
				buttonPressed = false;
			}
		}
	};
	Action stopRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button == 'D'){
				// Tell robot to stop
				byte send = 5;
				FireFly.toRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair("D" , (double) (2*duration)));
				updateInput();
				buttonPressed = false;
			}
		}
	};	
}
