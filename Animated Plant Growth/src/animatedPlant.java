//animatedPlant.java
// - use threading to display animated simulation of plant growth
//
// Jennifer Cremer

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.SwingUtilities;						//***


public class animatedPlant {
	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	
	public static void main(String[] args) {			//*** run on main thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	private static void createAndShowGUI() {			//*** run on the EDT
		JFrame frame = new ImageFrame( WIDTH, HEIGHT );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
//#############################################################
class ImageFrame extends JFrame{
	inputs UI;
	paint plantColor;
	BasicStroke[] thickness;
	Color bottom = new Color(0xFF652121);
	Color tips = Color.GREEN;
	//=========================================================
	//constructor
	public ImageFrame(int width, int height){
		setTitle("CAP 3027 2016 - HW05b - Jennifer Cremer");
		setSize(width, height);
		addMenu();
	}
	private void addMenu(){
		//---------------------------------------------------
		//setup frame's menu bar
		
		// === File Menu
		JMenu menu = new JMenu("File");
		// --- Generate Random Plant
			JMenuItem plantItem = new JMenuItem("Generate Random Plant");
			plantItem.addActionListener(new ActionListener() {
				public void actionPerformed( ActionEvent e ){		//*** run on EDT
					simulate();
				}
			});
		menu.add(plantItem);
		// --- Exit
			plantItem = new JMenuItem("Exit");
			plantItem.addActionListener(new ActionListener(){
				public void actionPerformed( ActionEvent e) {		//*** run on EDT
					System.exit(0);
				}
			});
		menu.add(plantItem);
		
		// === Color Menu
		JMenu color = new JMenu("Colors");
		// --- Custom Colors
			JMenuItem colorItem = new JMenuItem("Customize Colors");
			colorItem.addActionListener(new ActionListener() {
				public void actionPerformed( ActionEvent e ){		//*** run on EDT
					customColor();
				}
			});
		color.add(colorItem);
		// --- Default
			colorItem = new JMenuItem("Default");
			colorItem.addActionListener(new ActionListener(){
				public void actionPerformed( ActionEvent e) {		//*** run on EDT
					defaultColor();
				}
			});
		color.add(colorItem);
			
		// === attach menu to menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		menuBar.add(color);
		this.setJMenuBar(menuBar);
	}
	
	//==================================================================
	// simulate() 
	private void simulate(){
		new Thread( new Runnable() {
			public void run() {
				UI = new inputs();		//user inputs
				plantColor = new paint(bottom, tips, UI.steps);	//initialize colors
				thickness = new BasicStroke[UI.steps];		//initialize thickness array
				float dt = 5.5f/(UI.steps-1);				//thickness delta
				float trunk = 6.0f;
				for(int t = 0; t < UI.steps; t++){
					thickness[t] = new BasicStroke(trunk);	//fill array
					trunk -= dt;
				}
				stems[] branch = new stems[UI.stems*UI.steps];	//branch array with space for all segments
				for(int g = 0; g < UI.steps; g++){				//increment through steps
					final BufferedImage img = makeImage(g, branch);	//make image
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {						// EDT display image
							displayBufferedImage(img);
						}
					});
				}
			}
		}).start();
	}
	//===================================================================
	// Display BufferedImage
	private void displayBufferedImage(BufferedImage image){		//*** run on EDT
		setContentPane( new JLabel( new ImageIcon(image)));
		validate();
	}
	//================================================================
	// makeImage() - to be replaced later with grow()
	private BufferedImage makeImage(int g, stems[] branch){		//*** run on worker 
		BufferedImage image = new BufferedImage( inputs.height, inputs.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		//background
		g2d.setColor(Color.BLACK);
		g2d.fillRect( 0, 0, inputs.height, inputs.height);
		
		RenderingHints hint = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHints( hint );
		
			for(int b = 0; b < UI.stems; b++){
				if(g==0){branch[b] = new stems();}	//initialize base stems
				else{
					int q = (g-1)*UI.stems + b;
					branch[g*UI.stems + b] = new stems(	//subsequent segments based off last segment end points
							branch[q].theta, branch[q].segment, branch[q].x, branch[q].y, branch[q].direct);
					for (int i = 0; i < g; i++){		//run through past segment levels
						for(int a = 0; a < UI.stems; a++){	//run through stems
							int p = (i)*UI.stems + a; 
							g2d.setStroke( thickness[UI.steps - 1 - (g-i)] );
							g2d.setColor(plantColor.pcolor(UI.steps - 1 - (g-i)));
							g2d.draw(branch[p].plants);
						}
					}
				}
				g2d.setStroke( thickness[UI.steps - 1] );
				g2d.setColor(plantColor.pcolor(UI.steps - 1));
				branch[g*UI.stems + b].plot();
				g2d.draw(branch[g*UI.stems + b].plants);
			}
		
		return image;
	}
	// -------------------------------------------------------------
	//defaultColor()
	private void defaultColor(){
		bottom = new Color(0xFF652121);
		tips = Color.GREEN;
		simulate();
	}
	private void customColor(){
		String base = JOptionPane.showInputDialog("Specify the base Color");
		int bC = (int) Long.parseLong(base.substring( 2, base.length() ), 16);
		Color bColor = new Color(bC);
		
		String tip = JOptionPane.showInputDialog("Specify the tip Color");
		int tC = (int) Long.parseLong(tip.substring( 2, tip.length() ), 16);
		Color tColor = new Color(tC);
		
		bottom = bColor;
		tips = tColor;
		simulate();
	}
}