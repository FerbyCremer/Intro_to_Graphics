
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.image.*;
import java.io.*;

import javax.swing.*;



//#############################################################
class AreaSelectPanel extends JPanel{
	static private final Color OUTLINE_COLOR = Color.BLACK;

	//panel size
	private final int WIDTH, MAX_X;
	private final int HEIGHT, MAX_Y;

	//image displayed on panel
	private BufferedImage image;
	private Graphics2D g2d;

	//current selection
	private int x = -1;
	private int y = -1;
	private int w = 0;
	private int h = 0;

	// --------------------------------------------------------
	// constructor

	public AreaSelectPanel( BufferedImage image ){
		this.image = image;
		g2d = image.createGraphics();
		g2d.setXORMode( OUTLINE_COLOR );

		//define panel characteristics
		WIDTH = image.getWidth();
		HEIGHT = image.getHeight();
		Dimension size = new Dimension( WIDTH, HEIGHT );
		setMinimumSize( size );
		setMaximumSize( size );
		setPreferredSize( size );

		MAX_X = WIDTH - 1;
		MAX_Y = HEIGHT - 1;

		addMouseListener( new MouseAdapter(){
			public void mousePressed( MouseEvent event ){
				clearSelection( event.getPoint() );
			}
		});

		addMouseMotionListener( new MouseMotionAdapter(){
			public void mouseDragged( MouseEvent event ){
				updateSelection( event.getPoint() );
			}
		});
	}
	// --------------------------------------------------------
	// accessors - get points defining the area selected

	Point2D.Double getUpperLeft(){
		return getUpperLeft( new Point2D.Double() );
	}

	Point2D.Double getUpperLeft( Point2D.Double p ){
		if( w < 0 )
			if( h < 0 )
				p.setLocation( (x+w)/((double) MAX_X), (y+h)/((double) MAX_Y) );
			else
				p.setLocation( (x+w)/((double) MAX_X), y/((double) MAX_Y) );
		else if ( h < 0 )
			p.setLocation( x/((double) MAX_X), (y+h)/((double) MAX_Y) );
		else
			p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );

		return p;
	}

	Point2D.Double getLowerRight(){
		return getLowerRight( new Point2D.Double() );
	}

	Point2D.Double getLowerRight( Point2D.Double p ){
		if( w < 0 )
			if( h < 0 )
				p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );
			else
				p.setLocation( x/((double) MAX_X), (y+h)/((double) MAX_Y) );
		else if ( h < 0 )
			p.setLocation( (x+w)/((double) MAX_X), y/((double) MAX_Y) );
		else
			p.setLocation( (x+w)/((double) MAX_X), (y+h)/((double) MAX_Y) );

		return p;
	}

	// --------------------------------------------------------
	// change background image

	public void setImage( BufferedImage src ){
		g2d.setPaintMode();
		g2d.drawImage(src, 
				0, 0, MAX_X, MAX_Y,
				0, 0, (src.getWidth() - 1), (src.getHeight() - 1), 
				OUTLINE_COLOR, null);
		g2d.setXORMode( OUTLINE_COLOR );

		x = -1;
		y = -1;
		w = 0;
		h = 0;
		repaint();
	}

	// --------------------------------------------------------
	// behaviors

	public void paintComponent( Graphics g ){
		super.paintComponent(g);
		g.drawImage( image, 0, 0, null );
	}

	private void clearSelection( Point p ){
		//erase old selection
		drawSelection();

		//begin new selection
		x = (p.x < 0) ? 0 : ( (p.x < WIDTH) ? p.x : MAX_X );
		y = (p.y < 0) ? 0 : ( (p.y < HEIGHT) ? p.y : MAX_Y );
		w = 0;
		h = 0;

		drawSelection();
	}

	private void updateSelection( Point p ){
		//erase old selection
		drawSelection();

		//begin new selection
		int px = (p.x < 0) ? 0 : ( (p.x < WIDTH) ? p.x : MAX_X );
		int py = (p.y < 0) ? 0 : ( (p.y < HEIGHT) ? p.y : MAX_Y );
		w = px - x;
		h = py - y;

		drawSelection();
	}

	private void drawSelection(){
		if( w < 0 )
			if( h < 0 )
				g2d.drawRect( (x+w), (y+h), -w, -h);
			else
				g2d.drawRect( (x+w), y, -w, h);
		else if( h < 0 )
			g2d.drawRect( x, (y+h), w, -h);
		else
			g2d.drawRect( x, y, w, h);

		repaint();
	}
}
class hw09 {
	private static final int WIDTH = 600;
	private static final int HEIGHT = 450;
	static JFrame frame = new JFrame();
	static BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	final static AreaSelectPanel panel = new AreaSelectPanel(img);
	private static JFileChooser chooser = new JFileChooser();
	static boolean brot = false;
	static boolean jul = false;
	
