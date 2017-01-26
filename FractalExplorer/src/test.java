import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.Color;
import java.io.*;
import java.util.Vector;
class test{
	static BufferedImage canvas = new BufferedImage(600, 540, BufferedImage.TYPE_INT_ARGB);
	static JFrame frame = new JFrame();
	final static AreaSelectPanel panel = new AreaSelectPanel(canvas);
	static boolean mandelbrot = false;
	static boolean julia = false;


	public static void main( String[] args ){
		SwingUtilities.invokeLater( new Runnable() {
			public void run(){
				createAndShowGUI();
			}});
	}
	public static void createAndShowGUI(){
		//JFrame frame = new JFrame();
		addMenu(frame);
		frame.setTitle("CAP 3027 2016 - HW09 - Nathan Lively");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add( panel, BorderLayout.CENTER );
		frame.setSize(600,450);

		/* try{
	 final JFileChooser chooser = new JFileChooser();
	 File file = null;
		if(chooser.showOpenDialog(frame)== JFileChooser.APPROVE_OPTION){
		file = chooser.getSelectedFile();
		}
	 img = ImageIO.read( file);
 }catch (IOException e ) {}*/
		// final AreaSelectPanel panel = new AreaSelectPanel(canvas);

		final JButton button = new JButton( "Zoom" );
		button.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent event){ 
				new Thread(new Runnable(){
					public void run(){
						doZoom(panel.getUpperLeft(),panel.getLowerRight());
					}
				}).start();
				//button.setText("Selected " + panel.getUpperLeft() + " to " +panel.getLowerRight());
			}
		} );
		// frame.getContentPane().add( panel, BorderLayout.CENTER );
		frame.getContentPane().add( button, BorderLayout.SOUTH );
		// frame.pack();
		frame.setVisible( true );
	}

	private static void addMenu(JFrame frame){

		JMenu fileMenu = new JMenu("File");


		JMenuItem mandelbrot_Item = new JMenuItem("Mandelbrot");
		mandelbrot_Item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				new Thread(new Runnable(){
					public void run(){
						julia = false;
						mandelbrot = true;
						mandelbrot(0.0,1.0,1.0,0.0); // should be 0.0,0.0,1.0,1.0 , but I changed it for testing 0.25,0.25,.75,.75
					}
				}).start();
			}
		});
		fileMenu.add(mandelbrot_Item);

		JMenuItem julia_Item = new JMenuItem("Julia");
		julia_Item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){	
				new Thread(new Runnable(){
					public void run(){
						julia = true;
						mandelbrot = false;
						julia(0.0,1.0,1.0,0.0,true);
					}
				}).start();

			}
		});
		fileMenu.add(julia_Item);


		JMenuItem save_Item = new JMenuItem("Save Image");
		save_Item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				new Thread(new Runnable(){
					public void run(){
						save_L_Sys();
					}
				}).start();
			}
		});
		fileMenu.add(save_Item);


		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new
				ActionListener(){
			public void actionPerformed(ActionEvent event){
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);

		frame.setJMenuBar(menuBar);	
	}

	private static File saveFile(){
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		File file = null;
		if(chooser.showSaveDialog(frame)== JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		return file;
	}
	//save image to file
	private static void save_L_Sys(){
		try{
			File outputFile =saveFile();
			javax.imageio.ImageIO.write( canvas, "png", outputFile );
		}catch ( IOException e ){
			// JOptionPane.showMessageDialog( ImageFrame.this,"Error saving file","oops!",JOptionPane.ERROR_MESSAGE );
		}

	}

	private static void doZoom(java.awt.geom.Point2D.Double double1, java.awt.geom.Point2D.Double double2){
		if(mandelbrot==true){
			System.out.println(double1.getX()+","+double1.getY()+":::"+double2.getX()+","+double2.getY());
			mandelbrot(double1.getX(),double1.getY(),double2.getX(),double2.getY());
		}else if(julia==true){
			julia(double1.getX(),double1.getY(),double2.getX(),double2.getY(),false);
		}	
	}
	private static Vector<Color> getColorScheme(){
		Vector<Color> colors = new Vector<Color>();

		Color olive_Green = new Color(0xFF6B8E23);
		Color sky_Blue = new Color(0xFF7EC0EE);
		Color lavender_Purple = new Color(0xFFB378D3);
		Color rose_Pink = new Color(0xFFFFB6C1);

		colors.add(olive_Green);
		double delta_GB_red = (sky_Blue.getRed()-olive_Green.getRed())/20;
		double delta_GB_green= (sky_Blue.getGreen()-olive_Green.getGreen())/20;
		double delta_GB_blue= (sky_Blue.getBlue()-olive_Green.getGreen())/20;	 

		Color saveColor = olive_Green;
		for(int i=0;i<19;i++){
			saveColor = new Color((int) (saveColor.getRed()+delta_GB_red),(int) (saveColor.getGreen()+delta_GB_green),(int) (saveColor.getBlue()+delta_GB_blue));
			colors.add(saveColor);		 
		}
		//////////////////////////
		colors.add(sky_Blue);
		double delta_BP_red = (lavender_Purple.getRed()-sky_Blue.getRed())/30;
		double delta_BP_green= (lavender_Purple.getGreen()-sky_Blue.getGreen())/30;
		double delta_BP_blue= (lavender_Purple.getBlue()-sky_Blue.getBlue())/30;	 

		saveColor = sky_Blue;
		for(int i=0;i<29;i++){
			saveColor = new Color((int) (saveColor.getRed()+delta_BP_red),(int) (saveColor.getGreen()+delta_BP_green),(int) (saveColor.getBlue()+delta_BP_blue));
			colors.add(saveColor);		 
		}
		//////////////////////////
		colors.add(lavender_Purple);
		double delta_PP_red = (rose_Pink.getRed()-lavender_Purple.getRed())/49;
		double delta_PP_green= (rose_Pink.getGreen()-lavender_Purple.getGreen())/49;
		double delta_PP_blue= (rose_Pink.getBlue()-lavender_Purple.getBlue())/49;	 

		saveColor = sky_Blue;
		for(int i=0;i<48;i++){
			saveColor = new Color((int) (saveColor.getRed()+delta_PP_red),(int) (saveColor.getGreen()+delta_PP_green),(int) (saveColor.getBlue()+delta_PP_blue));
			colors.add(saveColor);		 
		}
		colors.add(rose_Pink);


		System.out.println("size of color vector:"+colors.size());
		return colors;


	}

	static double realBoundDefault = -2;
	static double imaginaryBoundDefault = -1.5;
	static double sizeRealMax = 4;
	static double sizeImaginaryMax =3;


	private static void mandelbrot(double realBounds1,double imaginaryBounds1,double realBounds2,double imaginaryBounds2){

		Vector<Color> colors = getColorScheme();
		//double realBoundDefault = -2;
		//double imaginaryBoundDefault = -1.5;

		//double sizeRealMax = 4;
		//double sizeImaginaryMax =3;

		double scaledRealStart = realBoundDefault+ sizeRealMax*(realBounds1);
		double scaledImaginaryStart = imaginaryBoundDefault+sizeImaginaryMax*(imaginaryBounds2);

		double scaledRealEnd = realBoundDefault+ sizeRealMax*(realBounds2);
		double scaledImaginaryEnd = imaginaryBoundDefault+sizeImaginaryMax*(imaginaryBounds1);


		realBoundDefault = scaledRealStart;
		imaginaryBoundDefault = scaledImaginaryStart;



		System.out.println("");
		System.out.println("min real:"+realBoundDefault);
		System.out.println("min imaginary"+imaginaryBoundDefault);
		System.out.println(realBounds1+","+imaginaryBounds1+":::"+realBounds2+","+imaginaryBounds2);
		System.out.println("real start:"+scaledRealStart);
		System.out.println("imaginary start"+scaledImaginaryStart);
		//now do mandelbrot
		double zeta_Real=0;
		double zeta_Imaginary = 0;
		double mu_Real = scaledRealStart;
		double mu_Imaginary = scaledImaginaryStart;

		double delta_Real=  (scaledRealEnd-scaledRealStart)/(double)canvas.getWidth();
		double delta_Imaginary= (scaledImaginaryEnd-scaledImaginaryStart)/(double)canvas.getHeight();

		sizeRealMax = Math.abs(scaledRealEnd-scaledRealStart);
		sizeImaginaryMax = Math.abs(scaledImaginaryEnd-scaledImaginaryStart);
		System.out.println("sizeRealMAX:"+sizeRealMax);
		System.out.println("sizeImaginaryMAX:"+sizeImaginaryMax);
		System.out.println("real delta:"+delta_Real);
		System.out.println("imaginary delta"+delta_Imaginary);
		int t;
		for(int i=0;i<canvas.getHeight();i++){
			mu_Real = realBoundDefault;
			for(int j=0;j<canvas.getWidth();j++){		
				zeta_Real = 0;
				zeta_Imaginary= 0;
				t=0;
				while(t!=100){
					//System.out.println("tCount:"+t);
					double zetaSave;
					//zeta_Real 
					zetaSave = ((zeta_Real*zeta_Real)-(zeta_Imaginary*zeta_Imaginary)) + mu_Real; //z = z^2 + mu
					zeta_Imaginary = (2*(zeta_Real*zeta_Imaginary))+ mu_Imaginary;
					zeta_Real = zetaSave;
					//System.out.println("real:"+zeta_Real);
					//System.out.println("imaginary:"+zeta_Imaginary);
					if((Math.pow(zeta_Real, 2)+Math.pow(zeta_Imaginary, 2))>4){
						//System.out.println("break");
						break;
					}else{
						t++;
					}
				}

				if(t<100){
					//System.out.println("DIVERGED");
					canvas.setRGB(j,(canvas.getHeight()-1)-i, colors.get(t).getRGB());//diverged
				}else{
					//System.out.println("IN SET");
					//System.exit(0);
					canvas.setRGB(j, (canvas.getHeight()-1)-i,0xFF000000);//in set
				}//(canvas.getHeight()-1)-i

				mu_Real+= delta_Real;		
			}

			mu_Imaginary+=delta_Imaginary;
		}
		System.out.println("real end:"+mu_Real);
		System.out.println("imaginary end"+mu_Imaginary);

		//final AreaSelectPanel panel = new AreaSelectPanel(canvas);
		SwingUtilities.invokeLater( new Runnable() {
			public void run(){
				// frame.getContentPane().add( panel, BorderLayout.CENTER );
				frame.pack();
				frame.repaint();
				panel.repaint();
			}});
	}


	static double realBoundDefaultJulia = -2;
	static double imaginaryBoundDefaultJulia = -1.5;
	static double sizeRealMaxJulia = 4;
	static double sizeImaginaryMaxJulia =3;
	static double mu_Real;
	static double mu_Imaginary;
	private static void julia(double realBounds1,double imaginaryBounds1,double realBounds2,double imaginaryBounds2, boolean check){

		Vector<Color> colors = getColorScheme();
		//double realBoundDefault = -2;
		//double imaginaryBoundDefault = -1.5;

		//double scaledRealStart = realBoundDefault*(realBounds2-realBounds1);  does this work better????
		//double scaledImaginaryStart = imaginaryBoundDefault*(imaginaryBounds2-imaginaryBounds1);

		//need to add if around inputs and also possibly resizing SizeMAx values

		double scaledRealStart = realBoundDefaultJulia+ sizeRealMaxJulia*(realBounds1);
		double scaledImaginaryStart = imaginaryBoundDefaultJulia+sizeImaginaryMaxJulia*(imaginaryBounds2);

		double scaledRealEnd = realBoundDefaultJulia+ sizeRealMaxJulia*(realBounds2);
		double scaledImaginaryEnd = imaginaryBoundDefaultJulia+sizeImaginaryMaxJulia*(imaginaryBounds1);


		realBoundDefaultJulia = scaledRealStart;
		imaginaryBoundDefaultJulia = scaledImaginaryStart;


		//now do julia
		double zeta_Real=scaledRealStart;
		double zeta_Imaginary = scaledImaginaryStart;

		double zetaUpdate_Real=scaledRealStart;
		double zetaUpdate_Imaginary = scaledImaginaryStart;

		if(check){
			mu_Real = getInputDouble("Please Input the Real Component of Mu [-2,2]",-2,2);
			mu_Imaginary =getInputDouble("Please Input the Imaginary Component of Mu [-1.5,1.5]",-1.5,1.5);
		}

		double delta_Real=  (scaledRealEnd-scaledRealStart)/(double)canvas.getWidth();
		double delta_Imaginary= (scaledImaginaryEnd-scaledImaginaryStart)/(double)canvas.getHeight();

		// double delta_Real=  Math.abs(2*scaledRealStart)/(double)canvas.getWidth();
		// double delta_Imaginary= Math.abs(2*scaledImaginaryStart)/(double)canvas.getHeight();
		sizeRealMaxJulia = Math.abs(scaledRealEnd-scaledRealStart);
		sizeImaginaryMaxJulia = Math.abs(scaledImaginaryEnd-scaledImaginaryStart);
		int t;
		for(int i=0;i<canvas.getHeight();i++){
			zetaUpdate_Real = realBoundDefaultJulia;
			for(int j=0;j<canvas.getWidth();j++){		
				zeta_Real = zetaUpdate_Real;
				zeta_Imaginary= zetaUpdate_Imaginary;
				t=0;
				while(t!=100){
					//System.out.println("tCount:"+t);
					double zetaSave;
					//zeta_Real 
					zetaSave = ((zeta_Real*zeta_Real)-(zeta_Imaginary*zeta_Imaginary)) + mu_Real; //z = z^2 + mu
					zeta_Imaginary = (2*(zeta_Real*zeta_Imaginary))+ mu_Imaginary;
					zeta_Real = zetaSave;
					//System.out.println("real:"+zeta_Real);
					//System.out.println("imaginary:"+zeta_Imaginary);
					if((Math.pow(zeta_Real, 2)+Math.pow(zeta_Imaginary, 2))>4){
						//System.out.println("break");
						break;
					}else{
						t++;
					}
				}

				if(t<100){
					//System.out.println("DIVERGED");
					canvas.setRGB(j,(canvas.getHeight()-1)- i, colors.get(t).getRGB());//diverged
				}else{
					//System.out.println("IN SET");
					//System.exit(0);
					canvas.setRGB(j, (canvas.getHeight()-1)-i,0xFF000000);//in set
				}

				zetaUpdate_Real+= delta_Real;		
			}

			zetaUpdate_Imaginary+=delta_Imaginary;
		}

		// final AreaSelectPanel panel = new AreaSelectPanel(canvas);
		SwingUtilities.invokeLater( new Runnable() {
			public void run(){
				//frame.getContentPane().add( panel, BorderLayout.CENTER );
				frame.pack();
				frame.repaint();
				panel.repaint();
			}});


	}

	//get double input within bounds	
	private static double getInputDouble(String question, double lower, double upper){
		double input =0.0;
		boolean check = false;
		do{
			String result = JOptionPane.showInputDialog(question);

			try {
				try{
					input = Double.parseDouble(result);
					if((input < lower) ||(input > upper)){
						throw new IllegalArgumentException();
					}else{
						check = true;
					}

				}catch(Exception IllegalAgumentException){
					JOptionPane.showMessageDialog(frame, "Please input a double in the correct range");
					check = false;
				}
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(frame, "Please input a double in the correct range");
				check = false;
			}
		}while(check == false);


		return input;		  
	}


}


