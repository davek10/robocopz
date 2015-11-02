package guiTest;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

public class Draw extends JPanel{
	private void draw(Graphics g){
		Graphics2D gd = (Graphics2D) g;
		

	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		g.setColor(Color.BLACK);
		//g.
		Image image = new Image();
		draw(g);
	}
}
