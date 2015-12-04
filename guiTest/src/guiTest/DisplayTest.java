package guiTest;


import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DisplayTest extends JFrame {
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
	public static ArrayList<String> inputList = new ArrayList<String>();
	// Text pane for the head
	static JTextPane head = new JTextPane();
	//Text pane for the servos data
	static JTextPane input = new JTextPane();
	// String for the Servos pane
	static String inputText;
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


	public DisplayTest() {
		inputText = "Servo \t Value \n";

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
		head.setText("Display test");
		head.setEditable(false);
		panel.add(head);
		// Center text in header
		StyledDocument doc = head.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		int[] bound;
		bound = getBound(0, 0, 4, 4);
		input.setBounds(bound[0],bound[1],bound[2],bound[3]);
		input.setText(inputText);
		input.setEditable(false);
		panel.add(input);
		System.out.println("test");
		input.addKeyListener(new KeyListener() {

		      /** Handle the key typed event from the text field. */
		      public void keyTyped(KeyEvent e) {
		       // displayInfo(e, "KEY TYPED: ");
		      }

		      /** Handle the key pressed event from the text field. */
		      public void keyPressed(KeyEvent e) {
		    	  int keyCode = e.getKeyCode() % 255;
		    	  FireFly.instrToRobot((byte) keyCode);
		    	  inputList.add(KeyEvent.getKeyText(keyCode));
		          System.out.println("key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")");
		          updateServo();
		      }

		      /** Handle the key released event from the text field. */
		      public void keyReleased(KeyEvent e) {
		        //System.out.print("Release");
		      }
		});
	}

	static private void updateServo(){
		inputText = "";
		// First 18 bits of the Bluetooth data, bit 0-17. Servo information
		for (int j = 0; j < inputList.size(); j++){
			inputText += inputList.get(j);
		}		
		input.setText(inputText);

	}
}
