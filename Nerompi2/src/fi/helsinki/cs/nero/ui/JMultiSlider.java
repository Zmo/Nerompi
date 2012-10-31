/*
 * JMultiSlider - JMultiSlider.java
 * Created on 17.10.2004
 * 
 */
package fi.helsinki.cs.nero.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

/**
 * @author Timi Tuohenmaa
 *
 */

public class JMultiSlider extends JComponent {

	final static protected int SIDE_WIDTH = 10;
	final static protected int KNOB_WIDTH = 8;
	final static protected int MIDDLEKNOB_MINUS = 3;
	final private Color BG_COLOR = new Color(172, 210, 248);
	final private Color KNOB_COLOR = new Color(0, 0, 255);
	final private Color MIDDLE_COLOR = new Color(90,119,173);
	
	private Polygon leftKnob, rightKnob, middleKnob;
	
	protected BoundedRangeModel sliderModel;

	private boolean leftKnobMoving, rightKnobMoving;

	private int pressPos, pressLeftKnobPos, pressRightKnobPos;

	private JMultiSliderMouseListener mouseListener;

	protected ChangeListener changeListener = createChangeListener();
	protected transient ChangeEvent changeEvent = null;
	
	/**
	 * Konstruktori, joka luo liukus‰‰timen, joka l‰htee nollasta ja
	 * p‰‰ttyy sataan.
	 */
	public JMultiSlider() {
		this(0, 100);
	}
	
	/**
	 * Konstruktori, jolle annetaan liukus‰‰timen alku- ja loppuarvot.
	 * @param min m‰‰ritt‰‰ Sliderin l‰htˆarvon
	 * @param max m‰‰ritt‰‰ Sliderin loppuarvon
	 */
	public JMultiSlider(int min, int max) {
		leftKnob = new Polygon();
		rightKnob = new Polygon();
		middleKnob = new Polygon();

		sliderModel = new DefaultBoundedRangeModel(min, max, min, max);
		sliderModel.addChangeListener(changeListener);
		
		mouseListener = new JMultiSliderMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}

