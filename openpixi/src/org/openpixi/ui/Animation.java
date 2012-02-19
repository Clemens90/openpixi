package org.openpixi.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import javax.swing.event.*;

/**
 * Displays the animation of particles.
 */
public class Animation extends JApplet {

	private JButton startButton, stopButton, resetButton;
	private JSlider speedSlider;
	private JSlider stepSlider;
	
	private JSlider efieldXSlider;
	private JSlider efieldYSlider;
	private JSlider bfieldZSlider;
	private JSlider gfieldXSlider;
	private JSlider gfieldYSlider;
	
	private JComboBox initComboBox;
	private JCheckBox traceCheck;
	private Particle2DPanel particlePanel;

	String[] initStrings = {
			"10 random particles",
			"100 random particles",
			"1000 random particles",
			"10000 random particles",
			"Single particle in gravity",
			"Three particles in magnetic field" };

	/**
	 * Listener for slider.
	 */
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if (!source.getValueIsAdjusting()) {
				int frames = (int) source.getValue();
				if (frames == 0) {
					if (!particlePanel.sl)
						particlePanel.stopAnimation();
				} else {
					int delay = (int) source.getMaximum()
							- (int) source.getValue();
					particlePanel.timer.stop();
					particlePanel.timer.setDelay(delay);
					particlePanel.timer.setInitialDelay(10 * delay);
					particlePanel.timer.start();
				}
			}
		}
	}
	
	class ComboBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			int id  = cb.getSelectedIndex();
			particlePanel.resetAnimation(id);
		}
	}

	/**
	 * Listener for start button.
	 */
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			particlePanel.startAnimation();
		}
	}

	/**
	 * Listener for stop button.
	 */
	class StopListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			particlePanel.stopAnimation();
		}
	}

	/**
	 * Listener for reset button.
	 */
	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			particlePanel.resetAnimation(initComboBox.getSelectedIndex());
		}
	}
	
	class CheckListener implements ItemListener {
		public void itemStateChanged(ItemEvent eve){
				particlePanel.checkTrace();
		}
		
	}
	
	class EFieldXListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(!source.getValueIsAdjusting())
			{
				int value = source.getValue();
				particlePanel.f.setEX(value);
			}
		}
	}
	
	class EFieldYListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(!source.getValueIsAdjusting())
			{
				int value = source.getValue();
				particlePanel.f.setEY(value);
			}
		}
	}
	
	
	class BFieldZListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(!source.getValueIsAdjusting())
			{
				int value = source.getValue();
				particlePanel.f.setBZ(value);
			}
		}
	}
	
	class GFieldXListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(!source.getValueIsAdjusting())
			{
				int value = source.getValue();
				particlePanel.f.setGX(value);
			}
		}
	}
	
	class GFieldYListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(!source.getValueIsAdjusting())
			{
				int value = source.getValue();
				particlePanel.f.setGY(value);
			}
		}
	}
	
	class StepListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(!source.getValueIsAdjusting())
			{
				int value = source.getValue();
				particlePanel.step = value;
			}
		}
	}
	/**
	 * Constructor.
	 */
	public Animation() {
		particlePanel = new Particle2DPanel();

		this.setVisible(true);
		this.setSize(1000, 500);

		startButton = new JButton("start");
		stopButton = new JButton("stop");
		resetButton = new JButton("reset");

		/**one can also write the constructor for a JSlider as:
		 * speerSlider = new JSlider(int min, int max, int value);
		 * where min is the minimal value (the same as setMinimum(int min), 
		 * max is the maximal value (the same as setMinimum(int max),
		 * and value is the current value (the same as setValue(int value),
		 * and the code would be shorter,
		 * but they are written like this, so it is clearer and not so confusing
		 */
		
		speedSlider = new JSlider();
		speedSlider.addChangeListener(new SliderListener());
		speedSlider.setMinimum(0);
		speedSlider.setMaximum(50);
		speedSlider.setValue(30);
		speedSlider.setMajorTickSpacing(5);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		JLabel speedLabel = new JLabel("Speed");
		Box speed = Box.createVerticalBox();
		speed.add(speedSlider);
		speed.add(speedLabel);
		
		stepSlider = new JSlider();
		stepSlider.addChangeListener(new StepListener());
		stepSlider.setMinimum(0);
		stepSlider.setMaximum(70);
		stepSlider.setValue(30);
		stepSlider.setMajorTickSpacing(5);
		stepSlider.setMinorTickSpacing(1);
		stepSlider.setPaintTicks(true);
		JLabel stepLabel = new JLabel("Step");
		
		efieldXSlider = new JSlider();
		efieldXSlider.addChangeListener(new EFieldXListener());
		efieldXSlider.setMinimum(0);
		efieldXSlider.setMaximum(10);
		efieldXSlider.setValue(0);
		efieldXSlider.setMajorTickSpacing(2);
		efieldXSlider.setMinorTickSpacing(1);
		efieldXSlider.setPaintTicks(true);
		
		efieldYSlider = new JSlider();
		efieldYSlider.addChangeListener(new EFieldYListener());
		efieldYSlider.setMinimum(0);
		efieldYSlider.setMaximum(10);
		efieldYSlider.setValue(0);
		efieldYSlider.setMajorTickSpacing(2);
		efieldYSlider.setMinorTickSpacing(1);
		efieldYSlider.setPaintTicks(true);
		
		bfieldZSlider = new JSlider();
		bfieldZSlider.addChangeListener(new BFieldZListener());
		bfieldZSlider.setMinimum(0);
		bfieldZSlider.setMaximum(10);
		bfieldZSlider.setValue(0);
		bfieldZSlider.setMajorTickSpacing(2);
		bfieldZSlider.setMinorTickSpacing(1);
		bfieldZSlider.setPaintTicks(true);
		
		gfieldXSlider = new JSlider();
		gfieldXSlider.addChangeListener(new GFieldXListener());
		gfieldXSlider.setMinimum(0);
		gfieldXSlider.setMaximum(10);
		gfieldXSlider.setValue(0);
		gfieldXSlider.setMajorTickSpacing(2);
		gfieldXSlider.setMinorTickSpacing(1);
		gfieldXSlider.setPaintTicks(true);
		
		gfieldYSlider = new JSlider();
		gfieldXSlider.addChangeListener(new GFieldYListener());
		gfieldYSlider.setMinimum(0);
		gfieldYSlider.setMaximum(10);
		gfieldYSlider.setValue(0);
		gfieldYSlider.setMajorTickSpacing(2);
		gfieldYSlider.setMinorTickSpacing(1);
		gfieldYSlider.setPaintTicks(true);
		
		initComboBox = new JComboBox(initStrings);
		initComboBox.setSelectedIndex(0);
		initComboBox.addActionListener(new ComboBoxListener());
		
		startButton.addActionListener(new StartListener());
		stopButton.addActionListener(new StopListener());
		resetButton.addActionListener(new ResetListener());
		
		traceCheck = new JCheckBox("Trace");
		traceCheck.addItemListener(new CheckListener());

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(startButton);
		controlPanel.add(stopButton);
		controlPanel.add(resetButton);
		controlPanel.add(initComboBox);
		controlPanel.add(speed);
		controlPanel.add(traceCheck);
		
		JLabel eFieldXLabel = new JLabel("Ex Field");
		JLabel eFieldYLabel = new JLabel("Ey Field");
		JLabel bFieldZLabel = new JLabel("Bz Field");
		JLabel gFieldXLabel = new JLabel("Gx Field");
		JLabel gFieldYLabel = new JLabel("Gy Field");
		
		Box fieldsBox = Box.createVerticalBox();
		fieldsBox.add(eFieldXLabel);
		fieldsBox.add(efieldXSlider);
		fieldsBox.add(eFieldYLabel);
		fieldsBox.add(efieldYSlider);
		fieldsBox.add(Box.createVerticalGlue());
		fieldsBox.add(bFieldZLabel);
		fieldsBox.add(bfieldZSlider);
		fieldsBox.add(Box.createVerticalGlue());
		fieldsBox.add(gFieldXLabel);
		fieldsBox.add(gfieldXSlider);
		fieldsBox.add(gFieldYLabel);
		fieldsBox.add(gfieldYSlider);
		fieldsBox.add(Box.createVerticalGlue());
		fieldsBox.add(stepLabel);
		fieldsBox.add(stepSlider);
		
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.SOUTH);
		this.add(particlePanel, BorderLayout.CENTER);
		this.add(fieldsBox, BorderLayout.EAST);

	}

	/**
	 * Entry point for java application.
	 */
	public static void main(String[] args) {

		JFrame web = new JFrame();

		web.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		web.setTitle("Animation");
		web.setContentPane(new Animation());

		web.pack();
		web.setVisible(true);
		web.setSize(1000, 500);
	}

}
