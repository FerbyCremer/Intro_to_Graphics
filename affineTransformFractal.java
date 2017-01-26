//affineTransformFractal.java
// - DOES STUFFS
//
// Jennifer Cremer

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.SwingUtilities;						//***


public class affineTransformFractal {
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
	public AffineTransform[] transform = new AffineTransform[100];
	protected BufferedImage img;
	int colorb;
	int colorf;
	//=========================================================
	//constructor
	public ImageFrame(int width, int height){
		setTitle("CAP 3027 2016 - HW07 - Jennifer Cremer");
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
		// --- Load fractal description
		JMenuItem item = new JMenuItem("Load IFS Description");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				loadDescrip();
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
		// --- Display IFS
		item = new JMenuItem("Display IFS");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				display();
			}
		});
		menu.add(item);
		// --- Display IFS
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
	// === loadDescrip() 
	private void loadDescrip(){
		new Thread( new Runnable() {
			public void run() {
				File file = getFile();
				ArrayList transforms = new ArrayList();

				String line = "";
				BufferedReader lines;
				try {
					lines = new BufferedReader(new FileReader(file));

					while((line = lines.readLine()) != null){

						String tran = "";

						tran = line;
						Scanner s = new Scanner(tran);
						int i = 0;
						double[] comp = new double[7];
							comp[6] = 0;
						
						while(s.hasNextDouble()){
							comp[i] = s.nextDouble();
							i++;
						}
						
						AffineTransform at = new AffineTransform(comp[0], comp[2], comp[1], comp[3], comp[4], comp[5]);
						
						int prob = 0;
						if(comp[6] != 0){
							prob = (int)(comp[6]*100);
							for(int p = 0; p < prob; p++){
								transforms.add(at);
							}
						}
						else{
							transforms.add(at);
						}
						s.close();
					}

					if(transforms.size() != 100){
						double sum = 0;
						//double det = 0;
						for(int i = 0; i < transforms.size(); i++){
							double det = Math.abs(((AffineTransform) transforms.get(i)).getDeterminant());
							sum += det;
							System.out.println("sum: " + sum);
						}
						int index = 0;
						for(int i = 0; i < transforms.size(); i++){
							double det = Math.abs(((AffineTransform) transforms.get(i)).getDeterminant());
							System.out.println("det: " + det);
							double weight = ( det / sum)*100;
							if(weight != 0.0 && (weight*10) % 10 < 8 
											 && (weight*10) % 10 > 5)
							{
								weight =  (int)Math.ceil(weight);
								}
							else if(weight == 0.0){
								weight = (int)1.0;
								sum += 0.01;
								}
							else{
								weight =  (int)(weight);
								}
							for(int p = 0; p < (int)weight; p++){
								transform[index] = ((AffineTransform)transforms.get(i));
								index++;
							}
						}
					}
					else{
						transform = (AffineTransform[]) transforms.toArray(transform);
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
				String str = JOptionPane.showInputDialog("How large is the image?");
				int height = Integer.parseInt(str);
				int width = height;
				String colorB = JOptionPane.showInputDialog("What is the background color?");
				int color1 = (int) Long.parseLong(colorB.substring( 2, colorB.length() ), 16);
				String colorF = JOptionPane.showInputDialog("What is the Foreground color?");
				int color2 = (int) Long.parseLong(colorF.substring( 2, colorF.length() ), 16);

				img = makeImage(width, color1, color2);	//make image
			}
		}).start();
	}
	// =====================================================================
	// === display()
	private void display(){
		new Thread( new Runnable() {
			public void run() {
				String n = JOptionPane.showInputDialog("How many generations?");
				int gen = Integer.parseInt(n);

				img = makeFractal(colorb, colorf, gen);	//make image
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
	private BufferedImage makeImage(int width, int color1, int color2){		//*** run on worker 
		BufferedImage image = new BufferedImage( width, width, BufferedImage.TYPE_INT_ARGB);
		colorb = color1;
		colorf = color2;

		return image;
	}
	//================================================================
	// === makeFractal()
	private BufferedImage makeFractal(int color1, int color2, int gen){		//*** run on worker 
		Graphics2D g2d = img.createGraphics();

		//background
		g2d.setColor(new Color(color1));
		g2d.fillRect( 0, 0, img.getHeight(), img.getHeight());
		//foreground
		g2d.setColor(new Color(color2));


		Random rand = new Random();

		RenderingHints hint = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHints( hint );

		double x = rand.nextDouble();
		double y = rand.nextDouble();
		Point2D p = new Point.Double( x, y );
		for(int threshold = 0; threshold < 200; threshold++){
			int i = rand.nextInt(100);
			transform[i].transform(p, p);
		}
		//Rectangle piece = new Rectangle(new Dimension(1,1));
		Rectangle2D piece = new Rectangle2D.Double(p.getX(),p.getY(),1,1);
		for(int b = 0; b < gen; b++){
			piece.setRect((p.getX()*(img.getHeight())), (img.getHeight() - p.getY()*(img.getHeight())), 1, 1);
			g2d.draw(piece);
			int i = rand.nextInt(100);
			transform[i].transform(p, p);
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
}


