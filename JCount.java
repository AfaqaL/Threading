// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JCount extends JPanel {
	private JTextField text;
	private JLabel label;
	private JButton startBtn;
	private JButton stopBtn;
	private Counter counter;
	public JCount() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		createElements();
		addElements();
		setListeners();
	}

	private void setListeners() {
		startBtn.addActionListener(e -> {
			if(counter != null)
				counter.interrupt();

			counter = new Counter(text.getText());
			counter.start();
		});

		stopBtn.addActionListener(l -> {
			if(counter != null)
				counter.interrupt();
		});
	}

	private class Counter extends Thread{
		private static final int SLEEP_TIMER = 100;
		private int value;
		public Counter(String text){
			value = Integer.parseInt(text);
		}

		@Override
		public void run() {
			for (int i = 0; i <= value; i++) {
				if(isInterrupted()) return;
				if(i % 10000 == 0){
					try {
						updateLabel(i);
						Thread.sleep(SLEEP_TIMER);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}

		private void updateLabel(int i) {
			SwingUtilities.invokeLater(() -> label.setText(String.valueOf(i)));
		}
	}

	private void addElements() {
		super.add(text);
		super.add(label);
		super.add(startBtn);
		super.add(stopBtn);
		super.add(Box.createRigidArea(new Dimension(0,40)));
	}

	private void createElements() {
		text = new JTextField("0");
		label = new JLabel("0");
		startBtn = new JButton("Start");
		stopBtn = new JButton("Stop");
		counter = null;
	}

	static public void main(String[] args)  {
		// Creates a frame with 4 JCounts in it.
		// (provided)
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

