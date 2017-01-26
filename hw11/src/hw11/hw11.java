package hw11;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Random;

//#########################################################
public class hw11 {
	static private final int WIDTH = 800;
	static private final int HEIGHT = 800;
	protected static final JFrame frame = new JFrame();
	static private BufferedImage petriDish = new BufferedImage( 800, 800, BufferedImage.TYPE_INT_ARGB);
	final static CellWorld agar = new hw11().new CellWorld(petriDish);
	private static JFileChooser chooser = new JFileChooser();
	static boolean active = false;
	private final static int millisecond = 250;
	private static Timer timer;
	public static cell[][] colony = new cell[100][100];

	public static void main(String[] args) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run (){
				createAndShowGUI();
			}
		});
	}
	public static void createAndShowGUI(){
		frame.setTitle( "CAP 3027 2016 - HW11 - Jennifer Cremer" );
		frame.setSize( WIDTH, HEIGHT );

		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add( new hw11().new CellWorld( petriDish), BorderLayout.CENTER);
		frame.validate();
		frame.setVisible( true );

		// ----- setup file chooser dialog
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));

		//---add menu
		addMenu(frame);

		//timer
		timer = new Timer( millisecond, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//turn off timer to draw frame
				timer.stop();
				//display next
				CellLives();
				frame.repaint();
				//restart timer
				timer.restart();
			}
		});

		final JButton button = new JButton( "Run" );
		button.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ){
				new Thread(new Runnable(){
					public void run(){
						active = true;
						if( button.getText().equals("Run") ){
							button.setText( "Pause" );
							timer.start();
						}
						else{
							button.setText( "Run" );
							timer.stop();
							active = false;
						}
					}
				}).start();
			}
		});
		frame.getContentPane().add( button, BorderLayout.SOUTH );
	}
	private static void addMenu(JFrame frame){
		//---------------------------------------------------
		//setup frame's menu bar
		// === File Menu
		JMenu menu = new JMenu("File");

		// --- Random population
		JMenuItem item = new JMenuItem("Randomly populated world");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				randomWorld();
			}
		});
		menu.add(item);

		// --- empty world
		item = new JMenuItem("Empty world");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				emptyWorld();
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
		frame.setJMenuBar(menuBar);
	}
	//===================================================================
	// ---- RandomWorld
	private static void randomWorld(){
		new Thread( new Runnable() {
			public void run() {
				Random rand = new Random();
				double probability = Double.parseDouble(new String 
						( JOptionPane.showInputDialog(
								"What is the probability of a random cell being alive? [0.0, 1.0]")));
				for( int i = 0; i < 100; i++ ){
					for( int j = 0; j < 100; j++ ){
						double life = rand.nextDouble();
						colony[i][j] = new cell();
						if ( life <= probability )
							agar.paintCell(agar.GREEN, i, j);
						else
							agar.paintCell(agar.BLACK, i, j);
					}
				}
				SwingUtilities.invokeLater( new Runnable() {
					public void run(){
						frame.repaint();
						agar.repaint();
					}
				});
			}
		}).start();
	}
	//===================================================================
	// ---- emptyWorld
	private static void emptyWorld(){
		new Thread( new Runnable() {
			public void run() {
				for( int i = 0; i < 100; i++ ){
					for( int j = 0; j < 100; j++ ){
						colony[i][j] = new cell();
						agar.paintCell(agar.BLACK, i, j);
					}
				}
				SwingUtilities.invokeLater( new Runnable() {
					public void run(){
						frame.repaint();
						agar.repaint();
					}
				});
			}
		}).start();
	}
	// =====================================================================
	// === CellLives()
	private static void CellLives(){
		cell[][] curr = new cell[100][100];
		for(int x = 0; x < 100; x++){
			for (int y = 0; y < 100; y++){
				curr[x][y] = new cell();
				curr[x][y].setColor(colony[x][y].getColor());
			}
		}
		for (int i = 0; i < 100; i++){
			for (int j = 0; j < 100; j++){
				int count = 0;
				int a = i - 1, b = i + 1, c = j - 1, d = j + 1;
				if(b > 99)
					b = 0;
				if(a < 0)
					a = 99;
				if(d > 99)
					d = 0;
				if(c < 0)
					c = 99;
				
				if(!curr[a][c].isDead())
					count++;
				if(!curr[a][j].isDead())
					count++;
				if(!curr[a][d].isDead())
					count++;
				if(!curr[i][c].isDead())
					count++;
				if(!curr[i][d].isDead())
					count++;
				if(!curr[b][c].isDead())
					count++;
				if(!curr[b][j].isDead())
					count++;
				if(!curr[b][d].isDead())
					count++;

				if (count < 2 || count > 3){
					if(curr[i][j].getColor() == agar.RED)
						agar.paintCell(agar.BLACK, i, j);
					else if (curr[i][j].getColor() == agar.BLACK)
						agar.paintCell(agar.BLACK, i, j);
					else
						agar.paintCell(agar.RED, i, j);
				}
				else if (count == 3){
					if(curr[i][j].getColor() == agar.GREEN){
						agar.paintCell(agar.BLUE, i, j);
					}
					else if (curr[i][j].getColor() == agar.BLUE){
						agar.paintCell(agar.BLUE, i, j);
					}
					else
						agar.paintCell(agar.GREEN, i, j);
				}
				else if (count == 2){
					if(curr[i][j].getColor() == agar.GREEN){
						agar.paintCell(agar.BLUE, i, j);
					}
					else if(curr[i][j].getColor() == agar.RED){
						agar.paintCell(agar.BLACK, i, j);
					}
					else{
						agar.paintCell(curr[i][j].getColor(), i, j);
					}
				}
			}
		}
	}
	// =====================================================================
	// === save()
	private static void save(){
		new Thread( new Runnable() {
			public void run() {
				File outputFile = saveFile();
				try
				{
					javax.imageio.ImageIO.write( petriDish, "png", outputFile );
				}
				catch ( IOException e )
				{
					JOptionPane.showMessageDialog( frame,
							"Error saving file",
							"oops!",
							JOptionPane.ERROR_MESSAGE );
				}
			}
		}).start();
	}
	//===================================================================
	// ---- save file dialog
	private static File saveFile(){
		File file = null;
		if(chooser.showSaveDialog( frame ) == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		return file;
	}
	//######################################################################
	class CellWorld extends JPanel{
		final int BLACK = 0xFF000000;
		final int RED = 0xFFFF0000;
		final int GREEN = 0xFF00FF00;
		final int BLUE = 0xFF0000FF;

		BufferedImage image;
		private Graphics2D g2d;

		private boolean isARGBColor( Point p, int argb ){
			return (image.getRGB( p.x, p.y ) == argb );
		}
		
		void paintCell(int color, int x, int y){
			g2d.setColor(new Color(color));
			g2d.fillRect(x*8, y*8, 8, 8);
			colony[x][y].setColor(color);
			repaint();
		}

		void drawCell( Point point, int color ){
			int i = (point.x >> 3) << 3;
			int j = (point.y >> 3) << 3;

			g2d.setColor(new Color(color));
			g2d.fillRect(i, j, 8, 8);

			colony[i/8][j/8].setColor(color);
			repaint();
		}

		public CellWorld( BufferedImage image ){
			this.image = image;
			g2d = image.createGraphics();

			if (hw11.active == false){
				addMouseListener( new MouseAdapter(){
					public void mousePressed( MouseEvent e ){
						Point point = e.getPoint();
						if( isARGBColor( point, BLACK ) || isARGBColor( point, RED ) )
							drawCell( point, GREEN );
						else if( isARGBColor( point, GREEN ) || isARGBColor( point, BLUE ) )
							drawCell( point, RED );
					}
					public void mouseClicked( MouseEvent e){}
				});

				addMouseMotionListener( new MouseMotionListener(){
					public void mouseMoved(MouseEvent e){
						//set mouse cursor to cross hair if inside cell
						Point point = e.getPoint();
						if( isARGBColor( point, RED ) || 
								isARGBColor( point, GREEN ) || isARGBColor( point, BLUE ) )
							setCursor( Cursor.getDefaultCursor() );
						else
							setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
					}
					public void mouseDragged(MouseEvent e){}
				});
			}
		}
		public void paintComponent( Graphics g ){
			super.paintComponent(g);
			g.drawImage( image, 0, 0, null );
		}
	}
}