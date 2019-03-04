package ca.mcgill.ecse223.block.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * This helper class creates a Slider object based on a JSlider with a set of JLabels
 * for defining minimum and maximum values, as well as a numerical readout.
 * @author Kelly Ma
 */
public class Slider {
	
	JPanel panel;
	JLabel nameLabel;
	JLabel readOut;
	JSlider slider;
	int min;
	int max;
	double dmin;
	double dmax;
	
	private static final int SMIN = 1;
	private static final int SMAX = 100;

	/**
	 * The constructor to create a JSlider for this application.
	 * @param name The name of the slider
	 * @param min The minimum value permitted
	 * @param max The maximum value permitted
	 * @param val A starting value for the slider 
	 */
	public Slider(String name, Integer min, Integer max, Integer val) {
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		nameLabel = new JLabel(name);
		slider = new JSlider(min, max, val);
		readOut = new JLabel(val.toString());
		defineFont(nameLabel);
		defineFont(readOut);
		this.min = min;
		this.max = max;
		panel.add(nameLabel);
		panel.add(slider);
		panel.add(readOut);
		slider.setBackground(Color.WHITE);
		panel.setBackground(Color.WHITE);
	}
	
	/**
	 * The constructor to create a JSlider for this application.
	 * Overloaded to accept double values.
	 * @param name The name of the slider
	 * @param min The minimum value permitted
	 * @param max The maximum value permitted
	 * @param val A starting value for the slider 
	 */
	public Slider(String name, Double min, Double max, Double val) {
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		nameLabel = new JLabel(name);
		slider = new JSlider(SMIN, SMAX, (int) ((SMAX-SMIN)*(val/(max-min))));
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		readOut = new JLabel(""+numberFormat.format(val));
		defineFont(nameLabel);
		defineFont(readOut);
		dmin = min;
		dmax = max;
		panel.add(nameLabel);
		panel.add(slider);
		panel.add(readOut);
		slider.setBackground(Color.WHITE);
		panel.setBackground(Color.WHITE);
	}
	
	/**
	 * Returns the value at the slider as an integer.
	 * @return The slider value
	 */
	public int getIValue() {
		return slider.getValue();
	}
	
	/**
	 * Sets the value upon readout as a String.
	 * @param arg An integer value
	 */
	void setISlider(Integer arg) {
		readOut.setText(arg.toString());
	}
	
	/**
	 * Returns the value at the slider as a double.
	 * @return The slider value
	 */
	public double getDValue() {
		double scale = ((double)slider.getValue())/SMAX;
		return dmin + scale * (dmax - dmin);
	}
	
	/**
	 * Sets the value upon readout as a String.
	 * @param arg An integer value
	 */
	void setDSlider(Double arg) {
		readOut.setText(arg.toString());
	}
	
	/**
	 * Helper method to modify font properties of a JLabel.
	 * @param label A particular JLabel
	 */
	private void defineFont(JLabel label) {
		label.setFont(new Font(Block223MainPage.UI_FONT.getFamily(), Font.PLAIN, 8));
	}
	
}