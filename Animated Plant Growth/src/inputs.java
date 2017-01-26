import javax.swing.*;

public class inputs {
	static int height;
	int stems;
	int steps;
	static double prob;
	static double maxRot;
	static double growth;

	public inputs(){
		String str = JOptionPane.showInputDialog("How large is the image?");
		this.height = Integer.parseInt(str);
		String stem = JOptionPane.showInputDialog("How many stems does the plant have?");
		this.stems = Integer.parseInt(stem);
		String step = JOptionPane.showInputDialog("How many steps per stem?");
		this.steps = Integer.parseInt(step);
		String probability = JOptionPane.showInputDialog("What is the transmission probabilitiy?");
		this.prob = Double.parseDouble(probability);
		String rotation = JOptionPane.showInputDialog("What is the maximum rotation angle?");
		this.maxRot = Double.parseDouble(rotation);
		String grow = JOptionPane.showInputDialog("What is the growth segment increment?");
		this.growth = Double.parseDouble(grow);
	}
}
