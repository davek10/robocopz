package guiTest;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.Component;
import java.awt.Font;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.Action;

public class GUI extends JFrame {
	
	int border = 5;
	int width = 640;
	int height = 480;
	int headHight = 50;
	int xWindow = 0;
	int yWindow = 0;
	public static ArrayList<DirChoice> path = new ArrayList();
	
	
	
	
	
	
	JButton button1 = new JButton();

	private JPanel panel;
	

	public static void main(String[] args) {
		path.add(new DirChoice(true, false, true, 1));
		path.add(new DirChoice(true, false, true, 2));
		path.add(new DirChoice(true, false, true, 3));

		
		EventQueue.invokeLater(new Runnable() {
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


	public int[] getBound(int xStart,int yStart,int xEnd,int yEnd){
		int[] bound = new int[4];
		
		int fullHeight = height-headHight-2*border-43;
		int winWidth = (width-(xWindow+4)*border)/xWindow;
		int winHeight = fullHeight*(yEnd-yStart)/yWindow-yWindow;
		

		bound[0] = border+xStart*(border+winWidth);
		bound[1] = headHight+(2*border+(yStart)*(border+winHeight));
		bound[2] = (xEnd-xStart)*winWidth;
		bound[3] = winHeight; 
		
		System.out.println(bound[0]);
		System.out.println(winWidth+"win");
		return bound;
	}
	
	
	public GUI() {
		String t1 = "t1";
		
		String t2 = "t2";
		
		String t3 = "t3";
		
		String t4 = "t4";

		String headText = "\t\t ROBOKAPPA";
		
		//xWindow = total nr of snappoints in x-dir
		//snappoints numbered 0-5
		xWindow = 3;
		
		//yWindow = total nr of snappoints in y-dir
		//snappoints numbered 0-5
		yWindow = 5;
		
		int fullHeight = height-headHight-2*border-43;
		int winWidth = (width-(xWindow+4)*border)/xWindow;
		int winHeight = fullHeight/yWindow-yWindow;

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
				(winWidth)*xWindow+ (xWindow-1)*border,
				headHight);
		head.setText(headText);
		panel.add(head);
		
		int[] bound;

		JTextPane pane1 = new JTextPane();
		bound = getBound(0, 0, 1, 4);
		pane1.setBounds(bound[0],bound[1],bound[2],bound[3]);
		pane1.setText(t1);
		panel.add(pane1);
		
		bound = getBound(1, 0, 2, 4);
		JTextPane pane2 = new JTextPane();
		pane2.setBounds(bound[0],bound[1],bound[2],bound[3]);
		pane2.setText(t2);
		panel.add(pane2);
		
		bound = getBound(2, 0, 3, 2);
		JTextPane pane3 = new JTextPane();
		pane3.setBounds(bound[0],bound[1],bound[2],bound[3]);
		pane3.setText(t3);
		panel.add(pane3);
		

		
		Action action = new Button1Action();
		button1.setAction(action);
		bound = getBound(2, 2, 3, 3);
		button1.setBounds(bound[0],bound[1],bound[2],bound[3]);
		button1.setText(t4);
		panel.add(button1);
		
		JPanel minimap = new JPanel();
		bound = getBound(2, 3, 3, 5);
		minimap.setBounds(bound[0], bound[1], bound[2], bound[3]);
		panel.add(minimap);
			
	}
	private class Button1Action extends AbstractAction {
		int counter = 1;
		public void actionPerformed(ActionEvent e) {
			counter*=-1;
			if (counter>0){
			button1.setText("t44");
		}else{
			button1.setText("t45");
		}
			}
	}
}
