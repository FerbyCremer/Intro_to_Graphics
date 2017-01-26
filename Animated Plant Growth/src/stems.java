import java.awt.geom.Line2D;
import java.util.Random;

public class stems{
	double theta = Math.PI/2.0;
	double segment = 1.0;
	double x;
	double y;
	double xd;
	double yd;
	double reflect;
	int direct;
	Random rand = new Random();
	Line2D plants = new Line2D.Double();
	
	public stems(){
		this.theta = Math.PI/2.0;
		this.segment = 1.0;
		this.x = inputs.height/2.0;
		this.y = inputs.height/2.0;
		this.reflect = 1.0 - inputs.prob;
		this.direct = rand.nextBoolean() ? -1 : 1;
	}
	public stems(double theta, double segment, double x, double y, int direct){
		this.theta = theta;
		this.segment = segment;
		this.x = x;
		this.y = y;
		this.reflect = 1.0 - inputs.prob;
		this.direct = direct;
	}
	//plotting segment function
	public Line2D plot(){
		offset();
		xd = segment*Math.cos(theta);
		yd = segment*Math.sin(theta);
		plants.setLine(x, y, x+xd, y-yd);
		x += xd;
		y -= yd;
		return plants;
	}
	//determine tau and flip coin
	public int Coin(){
		double tau;
		if (direct == -1){tau = inputs.prob;}
		else{tau = reflect;}
		double r = rand.nextDouble();
		if (r > tau){direct = 1;}
		else{direct = -1;}
		return direct;
	}
	//compute offset
	public void offset(){
		Coin();
		segment += inputs.growth;
		theta += inputs.maxRot*rand.nextDouble()*direct;
	}
}
