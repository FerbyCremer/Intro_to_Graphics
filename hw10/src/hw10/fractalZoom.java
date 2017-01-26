package hw10;
import java.awt.*;
import java.awt.event.*;
import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
//#############################################################
class fractalZoom {
	private static final int WIDTH = 650;
	private static final int HEIGHT = 470;
	static JFrame frame = new JFrame();
	static BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	private final static int Milliseconds = 100;
	private static Timer timerZoomIn;
	private static Timer timerZoomOut;
	static Point point;
	private static JFileChooser chooser = new JFileChooser();
	public static boolean brot = false;
	public static boolean jul = false;
	static double xStart = -2;
	static double yStart = 1.5;
	static double centerX = 0;
	static double centerY = 0;
	static double dx = 4;
	static double dy = 3;
	static JLabel imageBox;
	public static void main(String[] args) {			//*** run on main thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	private static void createAndShowGUI() {			//*** run on the EDT
		ImageIcon icon = new ImageIcon(img);
		imageBox = new JLabel();
		imageBox.setIcon(icon);
		frame.setTitle("CAP 3027 2016 - HW10 - Jennifer Cremer");
		frame.setSize(600, 450);
		addMenu();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(imageBox, BorderLayout.CENTER);
		frame.getContentPane().add( new JLabel("Click and hold to zoom (LMB to zoom in/RMB to zoom out)"), BorderLayout.SOUTH );
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		frame.pack();
		frame.setVisible(true);
		imageBox.addMouseListener( new MouseAdapter(){
			public void mousePressed( MouseEvent event ){
				if(SwingUtilities.isLeftMouseButton(event)){
					point = event.getPoint();
					timerZoomIn.start();
				}
				else if (SwingUtilities.isRightMouseButton(event)){
					point = event.getPoint();
					timerZoomOut.start();
				}
			}
			public void mouseReleased ( MouseEvent event ){
				if(SwingUtilities.isLeftMouseButton(event))
					timerZoomIn.stop();
				else if (SwingUtilities.isRightMouseButton(event))
					timerZoomOut.stop();
			}
		});
		// create timer to display animation
		timerZoomIn = new Timer( Milliseconds, new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				//turn off timer so frame is drawn
				//next event fires
				timerZoomIn.stop();
				//display next frame
				centerX = (point.getX() - (img.getWidth()/2));
				centerY = (point.getY() - (img.getHeight()/2));
				if ( brot == true)
					mandel( 0.95 );
				else if ( jul == true)
					julia( 0.95 );
				frame.setIconImage(img);
				frame.repaint();
				//restart timer (draw next in __ milliseconds)
				timerZoomIn.restart();
			}
		});
		timerZoomOut = new Timer( Milliseconds, new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				//turn off timer so frame is drawn
				//next event fires
				timerZoomOut.stop();
				//display next frame
				centerX = (point.getX() - (img.getWidth()/2));
				centerY = (point.getY() - (img.getHeight()/2));
				if ( brot == true)
					mandel( 1.05 );
				else if ( jul == true)
					julia( 1.05);
				frame.setIconImage(img);
				frame.repaint();
				timerZoomOut.restart();
			}
		});
	}
	//=========================================================
	//constructor
	private static void addMenu(){
		//setup frame's menu bar
		// === File Menu
		JMenu menu = new JMenu("File");
		// --- Mandelbrot set
		JMenuItem item = new JMenuItem("Mandelbrot");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				brot = true;
				jul = false;
				mandel( 1.0 );
			}
		});
		menu.add(item);
		// --- Julia set
		item = new JMenuItem("Julia");
		item.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ){		//*** run on EDT
				brot = false;
				jul = true;
				julia( 1.0 );
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
	static void mandel( final double percent ){
		new Thread( new Runnable() {
			public void run(){
				double per = percent;
				int[] colors = new int[100];
				final int green = 0xFF00FF00;
				final int yellow = 0xFFFFFF00;
				final int purple = 0xFFFF00FF;
				final int cyan = 0xFF0077FF;
				final BufferedImage image = img;
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
				centerX = centerX/img.getWidth();
				centerY = centerY/img.getHeight();
				double ua0 = (xStart + (dx*centerX))*per;
				double ub0 = (yStart - (dy*centerY))*per;
				double deltaA = (((xStart+dx + (dx*centerX))*per) - ua0 )/599.0;
				double deltaB = (((yStart+dy + (dy*centerY))*per) - ub0 )/449.0;
				dx = deltaA*599.0;
				dy = deltaB*449.0;
				yStart = ub0;
				for(int i = 0; i < 600; i++){
					ub0 = yStart;
					for(int j = 0; j < 450; j++){
						double za0 = 0;
						double zb0 = 0;
						double za = 0;
						double zb = 0;
						int t = 0;
						while ( t != tmax ){
							za0 = (za*za - zb*zb) + ua0;
							zb0 = (za*zb + zb*za) + ub0;
							double mag = za0*za0+zb0*zb0;
							if(mag > 4)
								break;
							else
								t++;
							za = za0;
							zb = zb0;
						}
						if(t<tmax)
							image.setRGB( i,j, colors[t]);
						else
							image.setRGB(i, j, 0xFF000000);
						ub0 -= deltaB;
					}
					ua0 += deltaA;
				}
				SwingUtilities.invokeLater( new Runnable() {
					public void run(){
						frame.pack();
						frame.repaint();
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
	static void julia( final double percent ){
		final double per = percent;
		new Thread( new Runnable() {
			public void run(){
				int[] colors = new int[100];
				final int green = 0xFF00FF00;
				final int yellow = 0xFFFFFF00;
				final int purple = 0xFFFF00FF;
				final int cyan = 0xFF0055FF;
				final BufferedImage image = img;
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
				int tmax = 100;
				centerX = centerX/img.getWidth();
				centerY = centerY/img.getHeight();
				double za0 = (xStartJ + (dxJ*centerX))*per;
				double zb0 = (yStartJ - (dyJ*centerY))*per;
				double deltaA = (((xStartJ+dxJ + (dxJ*centerX))*per) - za0 )/599.0;
				double deltaB = (((yStartJ+dyJ + (dyJ*centerY))*per) - zb0 )/449.0;
				dxJ = deltaA*599.0;
				dyJ = deltaB*449.0;
				yStartJ = zb0;
				for(int i = 0; i < 600; i++){
					zb0 = yStartJ;
					for(int j = 0; j < 450; j++){
						double za = za0;
						double zb = zb0;
						double za1 = za0;
						double zb1 = zb0;
						int t = 0;
						while ( t != tmax ){
							za = (za1*za1 - zb1*zb1) + ua0;
							zb = (za1*zb1 + zb1*za1) + ub0;
							double mag = za*za+zb*zb;
							if(mag > 4)
								break;
							else
								t++;
							za1 = za;
							zb1 = zb;
						}
						if(t<tmax)
							image.setRGB(i,j, colors[t]);
						else
							image.setRGB(i, j, 0xFF000000);
						zb0 -= deltaB;
					}
					za0 += deltaA;
				}
				SwingUtilities.invokeLater( new Runnable() {
					public void run(){
						frame.pack();
						frame.repaint();
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
	
	public void paintComponent( Graphics g2d ){
		paintComponent(g2d);
		g2d.drawImage( img, 0, 0, null );
	}
}

