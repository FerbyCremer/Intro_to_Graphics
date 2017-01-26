//Lsystem.java
// 
//
// Jennifer Cremer

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.SwingUtilities;						//***

public class Lsystem {
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
	private final JFileChooser chooser;
	protected BufferedImage img;
	protected int delta;
	protected int scale;
	protected String omega;
	protected String[] rules = new String[10];
	int colorb;
	int colorf;
	protected String instructions;
	protected Stack<LState> stack = new Stack<LState>();
	private final char[] def = { 'F', 'f', 'R', 'r', 'L', 'l', '+', '-' };
	protected double modX;
	protected double modY;
	//=========================================================
	//constructor
	public ImageFrame(int width, int height){
		setTitle("CAP 3027 2016 - HW08 - Jennifer Cremer");
		setSize(width, height);

		addMenu();

		// ----- setup file chooser dialog
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
	}
	private void addMenu(){
		//---------------------------------------------------
		//setup frame's menu bar

		// === File Menu
		JMenu menu = new JMenu("File");
		// --- Load L-System
		JMenuItem item = new JMenuItem("Load L-System");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				load();
			}
		});
		menu.add(item);
		// --- Configure
		item = new JMenuItem("Configure Image");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				configure();
			}
		});
		menu.add(item);
		// --- Display L-System
		item = new JMenuItem("Display L-System");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				display();
			}
		});
		menu.add(item);
		// --- save
		item = new JMenuItem("Save Image");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				save();
			}
		});
		menu.add(item);
		// --- Exit
		item = new JMenuItem("Exit");
		item.addActionListener(new ActionListener(){
			public void actionPerformed( ActionEvent e) {		//*** run on EDT
				System.exit(0);
			}
		});
		menu.add(item);
		// === attach menu to menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

	//==================================================================
	// === load() 
	private void load(){
		new Thread( new Runnable() {
			public void run() {
				File file = getFile();
				String line = "";
				BufferedReader lines;
				try {
					lines = new BufferedReader(new FileReader(file));
					int lineCount = 1;
					while((line = lines.readLine()) != null){
						String tran = line;
						Scanner s = new Scanner(tran);
						s.useDelimiter("\n");
						if ( lineCount == 1 )
							delta = s.nextInt();
						else if ( lineCount == 2 )
							scale = s.nextInt();
						else if ( lineCount == 3 )
							omega = new String(s.nextLine());
						else{
							int index = lineCount - 4;
							rules[index] = s.nextLine();
						}
						lineCount++;
						s.close();
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();}catch (IOException e) {e.printStackTrace();}
			}
		}).start();
	}
	// =====================================================================
	// === configure()
	private void configure(){
		new Thread( new Runnable() {
			public void run() {
				String str = JOptionPane.showInputDialog("What is the image's width?");
				int width = Integer.parseInt(str);
				String str2 = JOptionPane.showInputDialog("What is the image's height?");
				int height = Integer.parseInt(str2);
				String colorB = JOptionPane.showInputDialog("What is the background color?");
				int color1 = (int) Long.parseLong(colorB.substring( 2, colorB.length() ), 16);
				String colorF = JOptionPane.showInputDialog("What is the Foreground color?");
				int color2 = (int) Long.parseLong(colorF.substring( 2, colorF.length() ), 16);

				img = makeImage(width, height, color1, color2);	//make image
			}
		}).start();
	}
	// =====================================================================
	// === display()
	private void display(){
		new Thread( new Runnable() {
			public void run() {
				String n = JOptionPane.showInputDialog("How many generations?");
				final int gen = Integer.parseInt(n);
				new Thread( new Runnable() {
					public void run() {
						instructions = makeMasterString(gen);
					}
				}).start();

				String Xstate = JOptionPane.showInputDialog("What is the turtle's initial x state? ( x = [-1.0, 1.0] )");
				double x = Double.parseDouble(Xstate);
				String Ystate = JOptionPane.showInputDialog("What is the turtle's initial y state? ( y = [-1.0, 1.0] )");
				double y = Double.parseDouble(Ystate);
				String Thetastate = JOptionPane.showInputDialog("What is the turtle's initial bearing? ( Theta = 0 :=> )");
				int theta = Integer.parseInt(Thetastate);
				String baseLeng = JOptionPane.showInputDialog("What is the turtle's base segment length? ( 1.0 := 1/2 image height )");
				double base = Double.parseDouble(baseLeng);

				img = makeFractal( instructions, gen, x, y, theta, base );	//make image
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {						// EDT display image
						displayBufferedImage(img);
					}
				});
			}
		}).start();
	}
	// =====================================================================
	// === save()
	private void save(){
		new Thread( new Runnable() {
			public void run() {
				File outputFile = saveFile();
				try
				{
					javax.imageio.ImageIO.write( img, "png", outputFile );
				}
				catch ( IOException e )
				{
					JOptionPane.showMessageDialog( ImageFrame.this,
							"Error saving file",
							"oops!",
							JOptionPane.ERROR_MESSAGE );
				}
			}
		}).start();
	}
	//===================================================================
	// === Display BufferedImage
	private void displayBufferedImage(BufferedImage image){		//*** run on EDT
		setContentPane( new JLabel( new ImageIcon(image)));
		validate();
	}
	//================================================================
	// === makeImage() - to be replaced later with grow()
	private BufferedImage makeImage(int width, int height, int color1, int color2){		//*** run on worker 
		BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
		colorb = color1;
		colorf = color2;
		modX = width / 2;
		modY = height / 2;

		return image;
	}
	//================================================================
	// === makeMasterString()
	private String makeMasterString(int n){
		String input = omega;
		for ( int i = 0; i < n; i++ ){
			StringBuilder master = new StringBuilder("");
			for ( int k = 0; k < input.length(); k++){
				int j = 0;
				while( rules[j] != null ){
					char key = rules[j].charAt(0);
					if ( input.charAt(k) != key && j == 0)
						master.append(input.charAt(k));
					else if (key == input.charAt(k)){
						if ( j != 0)
							master.deleteCharAt(master.length() - 1);
						master.append(rules[j].substring(4));
					}
					j++;
				}
			}
			input = master.toString();
		}
		return input;
	}
	//================================================================
	// === makeFractal()
	private BufferedImage makeFractal( String instruct, int n, double x, double y, int theta, double base ){		//*** run on worker 
		Graphics2D g2d = img.createGraphics();

		//background
		g2d.setColor(new Color(colorb));
		g2d.fillRect( 0, 0, img.getHeight(), img.getHeight());
		//foreground
		g2d.setColor(new Color(colorf));

		RenderingHints hint = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHints( hint );

		x = (x+1) * modX;
		y = img.getHeight() - (y+1) * modY;
		double x2;
		double y2;
		double R = (base / Math.pow(scale, n))*0.5*img.getHeight();

		int index = 0;

		while( index != instruct.length() ){
			if( instruct.charAt(index) == def[0] || instruct.charAt(index) == def[1] ||
					instruct.charAt(index) == def[2] || instruct.charAt(index) == def[3] ||
					instruct.charAt(index) == def[4] || instruct.charAt(index) == def[5] ||
					instruct.charAt(index) == def[6] || instruct.charAt(index) == def[7]){
				if( instruct.charAt(index) != def[6] && instruct.charAt(index) != def[7]){
					x2 = x + R * Math.cos(Math.toRadians(theta));
					y2 = y - R * Math.sin(Math.toRadians(theta));

					if( instruct.charAt(index) == def[0] || instruct.charAt(index) == def[2] || instruct.charAt(index) == def[4])
						g2d.draw(new Line2D.Double( x, y, x2, y2));
					x = x2;
					y = y2;
				}
				else if ( instruct.charAt(index) == def[7] )
					theta -= delta;
				else
					theta += delta;
			}
			else if ( instruct.charAt(index) == '[' )
				stack.push(new LState(x, y, theta));
			else if ( instruct.charAt(index) == ']' ){
				x = stack.peek().x;
				y = stack.peek().y;
				theta = stack.peek().theta;
				stack.pop();
			}
			index++;
		}
		g2d.dispose();
		return img;
	}
	// -------------------------------------------------------------
	// ---- open file selected
	private File getFile(){
		File file = null;
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		return file;
	}
	// ---- save file dialog
	private File saveFile(){
		File file = null;
		if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		return file;
	}

	// ###############################################################
	public class LState{
		protected double x;
		protected double y;
		protected int theta;
		public LState(double x, double y, int theta){
			this.x = x;
			this.y = y;
			this.theta = theta;
		}
	}
}


