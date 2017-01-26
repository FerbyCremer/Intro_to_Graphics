import java.awt.Color;

public class paint {
	int steps;
	Color bColor;
	Color tColor;
	Color[] colors;

	public paint(Color bColor, Color tColor, int steps){
		this.steps = steps;
		this.bColor = bColor;
		this.tColor = tColor;
		colorArray();
	}
	public Color[] colorArray(){
		colors = new Color[steps];
		//components of base color
		double r = (bColor.getRGB() >>> 16) & 0x000000FF;
		double g = (bColor.getRGB() >>> 8) & 0x000000FF;
		double b = bColor.getRGB() & 0x000000FF;
		//components of tip color
		double R = (tColor.getRGB() >>> 16) & 0x000000FF;
		double G = (tColor.getRGB() >>> 8) & 0x000000FF;
		double B = tColor.getRGB() & 0x000000FF;
		//color delta
		double dRed = (double)(R-r)/(steps-1);
		double dGreen = (double)(G-g)/(steps-1);
		double dBlue = (double)(B-b)/(steps-1);
		//color array
		for(int i = 0; i< steps; i++){
			colors[i] = new Color(0xFF000000 |((int)r << 16)|((int)g << 8)|(int)b);
			r += dRed;
			g += dGreen;
			b += dBlue;
		}
		return colors;
	}
	public Color pcolor(int g){
		Color curr = colors[g];
		return curr;
	}
}
