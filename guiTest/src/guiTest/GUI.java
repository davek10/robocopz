package guiTest;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
/*
 * data[0..5] = sensor info
 * data[6..23] = servo info
 * data[24] = mode 
 */

public class GUI extends JFrame {
	// Height of title bar at the top of window
	int titleHeight = 43;
	// size of border
	int border = 5;
	// width of window
	int width = 1280;
	// height of windows
	int height = 720;
	// height of header
	int headHeight = 50;
	//xWindow = total number of snap-points in x
	int xWindow = 3;
	//yWindow = total number of snap-points in y
	int yWindow = 5;
	
	// ArrayList for path
	public static ArrayList<DirChoice> path = new ArrayList<DirChoice>();
	// Length of btData
	public static int btDataLength = 25;
	// Bluetooth data from the communications unit
	public static int[] btData;
	// ArrayList used for the keyboard inputs
	public static ArrayList<Pair> kInput = new ArrayList<Pair>();
	// ArrayList for decisions made by the robot. TODO add special datatype for this.
	public static ArrayList<Pair> decisionsList = new ArrayList<Pair>();
	// Arraylist for actions made when pressing keyboard buttons q,w,e,a,s,d
	public static ArrayList<Action> actionList = new ArrayList<Action>();

	// Strings for building the path string of images
	private static final String IMG_PATH = "src/img/";
	private static String controlIMG = "control";
	private static String autoIMG = "auto";
	private static final String IMG_END = ".png";
	
	// Mode for the robot
	static Mode robotMode = Mode.CONTROL ;
	
	// Button for mode
	static JButton modeButton = new JButton();
	// Button for submitting a file with control decisions to robot
	static JButton submitButton = new JButton();
	// Button for printing file
	JButton fileButton = new JButton();
	// The windows panel
	private JPanel panel = new JPanel();
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
	// Label containing images for pressed down buttons
	static JLabel kButtons = new JLabel();
	// Output for console
	static JTextArea console = new JTextArea(50, 10);
	// ScrollPane containing console
	static JScrollPane sp = new JScrollPane(console);
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
	// String for the submitButton
	static String submitButtonText = "Submit";
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
	
	// Byte for stopping
	static int stop = 7;
	
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
	