	/**
	 * Suorittaa yhden ja vain yhden changeEventin kaikille
	 * ChangeListenereille.
	 */
	protected void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (changeEvent == null) {
				changeEvent = new ChangeEvent(this);
			}
			((ChangeListener)listeners[i + 1]).stateChanged(changeEvent);
		}
	}
	
	/**
	 * Luo uuden ChangeListenerin
	 * @return uusi ChangeListener
	 */
	protected ChangeListener createChangeListener() {
		return new ModelListener();
	}
	
	/**
	 * Liitt‰‰ ChangeListenerin slideriin
	 * @param l liitett‰v‰ ChangeListener
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	/**
	 * Poistaa valitun ChangeListenerin sliderista
	 * @param l poistettava ChangeListener
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	/**
	 * Palauttaa vasemman s‰‰timen arvon
	 * @return vasemman s‰‰timen arvo
	 */
	public int getValueLeft() {
		return sliderModel.getValue();
	}
	
	/**
	 * Palauttaa oikean s‰‰timen arvon
	 * @return oikean s‰‰timen arvo
	 */
	public int getValueRight() {
		return sliderModel.getExtent() + sliderModel.getValue();
	}

	/**
	 * Palauttaa tiedon ollaanko juuri liikuttamassa s‰‰timi‰
	 * @return s‰‰timen tila
	 */
	public boolean getValueIsAdjusting() {
		return sliderModel.getValueIsAdjusting();
	}
	
	/**
	 * Palauttaa sliderin minimiarvon
	 * @return minimiarvo
	 */
	public int getMinimum() {
		return sliderModel.getMinimum();
	}
	
	/**
	 * Palauttaa sliderin maksimiarvon
	 * @return maksimiarvo
	 */
	public int getMaximum() {
		return sliderModel.getMaximum();
	}
	
	/**
	 * Asettaa vasemman s‰‰timen arvon.
	 * Arvo korjataan aina siten, ett‰ se on minimin ja maksimin v‰liss‰
	 * sek‰ korkeintaan sama kuin oikean s‰‰timen arvo.
	 * Laukaisee ChangeEventin.
	 * @param value vasemmalle s‰‰timelle annettava lukema
	 */
	public void setValueLeft(int value) {
		value = Math.max(value, getMinimum());
		setValues(value, getValueRight());
	}
	
	/**
	 * Asettaa oikean s‰‰timen arvon.
	 * Arvo korjataan aina siten, ett‰ se on minimin ja maksimin v‰liss‰
	 * sek‰ v‰hint‰‰n sama kuin vasemman s‰‰timen arvo.
	 * Laukaisee ChangeEventin.
	 * @param value
	 */
	public void setValueRight(int value) {
		sliderModel.setExtent(value - sliderModel.getValue());
	}
	
	/**
	 * Asettaa vasemman ja oikean s‰‰timen arvon samaan aikaan ja t‰ten
	 * laukaisee ChangeEventin vain kerran.
	 * Arvot korjataan pysym‰‰n minimin ja maksimin v‰liss‰ ja 
	 * vasemman s‰‰timen arvo korjataan maksimissaan oikean s‰‰timen
	 * suuruiseksi.
	 * @param leftValue vasemmalle s‰‰timelle annettava arvo
	 * @param rightValue oikealle s‰‰timelle annettava arvo
	 */
	public void setValues(int leftValue, int rightValue) {
		leftValue = Math.max(leftValue, sliderModel.getMinimum());
		rightValue = Math.min(rightValue, sliderModel.getMaximum());
		int fixedLeftValue = Math.max(Math.min(leftValue, rightValue),
				sliderModel.getMinimum());
		sliderModel.setRangeProperties(
				fixedLeftValue,
				rightValue - fixedLeftValue,
				sliderModel.getMinimum(),
				sliderModel.getMaximum(),
				sliderModel.getValueIsAdjusting()
		);
	}
	
	/**
	 * Asettaa vasemman, oikean, minimin ja maksimin yhdell‰ kerralla aiheuttaen
	 * vain yhden ChangeEventin.
	 * Vasemman s‰‰timen arvo korjataan minimiss‰‰n miniarvoksi ja maksimissaan
	 * oikean s‰‰timen arvoksi.
	 * @param leftValue vasemman s‰‰timen arvo
	 * @param rightValue oikean s‰‰timen arvo
	 * @param minimumValue minimiarvo
	 * @param maximumValue maksimiarvo
	 */
	public void setValuesAndRange(int leftValue, int rightValue,
			int minimumValue, int maximumValue) {
		leftValue = Math.max(leftValue, minimumValue);
		rightValue = Math.min(rightValue, maximumValue);
		int fixedLeftValue = Math.max(Math.min(leftValue, rightValue),
				minimumValue);
		sliderModel.setRangeProperties(
				fixedLeftValue,
				rightValue - fixedLeftValue,
				minimumValue,
				maximumValue,
				sliderModel.getValueIsAdjusting()
		);
	}
	
	/**
	 * Asettaa minimiarvon sliderille
	 * @param value sliderin minimiarvo
	 */
	public void setMinimum(int value) {
		sliderModel.setMinimum(value);
	}
	
	/**
	 * Asettaa maksimiarvon sliderille
	 * @param value sliderin maksimiarvo
	 */
	public void setMaximum(int value) {
		sliderModel.setMaximum(value);
	}
	
	/**
	 * Palauttaa vasemman s‰‰timen fyysisen sijainnin
	 * @return vasemman s‰‰timen fyysinen sijainti
	 */
	private int getPhysLeft() {
		return (int)Math.round((double)(sliderModel.getValue() -
				sliderModel.getMinimum()) / (sliderModel.getMaximum() -
				sliderModel.getMinimum()) *
				(this.getWidth() - SIDE_WIDTH * 2) );
	}

	/**
	 * Palauttaa oikean s‰‰timen fyysisen sijainnin
	 * @return oikean s‰‰timen fyysinen sijainti
	 */
	private int getPhysRight() {
		return (int)Math.round((double)(sliderModel.getExtent() +
				sliderModel.getValue() -
				sliderModel.getMinimum()) / (sliderModel.getMaximum() -
				sliderModel.getMinimum()) *
				(this.getWidth() - SIDE_WIDTH * 2) );
	}
	
	/**
	 * Piirt‰‰ vasemman s‰‰timen polygonin uudelleen uuteen paikkaan
	 */
	private void updateLeftKnob() {
		leftKnob.reset();
		leftKnob.addPoint(SIDE_WIDTH + getPhysLeft() - KNOB_WIDTH, 1);
		leftKnob.addPoint(SIDE_WIDTH + getPhysLeft(), 1);
		leftKnob.addPoint(SIDE_WIDTH + getPhysLeft(), getHeight() - 2);
		leftKnob.addPoint(SIDE_WIDTH + getPhysLeft() - KNOB_WIDTH,
				getHeight() - KNOB_WIDTH);
		leftKnob.addPoint(SIDE_WIDTH + getPhysLeft() - KNOB_WIDTH, 1);
	}

	/**
	 * Piirt‰‰ oikean s‰‰timen polygonin uudelleen uuteen paikkaan
	 */
	private void updateRightKnob() {
		rightKnob.reset();
		rightKnob.addPoint(SIDE_WIDTH + getPhysRight(), 1);
		rightKnob.addPoint(SIDE_WIDTH + getPhysRight() + KNOB_WIDTH, 1);
		rightKnob.addPoint(SIDE_WIDTH + getPhysRight() + KNOB_WIDTH,
				getHeight() - KNOB_WIDTH);
		rightKnob.addPoint(SIDE_WIDTH + getPhysRight(), getHeight() - 2);
		rightKnob.addPoint(SIDE_WIDTH + getPhysRight(), 1);
	}
	
	private void updateMiddleKnob() {
		middleKnob.reset();
		if (getValueLeft() == getValueRight()) {
			middleKnob.addPoint(SIDE_WIDTH + getPhysRight() - KNOB_WIDTH, 1);
			middleKnob.addPoint(SIDE_WIDTH + getPhysRight() + KNOB_WIDTH, 1);
			middleKnob.addPoint(SIDE_WIDTH + getPhysRight(), getHeight() / 2);
			middleKnob.addPoint(SIDE_WIDTH + getPhysRight() - KNOB_WIDTH, 1);
		}
		else {
			middleKnob.addPoint(SIDE_WIDTH + getPhysLeft(), 3);
			middleKnob.addPoint(SIDE_WIDTH + getPhysRight(), 3);
			middleKnob.addPoint(SIDE_WIDTH + getPhysRight(), getHeight() - 4);
			middleKnob.addPoint(SIDE_WIDTH + getPhysLeft(), getHeight() - 4);
			middleKnob.addPoint(SIDE_WIDTH + getPhysLeft(), 3);
		}
	}

	protected void paintComponent(Graphics g) {
		// piirret‰‰nkˆ pohja vai ei
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		// piirr‰ tausta sliderille mustilla kehyksill‰.
		Graphics2D g2d = (Graphics2D)g.create();
		g2d.setColor(Color.black);
		g2d.fill(new Rectangle(SIDE_WIDTH, 2,
				getWidth() - SIDE_WIDTH * 2, getHeight() - 5) );
		g2d.setColor(BG_COLOR);
		g2d.fill(new Rectangle(SIDE_WIDTH + 1, 3,
				getWidth() - SIDE_WIDTH * 2 - 2, getHeight() - 7) );
			
		// vasen nuppi
		g2d.setColor(KNOB_COLOR);
		updateLeftKnob();
		g2d.fill(leftKnob);
	
		// oikea nuppi
		g2d.setColor(KNOB_COLOR);
		updateRightKnob();
		g2d.fill(rightKnob);

		// alue nuppien v‰liss‰
		g2d.setColor(MIDDLE_COLOR);
		updateMiddleKnob();
		g2d.fill(middleKnob);

		g2d.dispose();
	}

	protected class ModelListener implements ChangeListener, Serializable {
		public void stateChanged(ChangeEvent event) {
			fireStateChanged();
		}
	}
	
	private class JMultiSliderMouseListener implements
			MouseInputListener {

		public void mousePressed(MouseEvent event) {
			if (event.getButton() == MouseEvent.BUTTON1) {
				pressPos = event.getX();
				pressLeftKnobPos = getValueLeft();
				pressRightKnobPos = getValueRight();
				
				if (middleKnob.contains(event.getPoint()) ) {
					sliderModel.setValueIsAdjusting(true);
					leftKnobMoving = rightKnobMoving = true;
				}
				else if (leftKnob.contains(event.getPoint()) ) {
					sliderModel.setValueIsAdjusting(true);
					leftKnobMoving = true;
				}
				else if (rightKnob.contains(event.getPoint()) ) {
					sliderModel.setValueIsAdjusting(true);
					rightKnobMoving = true;
				}
			}			
		}
	
		public void mouseReleased(MouseEvent event) {
			if (event.getButton() == MouseEvent.BUTTON1) {
				sliderModel.setValueIsAdjusting(false);
				leftKnobMoving = false;
				rightKnobMoving = false;
			}
		}
		
		public void mouseDragged(MouseEvent event) {
			int pressDiff =
				(int)Math.round(((double)event.getX() - pressPos) /
				(getWidth() - SIDE_WIDTH * 2) *
				(sliderModel.getMaximum() - sliderModel.getMinimum())) +
				sliderModel.getMinimum();
			if (sliderModel.getValueIsAdjusting()) {
				if (leftKnobMoving && rightKnobMoving) {
					sliderModel.setValue(pressLeftKnobPos + pressDiff);
				}
				else if (leftKnobMoving) {
					setValueLeft(pressLeftKnobPos + pressDiff);
				}
				else if (rightKnobMoving) {
					setValueRight(pressRightKnobPos + pressDiff);
				}
			}
			repaint();
		}
	
		public void mouseClicked(MouseEvent event) {}
		public void mouseEntered(MouseEvent event) {}
		public void mouseExited(MouseEvent event) {}
		public void mouseMoved(MouseEvent event) {}
	}

	/**
	 * Luo testik‰yttˆliittym‰n, jossa voi liikutella Slideria
	 */
	private static void createAndShowTestGUI() {
		JFrame frame = new JFrame("JMultiSlider");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMultiSlider multiSlider = new JMultiSlider();
		final JLabel leftLabel = new JLabel();
		final JLabel rightLabel = new JLabel();
		multiSlider.setPreferredSize(new Dimension(300,20));
		leftLabel.setPreferredSize(new Dimension(28, 16));
		rightLabel.setPreferredSize(new Dimension(28, 16));
		frame.getContentPane().add(multiSlider, BorderLayout.NORTH);
		frame.getContentPane().add(leftLabel, BorderLayout.WEST);
		frame.getContentPane().add(rightLabel, BorderLayout.EAST);
		multiSlider.setValueLeft(50);
		multiSlider.setValueRight(20);
		multiSlider.setValueLeft(10);
		multiSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				JMultiSlider slider = (JMultiSlider)event.getSource();
				leftLabel.setText(String.valueOf(slider.getValueLeft()));
			}
		});
		multiSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				JMultiSlider slider = (JMultiSlider)event.getSource();
				rightLabel.setText(String.valueOf(slider.getValueRight()));
			}
		});
		leftLabel.setVisible(true);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowTestGUI();
			}
		});
	}

}
