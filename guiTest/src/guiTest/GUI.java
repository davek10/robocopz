package guiTest;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;
import javax.swing.JButton;

import java.awt.Font;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.Action;

public class GUI extends JFrame {
	// Height of title bar at the top of window
	int titleHeight = 43;
	// size of border
	int border = 5;
	// width of window
	int width = 1920;
	// height of windows
	int height = 1080;
	// height of header
	int headHeight = 50;
	//xWindow = total number of snap-points in x
	int xWindow = 3;
	//yWindow = total number of snap-points in y
	int yWindow = 5;
	// ArrayList for path
	public static ArrayList<DirChoice> path = new ArrayList<DirChoice>();
	// Button for mode
	JButton mode = new JButton();
	// The windows panel
	private JPanel panel;
	//screen height - without border, height of title bar and header
	int fullHeight = height-headHeight-2*border-titleHeight;
	
	// width for 1 screen-snap-unit. 
	int winWidth = (width-(xWindow+4)*border)/xWindow;
	

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
		bound[3] = fullHeight*(yEnd-yStart)/yWindow;

		return bound;
	}
	
	
	public GUI() {
		String t1 = "t1";
		
		String t2 = "t2";
		
		String t3 = "t3";
		
		String t4 = "t4";

		String headText = "\t\t ROBOKAPPA";

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, width, height);
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(border, border, border, border));
		setContentPane(panel);
		panel.setLayout(null);
		
		
		
		JTextPane head = new JTextPane();
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

		JTextPane pane1 = new JTextPane();
		bound = getBound(0, 0, 1, 2);
		pane1.setBounds(bound[0],bound[1],bound[2],bound[3]);
		pane1.setText(t1);
		panel.add(pane1);
		
		bound = getBound(0, 2, 1, 4);
		JTextPane pane2 = new JTextPane();
		pane2.setBounds(bound[0],bound[1],bound[2],bound[3]);
		pane2.setText(t2);
		panel.add(pane2);
		
		bound = getBound(2, 0, 3, 2);
		JTextPane pane3 = new JTextPane();
		pane3.setBounds(bound[0],bound[1],bound[2],bound[3]);
		pane3.setText(t3);
		panel.add(pane3);
		
		Action action = new ModeAction();
		mode.setAction(action);
		bound = getBound(2, 2, 3, 3);
		mode.setBounds(bound[0],bound[1],bound[2],bound[3]);
		mode.setText(t4);
		panel.add(mode);
		
		JPanel minimap = new JPanel();
		bound = getBound(2, 3, 3, 5);
		minimap.setBounds(bound[0], bound[1], bound[2], bound[3]);
		panel.add(minimap);
			
	}
	// Action for button mode
	private class ModeAction extends AbstractAction {
		int counter = 1;
		public void actionPerformed(ActionEvent e) {
			counter*=-1;
			if (counter>0){
			mode.setText("Robo");
		}else{
			mode.setText("Kappa");
		}
			}
	}
}