	public static void main(String[] args) {			//*** run on main thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {			//*** run on the EDT
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setTitle("CAP 3027 2016 - HW09 - Jennifer Cremer");
		frame.setSize(600, 450);
		frame.getContentPane().add( panel, BorderLayout.CENTER );
		addMenu(frame);

		// ----- setup file chooser dialog
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));

		final JButton button = new JButton( "Zoom" );
		button.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ){
				new Thread(new Runnable(){
					public void run(){
						zoom(panel.getUpperLeft(), panel.getLowerRight());
					
			//	button.setText( panel.getUpperLeft() + " to " + panel.getLowerRight() );
					}
				}).start();
			}
		});

		
		frame.getContentPane().add( button, BorderLayout.SOUTH );

		//frame.pack();
		frame.setVisible(true);
	}
	private static void addMenu(JFrame frame){
		//---------------------------------------------------
		//setup frame's menu bar

		// === File Menu
		JMenu menu = new JMenu("File");
		// --- Mandelbrot set
		JMenuItem item = new JMenuItem("Mandelbrot");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				brot = true;
				jul = false;
				mandel( 0.0, 1.0, 0.0, 1.0 );
			}
		});
		menu.add(item);
		// --- Julia set
		item = new JMenuItem("Julia");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				brot = false;
				jul = true;
				julia( 0.0, 1.0, 0.0, 1.0 );
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

	//==================================================================
	// === mandel() 
	static double xStart = -2;
	static double yStart = 1.5;
	static double dx = 4;
	static double dy = 3;
	private static void mandel( final double realNeg, final double realPos, final double imagPos, final double imagNeg ){
		
		new Thread( new Runnable() {
			public void run(){
				double rneg = realNeg;
				double rpos = realPos;
				double ipos = imagPos;
				double ineg = imagNeg;
				int[] colors = new int[100];
				final int green = 0xFF00FF00;
				final int yellow = 0xFFFFFF00;
				final int purple = 0xFFFF00FF;
				final int cyan = 0xFF0077FF;
				for (int a = 0; a < 100; a++){
					colorInterpolation gc = new colorInterpolation(green, cyan, 33);
					gc.extract();
					colorInterpolation cp = new colorInterpolation(cyan, purple, 33);
					cp.extract();
					colorInterpolation py = new colorInterpolation(purple, yellow, 33);
					py.extract();
					if(a < 32)
						colors[a] = gc.colorInterp(a);
					else if (a >=32 && a < 66)
						colors[a] = cp.colorInterp(a);
					else
						colors[a] = py.colorInterp(a);
				}
				int tmax = 100;
				double ua0 = xStart + dx*rneg;
				double ub0 = yStart + dy*ipos;
				double deltaA = ((xStart + dx*rpos) - ua0 )/599.0;
				double deltaB = ((yStart + dy*ineg) - ub0 )/449.0;
				dx = deltaA*599.0;
				dy = deltaB*449.0;
				yStart = ub0;
				for(int i = 0; i < 600; i++){
					ub0 = yStart;
					for(int j = 0; j < 450; j++){
						//Point2D.Double u = new Point.Double( ua0, ub0);
						double za0 = 0;
						double zb0 = 0;
						double za = 0;
						double zb = 0;
						//Point2D.Double z = new Point.Double( za0, zb0);
						int t = 0;
						while ( t != tmax ){
							za0 = (za*za - zb*zb) + ua0;
							zb0 = (za*zb + zb*za) + ub0;
							double mag = za0*za0+zb0*zb0;
							//System.out.println(mag);
							if(mag > 4)
								break;
							else
								t++;
							za = za0;
							zb = zb0;
							//System.out.println("while loop");
						}
						if(t<tmax){
							//System.out.println("not in set\n"+ (int) (300 + (ua0)*modA) + " , " + (int) (225+(-ub0)*modB));
							img.setRGB( i,j, colors[t]);
						}
						else{
							//System.out.println("in set " + (int) (300+(ua0)*modA) + " , " + (int) (225+(-ub0)*modB));
							img.setRGB(i, j, 0xFF000000);
						}

						ub0 -= deltaB;
						//System.out.println("j for loop");
					}
					ua0 += deltaA;
					//System.out.println("i for loop");

				}
				SwingUtilities.invokeLater( new Runnable() {
					public void run(){
						frame.pack();
						frame.repaint();
						panel.repaint();
					}
				});
			}
		}).start();
	}

	//==================================================================
	// === julia()
	static boolean ran = false;
	static double ua0;
	static double ub0;
	static double xStartJ = -2;
	static double yStartJ = 1.5;
	static double dxJ = 4;
	static double dyJ = 3;
	private static void julia( double realNeg, double realPos, double imagPos, double imagNeg ){
		final double rneg = realNeg;
		final double rpos = realPos;
		final double ipos = imagPos;
		final double ineg = imagNeg;
		new Thread( new Runnable() {
			public void run(){
				int[] colors = new int[100];
				final int green = 0xFF00FF00;
				final int yellow = 0xFFFFFF00;
				final int purple = 0xFFFF00FF;
				final int cyan = 0xFF0055FF;
				for (int a = 0; a < 100; a++){
					colorInterpolation gc = new colorInterpolation(green, cyan, 34);
					gc.extract();
					colorInterpolation cp = new colorInterpolation(cyan, purple, 34);
					cp.extract();
					colorInterpolation py = new colorInterpolation(purple, yellow, 34);
					py.extract();
					if(a < 33)
						colors[a] = gc.colorInterp(a);
					else if (a >=33 && a < 66)
						colors[a] = cp.colorInterp(a);
					else
						colors[a] = py.colorInterp(a);
				}
				
				if ( ran == false ){
				String ua = JOptionPane.showInputDialog("What is the a value of u?");
				ua0 = Double.parseDouble(ua);
				String ub = JOptionPane.showInputDialog("What is the b value of u?");
				ub0 = Double.parseDouble(ub);
				ran = true;
				}
				//final BufferedImage fractal2 = new BufferedImage(600,450, BufferedImage.TYPE_INT_ARGB);
				int tmax = 100;
				double za0 = xStartJ + dxJ*rneg;
				double zb0 = yStartJ + dyJ*ipos;
				double deltaA = ((xStartJ + dxJ*rpos) - za0 )/599.0;
				double deltaB = ((yStartJ + dyJ*ineg) - zb0 )/449.0;
				dxJ = deltaA*599.0;
				dyJ = deltaB*449.0;
				yStartJ = zb0;
				for(int i = 0; i < 600; i++){
					zb0 = yStartJ;
					for(int j = 0; j < 450; j++){
						//Point2D.Double u = new Point.Double( ua0, ub0);
						//double za0 = 0;
						//double zb0 = 0;
						double za = za0;
						double zb = zb0;
						double za1 = za0;
						double zb1 = zb0;
						//Point2D.Double z = new Point.Double( za0, zb0);
						int t = 0;
						while ( t != tmax ){
							za = (za1*za1 - zb1*zb1) + ua0;
							zb = (za1*zb1 + zb1*za1) + ub0;
							double mag = za*za+zb*zb;
							//System.out.println(mag);
							if(mag > 4)
								break;
							else
								t++;
							za1 = za;
							zb1 = zb;
							//System.out.println("while loop");
						}
						if(t<tmax){
							//System.out.println("not in set\n"+ (int) (300 + (za0)*modA) + " , " + (int) (225+(-zb0)*modB));
							img.setRGB(i,j, colors[t]);
						}
						else{
							//System.out.println("in set " + (int) (300+(za0)*modA) + " , " + (int) (225+(-zb0)*modB));
							img.setRGB(i, j, 0xFF000000);
						}

						zb0 -= deltaB;
						//System.out.println("j for loop");
					}
					za0 += deltaA;
					//System.out.println("i for loop");

				}
				SwingUtilities.invokeLater( new Runnable() {
					public void run(){
						frame.pack();
						frame.repaint();
						panel.repaint();
						//displayBufferedImage( img );
					}
				});
			}
		}).start();
	}
	// =====================================================================
	// === save()
	private static void save(){
		new Thread( new Runnable() {
			public void run() {
				File outputFile = saveFile();
				try
			{
				javax.imageio.ImageIO.write( img, "png", outputFile );
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
		if(chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		return file;
	}
	
	private static void zoom(Point2D.Double ULeft, Point2D.Double LRight){
		if ( brot == true )
			mandel(ULeft.getX(), LRight.getX(), ULeft.getY(), LRight.getY());
		else if (jul == true)
			julia(ULeft.getX(), LRight.getX(), ULeft.getY(), LRight.getY());
	}
}

