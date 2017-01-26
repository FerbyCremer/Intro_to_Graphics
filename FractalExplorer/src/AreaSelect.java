/**
 * illustrates Dave's AreaSelectPane
 */

/**
 * @author Dave Small 
 *
 */

import java. awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import javax.imageio.*;
import java.io.*;

class AreaSelect {
	public static void main( String[] args ){
		SwingUtilities.invokeLater( new Runnable() {
			public void run(){
				createAndShowGUI();
			}
		});
	}
	public static void createAndShowGUI(){
		JFrame frame = new JFrame();
		frame.setTitle( "AreaSelectPanel" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		BufferedImage img = null;
		try{
			img = ImageIO.read( new File( "vader.jpg" ) );
		} catch (IOException e ){}

		final AreaSelectPanel panel = new AreaSelectPanel( img );

		final JButton button = new JButton( "Select" );
		button.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ){
				button.setText( "Selected " + panel.getUpperLeft() + " to " +
						panel.getLowerRight() );
			}
		});

		frame.getContentPane().add( panel, BorderLayout.CENTER );
		frame.getContentPane().add( button, BorderLayout.SOUTH );

		frame.pack();
		frame.setVisible( true );
	}
}
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