//////////////AREA SELECT PANEL CODE BELOW////////////////////////////////
package introDAS;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
class AreaSelectPanel extends JPanel{
	static private final Color OUTLINE_COLOR = Color.BLACK;
	// panel size
	private final int WIDTH, MAX_X;
	private final int HEIGHT, MAX_Y;
	// image displayed on panel
	private BufferedImage image;
	private Graphics2D g2d;
	// current selection
	private int x = -1;
	private int y = -1;
	private int w = 0;
	private int h = 0;
	//------------------------------------------------------------------------
	// constructor
	public AreaSelectPanel( BufferedImage image )
	{
		this.image = image;
		g2d = (Graphics2D)image.createGraphics();
		g2d.setXORMode( OUTLINE_COLOR );
		// define panel characteristics
		WIDTH = image.getWidth();
		HEIGHT = image.getHeight();
		Dimension size = new Dimension( WIDTH, HEIGHT );
		setMinimumSize( size );
		setMaximumSize( size );
		setPreferredSize( size );
		MAX_X = WIDTH - 1;
		MAX_Y = HEIGHT - 1;
		addMouseListener( new MouseAdapter()
		{
			public void mousePressed( MouseEvent event )
			{
				clearSelection( event.getPoint() );
			}
		} );
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent event)
			{

				updateSelection( event.getPoint() );
			}
		} );
	}
	//------------------------------------------------------------------------
	// accessors - get points defining the area selected
	Point2D.Double getUpperLeft()
	{
		return getUpperLeft( new Point2D.Double() );
	}
	Point2D.Double getUpperLeft( Point2D.Double p )
	{
		if ( w < 0 )
			if ( h < 0 )
				p.setLocation( (x+w)/((double) MAX_X), (y+h)/((double) MAX_Y) );
			else
				p.setLocation( (x+w)/((double) MAX_X), y/((double) MAX_Y) );
		else if ( h < 0 )
			p.setLocation( x/((double) MAX_X), (y+h)/((double) MAX_Y) );
		else
			p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );
		return p;
	}

	Point2D.Double getLowerRight()
	{
		return getLowerRight( new Point2D.Double() );
	}
	Point2D.Double getLowerRight( Point2D.Double p )
	{
		if ( w < 0 )
			if ( h < 0 )
				p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );
			else
				p.setLocation( x/((double) MAX_X), (y+h)/((double) MAX_Y) );
		else if ( h < 0 )
			p.setLocation( (x+w)/((double) MAX_X), y/((double) MAX_Y) );
		else
			p.setLocation( (x+w)/((double) MAX_X), (y+h)/((double) MAX_Y) );
		return p;
	}
	//------------------------------------------------------------------------
	// change background image
	public void setImage( BufferedImage src )
	{
		g2d.setPaintMode();
		g2d.drawImage( src,
				0, 0, MAX_X, MAX_Y,
				0, 0, (src.getWidth() - 1), (src.getHeight() - 1),
				OUTLINE_COLOR, null );
		g2d.setXORMode( OUTLINE_COLOR );
		x = -1;
		y = -1;
		w = 0;
		h = 0;

		repaint();
	}
	//------------------------------------------------------------------------
	// behaviors

	public void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		g.drawImage( image, 0, 0, null );
	}
	private void clearSelection( Point p )
	{
		// erase old selection
		drawSelection();
		// begin new selection
		x = (p.x < 0) ? 0 : ( (p.x < WIDTH) ? p.x : MAX_X );
		y = (p.y < 0) ? 0 : ( (p.y < HEIGHT) ? p.y : MAX_Y );
		w = 0;
		h = 0;
		drawSelection();
	}
	private void updateSelection( Point p )
	{
		// erase old selection
		drawSelection();

		// modify current selection
		int px = (p.x < 0) ? 0 : ( (p.x < WIDTH) ? p.x : MAX_X );
		int py = (p.y < 0) ? 0 : ( (p.y < HEIGHT) ? p.y : MAX_Y );
		w = px - x;
		h = py - y;
		drawSelection();
	}
	private void drawSelection()
	{
		if ( w < 0 )
			if ( h < 0 )
				g2d.drawRect( (x+w), (y+h), -w, -h );
			else
				g2d.drawRect( (x+w), y, -w, h );
		else if ( h < 0 )
			g2d.drawRect( x, (y+h), w, -h );
		else
			g2d.drawRect( x, y, w, h );
		repaint();
	}
}

