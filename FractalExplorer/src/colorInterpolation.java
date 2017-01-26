

public class colorInterpolation {
	protected int colorA;
	protected int colorB;
	protected int steps;

	int Red1;
	int Green1;
	int Blue1;

	int Red2;
	int Green2;
	int Blue2;
	public colorInterpolation( int colorA, int colorB, int steps){
		this.colorA = colorA;
		this.colorB = colorB;
		this.steps = steps;
	}
	// =======================================================================
	public void extract(){
		this.Red1 = (colorA >>> 16) & 0x000000FF;
		this.Green1 = (colorA >>> 8) & 0x000000FF;
		this.Blue1 = (colorA) & 0x000000FF;
		//
		this.Red2 = (colorB >>> 16) & 0x000000FF;
		this.Green2 = (colorB >>> 8) & 0x000000FF;
		this.Blue2 = (colorB) & 0x000000FF;
	}
	public int colorInterp(int index){
		//delta
		double dRed = (double)(Red2 - Red1)/(steps-1);
		double dGreen = (double)(Green2 - Green1)/(steps-1);
		double dBlue = (double)(Blue2 - Blue1)/(steps-1);
		//initialize increment
		double nRed = Red1;
		double nGreen = Green1;
		double nBlue = Blue1;
		int pixelM = 0;
		// =======================================================================
		for(int n = 0; n < index+1; n++){
			pixelM = (0x000000FF << 24)|((int)nRed << 16)|((int)nGreen << 8)|(int)nBlue;
			nRed += dRed;
			nGreen += dGreen;
			nBlue += dBlue;
		}
		return pixelM;
	}
}

