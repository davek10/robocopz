package guiTest;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.JButton;

import java.awt.Font;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.swing.Action;

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
	// Bluetooth data from the communications unit
	public static byte[] btData = new byte[25];
	// Queue used for the keyboard inputs
	static Queue<Pair> kInput = new LinkedList<Pair>();
	// ArrayList for decisions made by the robot. TODO add special datatype for this.
	public static ArrayList<String> decisionsList = new ArrayList<String>();
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
	// String for the Servos pane
	static String servoText;
	// String for the inputs pane
	static String inputText;
	// String for the sensors pane
	static String sensorText;
	// String for the modeButton
	static String modeButtonText;
	// String for the head pane
	static String headText;
	// String for the decisions pane
	static String decisionsText;
	
	private static final Map<Integer, String> sensorMap;
    static
    {
        sensorMap = new HashMap<Integer, String>();
        sensorMap.put(1, "Front");
        sensorMap.put(2, "Front Right");
        sensorMap.put(3, "Back Right");
        sensorMap.put(4, "Back");
        sensorMap.put(5, "Back Left");
        sensorMap.put(6, "Front Left");
    }
	// Button for mode
	JButton mode = new JButton();
	// Mode for the robot
	Mode robotMode = Mode.AUTO;
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

	public static void main(String[] args) {
		//path.add(new DirChoice(true, false, true, 1));
		//path.add(new DirChoice(true, false, true, 2));
		//path.add(new DirChoice(true, false, true, 3));

		// Code for enabling events when pressing buttons
		EventQueue.invokeLater(new Runnable() {
			// Run method for the frame
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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


	public GUI() {
		for (int i = 0; i < 25; i++){
			btData[i] = (byte) (i*5);
		}
		servoText = "Servo \t Value \n";
		inputText = "Button \t Duration \n";
		sensorText = "Sensor \t Value \n";
		modeButtonText = robotMode.toString();
		headText = "\t\t ROBOKAPPA";
		decisionsText = "Decisions \n";

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, width, height);
		
		panel.setBorder(new EmptyBorder(border, border, border, border));
		setContentPane(panel);
		panel.setLayout(null);

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


		int[] bound;
		bound = getBound(0, 0, 1, 4);
		servos.setBounds(bound[0],bound[1],bound[2],bound[3]);
		servos.setText(servoText);
		servos.setEditable(false);
		panel.add(servos);

		bound = getBound(1, 0, 2, 2);
		inputs.setBounds(bound[0],bound[1],bound[2],bound[3]);
		inputs.setText(inputText);
		inputs.setEditable(false);
		panel.add(inputs);

		bound = getBound(2, 0, 3, 2);
		sensors.setBounds(bound[0],bound[1],bound[2],bound[3]);
		sensors.setText(sensorText);
		sensors.setEditable(false);
		panel.add(sensors);
		
		bound = getBound(1, 2, 2, 4);
		decisions.setBounds(bound[0],bound[1],bound[2],bound[3]);
		decisions.setText(decisionsText);
		decisions.setEditable(false);
		panel.add(decisions);

		Action action = new ModeAction();
		mode.setAction(action);
		bound = getBound(2, 2, 3, 3);
		mode.setBounds(bound[0],bound[1],bound[2],bound[3]);
		mode.setText(modeButtonText);
		panel.add(mode);

		JPanel minimap = new JPanel();
		bound = getBound(2, 3, 3, 5);
		minimap.setBounds(bound[0], bound[1], bound[2], bound[3]);
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

		//Setting up keystrokes for releasing W,A,S,D
		panel.getInputMap().put(KeyStroke.getKeyStroke("released W"), "releasedForward");
		panel.getActionMap().put("releasedForward", releasedForward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released A"), "stop left");
		panel.getActionMap().put("stop left", stopLeft);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released S"), "releasedBackward");
		panel.getActionMap().put("releasedBackward", releasedBackward);
		panel.getInputMap().put(KeyStroke.getKeyStroke("released D"), "stop right");
		panel.getActionMap().put("stop right", stopRight);
	}
	
	public static void update(byte[] FireFlyData){
		btData = FireFlyData;
		servoText = "Servo \t Value \n";
		// First 18 bits of the bluetooth data, bit 0-17. Servo information
		for (int j = 0; j < 18; j++){
			servoText += (j + 1) + ": \t" + btData[j] + "\n";
		}
		servos.setText(servoText);
		// Bit 18-24 of bluetooth data. Sensor information 
		sensorText = "Sensor \t Value \n";
		for( int l = 18; l < 25; l++){
			sensorText += sensorMap.get(l) + ": \t" + btData[l + 18] + "\n";
		}
		sensors.setText(sensorText);
		// 
		inputText = "Button \t Duration \n";
		for(Object object : kInput) {
			String element = (String) object.toString();
			inputText += element + "\n";
		}
		inputs.setText(inputText);
		
		decisionsText = "Decisions \n";
		for(String str: decisionsList){
			decisionsText += str + "\n";
		}
		
	}

	// Action for button mode
	private class ModeAction extends AbstractAction {
		int counter = 1;
		public void actionPerformed(ActionEvent e) {
			counter*=-1;
			if (counter>0){
				robotMode = Mode.AUTO;
			}else{
				robotMode = Mode.CONTROL;
			}
			mode.setText(robotMode.toString());
		}
	}

	// Action for every movement of the robot
	Action forward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to go forwards (until we tell it to stop)
				System.out.println("forward");
				buttonPressed = true;
				button = 'W';
			}
		}
	};
	Action backward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to go backwards (until we tell it to stop)
				System.out.println("backward");
				buttonPressed = true;
				button = 'S';
			}
		}
	};
	Action rotateLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to rotate left (until we tell it to stop)
				System.out.println("rotate left");
				buttonPressed = true;
				button = 'A';
			}
		}
	};
	Action rotateRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(!buttonPressed){
				// Tell robot to rotate right (until we tell it to stop)
				System.out.println("rotate right");
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
				System.out.println("stop forward");
				buttonPressed = false;
			}
		}
	};
	Action releasedBackward = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button == 'S'){
				// Tell robot to stop
				System.out.println("stop backward");
				buttonPressed = false;
			}
		}
	};
	Action stopLeft = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button == 'A'){
				// Tell robot to stop
				System.out.println("stop left");
				buttonPressed = false;
			}
		}
	};
	Action stopRight = new AbstractAction(){
		public void actionPerformed(ActionEvent e){
			if(button == 'D'){
				// Tell robot to stop
				System.out.println("stop right");
				buttonPressed = false;
			}
		}
	};	
}