	// Mapping decision bytes to corresponding strings
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
		decisionsMap.put(8, "Auto mode");
		decisionsMap.put(9, "Control mode");

	}	
	
	//screen height - without border, height of title bar and header
	int fullHeight = height-headHeight-2*border-titleHeight;
	// width for 1 screen-snap-unit. 
	int winWidth = (width-(xWindow+4)*border)/xWindow;
	// Boolean for checking if a button is currently pressed down
	boolean buttonPressed = false;
	// Char for storing pressed down button
	String button = "";


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
	// Updates bounds for the panel and every JComponent
	public void updatePanel(){
		width = panel.getWidth();
		height = panel.getHeight();
		fullHeight = height-headHeight-2*border-titleHeight;
		winWidth = (width-(xWindow+4)*border)/xWindow;
		head.setBounds(border,border,width,headHeight);
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
		/*
		bound = getBound(2, 3, 3, 5);
		minimap.setBounds(bound[0], bound[1], bound[2], bound[3]);
		 */
		bound = getBound(0, 4, 1, 5);
		sp.setBounds(bound[0],bound[1],bound[2],bound[3]);

		bound = getBound(1, 4, 2, 5);
		kButtons.setBounds(bound[0],bound[1],bound[2],bound[3]);
		
		bound = getBound(2, 4, 3, 5);
		submitButton.setBounds(bound[0],bound[1],bound[2],bound[3]);
	}

	// Updates the image in JLabel kButtons with image name given by IMG
	static void UpdateImage(String IMG){
		try {
			BufferedImage img = ImageIO.read(new File(IMG_PATH + IMG + IMG_END));
			ImageIcon icon = new ImageIcon(img);
			kButtons.setIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Initializes all JComponents
	void init(){
		panel.setBorder(new EmptyBorder(border, border, border, border));
		setContentPane(panel);
		panel.setLayout(null);
		// Every time the window is resized, call updatePanel
		panel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent comp) {
				updatePanel();
			}
		});
		// Set the image to the 'neutral' one
		UpdateImage(controlIMG);
		// Set all the bounds for the JComponents
		updatePanel();
		
		// Sets output of the console to sp
		PrintStream printStream = new PrintStream(new CustomOutputStream(console));
		System.setOut(printStream);
		System.setErr(printStream);
		panel.add(sp);

		head.setFont(new Font("Arial", Font.PLAIN, 43));
		head.setBounds(border,border,width,headHeight);
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
		
		submitButton.setAction(submitAction);
		submitButton.setText(submitButtonText);
		panel.add(submitButton);
		
		panel.add(kButtons);
		panel.add(minimap);
	}

	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Setting bounds for panel
		setBounds(0, 0, width, height);
		// Initiate values for textAreas and other JComponents
		init();
		// Add every keyboard press and release action to a list
		actionList.add(forward);
		actionList.add(releasedForward);
		actionList.add(rotateLeft);
		actionList.add(stopLeft);
		actionList.add(backward);
		actionList.add(releasedBackward);
		actionList.add(rotateRight);
		actionList.add(stopRight);
		actionList.add(walkLeft);
		actionList.add(stopWLeft);
		actionList.add(walkRight);
		actionList.add(stopWRight);

		// Setting up keystrokes for W,A,S,D,Q,E. Pressing and releasing
		panel.getInputMap().put(KeyStroke.getKeyStroke("W"), "forward");
		panel.getActionMap().put("forward", forward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released W"), "releasedForward");
		panel.getActionMap().put("releasedForward", releasedForward);

		panel.getInputMap().put(KeyStroke.getKeyStroke("A"), "rotate left");
		panel.getActionMap().put("rotate left", rotateLeft);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released A"), "stop left");
		panel.getActionMap().put("stop left", stopLeft);

		panel.getInputMap().put(KeyStroke.getKeyStroke("S"), "backward");
		panel.getActionMap().put("backward", backward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released S"), "releasedBackward");
		panel.getActionMap().put("releasedBackward", releasedBackward);

		panel.getInputMap().put(KeyStroke.getKeyStroke("D"), "rotate right");
		panel.getActionMap().put("rotate right", rotateRight);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released D"), "stop right");
		panel.getActionMap().put("stop right", stopRight);

		panel.getInputMap().put(KeyStroke.getKeyStroke("Q"), "walk left");
		panel.getActionMap().put("walk left", walkLeft);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released Q"), "stop wleft");
		panel.getActionMap().put("stop wleft", stopWLeft);

		panel.getInputMap().put(KeyStroke.getKeyStroke("E"), "walk right");
		panel.getActionMap().put("walk right", walkRight);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released E"), "stop wright");
		panel.getActionMap().put("stop wright", stopWRight);


	}
	// Turn a byte into unsigned Integer
	public static int byteToUnsigned(byte b) {
		return b & 0xFF;
	}
	// Update textareas from information in int[] buffer
	public static void update(int[] buffer){
		updateSensor(Arrays.copyOfRange(buffer, 0, 6));
		updateServo(Arrays.copyOfRange(buffer, 6, 24));
		updateInput();
		updateDecisions(buffer[buffer.length - 2]);
		updateMode(buffer[buffer.length - 1]);
	}
	// Disable actions for keyboard presses q,w,e,a,s,d
	static void disableActions(){
		for(Action a: actionList){
			a.setEnabled(false);
		}
	}
	// Enable actions for keyboard presses q,w,e,a,s,d
	static void enableActions(){
		for(Action a: actionList){
			a.setEnabled(true);
		}
	}
	// Update servo values from int[] buffer
	static private void updateServo(int[] buffer){
		servoText = "Servo \t Value \n";
		// First 18 bits of the Bluetooth data, bit 0-17. Servo information
		for (int j = 0; j < buffer.length; j++){
			servoText += (j) + ": \t" + buffer[j] + "\n";
		}		
		servos.setText(servoText);

	}
	// Update sensor values from int[] buffer
	static private void updateSensor(int[] buffer){
		sensorText = "Sensor \t Value \n";
		// Bit 18-24 of Bluetooth data. Sensor information 
		for( int l = 0; l < buffer.length; l++){
			sensorText += sensorMap.get(l) + ": \t" + buffer[l] + "\n";
		}
		sensors.setText(sensorText);

	}
	// Used for printing information to the log
	String logOutput(){
		String output = "Decisions: ";
		for(Pair s: decisionsList){
			output += (s.getLeft() + "(" + (int) (s.getRight()) + "), \n");
		}
		output += "\r\nInputs: ";
		for(Pair e: kInput){
			output += "(" + e.getLeft() + "," + e.getRight() + "), \n";
		}
		return output;
	}
	// Update the list of inputs
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
	// Update the decisions text from information in buffer
	static private void updateDecisions(int buffer){
		decisionsText = "Decisions \n";
		String decision;
		// If it is a valid key, representing the correct movement. 
		if(decisionsMap.containsKey(buffer)){
			decision = decisionsMap.get(buffer);
		}else{
			decision = "Invalid: " + buffer;
		}
		if(decisionsList.size() > 0 && (decisionsList.get(0).getLeft().equals(decision))){
			double temp = decisionsList.get(0).getRight();
			decisionsList.set(0, new Pair(decision, temp + 1));
		}else{
			decisionsList.add(0, new Pair(decision, 1));
		}
		int size = decisionsList.size();
		if(size > 13){
			size = 13;
		}
		// Iterate through last 14 keyboard input typed
		for(int i = 0; i < size; i++){
			String element = decisionsList.get(i).getLeft();
			double times = decisionsList.get(i).getRight();
			decisionsText += element + " (" + (int)(times) + ")" + "\n";
		}	
		// Iterate every decision in decisionsList
		decisions.setText(decisionsText);

	}
	// Update mode given Integer buffer
	static void updateMode(int buffer){
		if(buffer == 1){
			if(robotMode != Mode.AUTO){
				UpdateImage(autoIMG);
			}
			robotMode = Mode.AUTO;
			disableActions();
		}else{
			if(robotMode != Mode.CONTROL){
				UpdateImage(controlIMG);
			}
			robotMode = Mode.CONTROL;
			enableActions();
		}
		modeButton.setText(robotMode.getString());
	}

	// Action for button mode
	Action ModeAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			if (robotMode == Mode.CONTROL){
				robotMode = Mode.AUTO;				
			}else{
				robotMode = Mode.CONTROL;
			}
			FireFly.instrToRobot(robotMode.getInt());
			modeButton.setText(robotMode.getString());
			System.out.println("Switched mode: " + robotMode.getString());
			// Return focus to the panel so we can still press buttons on the keyboard
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
	// Action for submitButton
		Action submitAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// What the file is named, and in which directory
				String fileName = "src/txt/vars.txt";
				// The arraylist we save the integers we have read from the file
				ArrayList<Integer> readList = new ArrayList<Integer>();
				try{
					File file = new File(fileName);
					Scanner sc = new Scanner(file);
					// While there is a line to read
					while (sc.hasNextLine()) {
						// Read one line
						String line = sc.nextLine();
						// Split the line into several parts with delimiter given from line.split
						String[] tokens = line.split("= ");
						int output = 0;
						try{
							output = Integer.parseInt(tokens[1]);
							readList.add(output);
						}catch(ArrayIndexOutOfBoundsException exception) {
							System.out.println("Error in vars file, check for empty lines");
						}
						// Take the token in the second part of the list, when using format example 'var = 7'
					}
					sc.close();
				} catch (Exception ex){
					ex.printStackTrace();
				}
				// Send the arraylist of Integers to the robot
				 FireFly.paramsToRobot(readList);
				 // Return focus to the panel so we can still press buttons on the keyboard
				 panel.grabFocus();
			}
		};

	// Action for every movement of the robot
	Action forward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			// Only perform this when a button is not already pressed down
			if(!buttonPressed){
				// Tell robot to go forwards (until we tell it to stop)
				int send = 1;
				// Send instruction send to robot
				FireFly.instrToRobot(send);
				// Start the counter of how long we have held the button down
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				// Set the button pressed down
				button = "W";
				// Update the image displaying currently held down button
				UpdateImage(button);
			}
		}
	};
	Action backward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to go backwards (until we tell it to stop)
				int send = 2;
				FireFly.instrToRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = "S";
				UpdateImage(button);
			}
		}
	};
	Action rotateLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to rotate left (until we tell it to stop)
				int send = 3;
				FireFly.instrToRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = "A";
				UpdateImage(button);
			}
		}
	};
	Action rotateRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to rotate right (until we tell it to stop)
				int send = 4;
				FireFly.instrToRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = "D";
				UpdateImage(button);
			}
		}
	};
	Action walkLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to go forwards (until we tell it to stop)
				int send = 5;
				FireFly.instrToRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = "Q";
				UpdateImage(button);
			}
		}
	};
	Action walkRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to go forwards (until we tell it to stop)
				int send = 6;
				FireFly.instrToRobot(send);
				startTime = System.currentTimeMillis();
				buttonPressed = true;
				button = "E";
				UpdateImage(button);
			}
		}
	};
	// Action for stopping the robot
	Action releasedForward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			// If the button is the one we held down first
			if(button.equals("W")){
				// Tell robot to stop
				int send = stop;
				// Send stop instruction to robot
				FireFly.instrToRobot(send);
				// Calculate passed time in seconds
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				// Add the held down button and the time to kInput. duration is half the time for some reason
				kInput.add(0, new Pair(button, (double) (2*duration)));
				// Update JTextArea containing pressed down buttons
				updateInput();
				// Release the first button held down
				buttonPressed = false;
				// Update image to the one in the 'neutral' state
				UpdateImage(controlIMG);
			}
		}
	};
	Action releasedBackward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button.equals("S")){
				// Tell robot to stop
				int send = stop;
				FireFly.instrToRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair(button , (double) (2*duration)));
				updateInput();
				buttonPressed = false;
				UpdateImage(controlIMG);
			}
		}
	};
	Action stopLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button.equals("A")){
				// Tell robot to stop
				int send = stop;
				FireFly.instrToRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair(button, (double) (2*duration)));
				updateInput();
				buttonPressed = false;
				UpdateImage(controlIMG);
			}
		}
	};
	Action stopRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button.equals("D")){
				// Tell robot to stop
				int send = stop;
				FireFly.instrToRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair(button, (double) (2*duration)));
				updateInput();
				buttonPressed = false;
				UpdateImage(controlIMG);
			}
		}
	};	
	Action stopWLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button.equals("Q")){
				// Tell robot to stop
				int send = stop;
				FireFly.instrToRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair(button , (double) (2*duration)));
				updateInput();
				buttonPressed = false;
				UpdateImage(controlIMG);
			}
		}
	};
	Action stopWRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button.equals("E")){
				// Tell robot to stop
				int send = stop;
				FireFly.instrToRobot(send);
				duration = (double)(System.currentTimeMillis() - startTime)/1000;
				kInput.add(0, new Pair(button, (double) (2*duration)));
				updateInput();
				buttonPressed = false;
				UpdateImage(controlIMG);
			}
		}
	};
}
