import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

public class ClientGui {
	JFrame jf;
	ClientPanel panel;
	Client master;

	public ClientGui(Client ms) {
		master = ms;

		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) { }

		jf = new JFrame("Dis-Life");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new ClientPanel();

		final JButton button = new JButton("Connect & Start");
		button.addActionListener( new ActionListener() {
			Thread rthread;
			public void actionPerformed(ActionEvent ae) {
				if (master.isRunning()) {
					master.stop();
					try {
						rthread.join();
					} catch (InterruptedException iex) { }
					button.setText("Connect & Start");
				} else {
					rthread = new Thread(master);
					rthread.start();
					button.setText("Stop");
				}
			}
		});

		jf.getContentPane().add(panel, BorderLayout.CENTER);
		jf.getContentPane().add(button, BorderLayout.SOUTH);
		jf.setSize(600, 700);
		jf.setVisible(true);
	}

	public void update(boolean[][] data) {
		panel.setData(data);
		panel.repaint();
	}

	private class ClientPanel extends JPanel implements MouseListener,MouseMotionListener {
		private static final long serialVersionUID = 1L;
		boolean[][] data;

		public ClientPanel() {
			addMouseListener(this);
			addMouseMotionListener(this);
			data = new boolean[10][10];
		}

		public void setData(boolean[][] nData) {
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					data[i][j] = nData[i+1][j+1];
				}
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			int graphSize = data.length;
			float pixW = (float)this.getWidth() / (float)graphSize;
			float pixH = (float)this.getHeight() / (float)graphSize;

			for (int i = 0; i < graphSize; i++) {
				for (int j = 0; j < graphSize; j++) {
					if (data[i][j]) {
						g.setColor(Color.BLACK);
					} else {
						g.setColor(Color.WHITE);
					}

					g.fillRect((int)pixW * j, (int)pixH * i, (int)pixW, (int)pixH);
				}
			}
		}

		private int l_nX;
		private int l_nY;

		@Override
		public void mouseDragged(MouseEvent me) {
			int evX = me.getX();
			int evY = me.getY();
			if (evX > this.getWidth() || evX < 0) return;
			if (evY > this.getHeight() || evY < 0) return;
			int nX = evX * data.length / this.getWidth();
			int nY = evY * data.length / this.getHeight();

			if (l_nX != nX || l_nY != nY)
				data[nY][nX] = !data[nY][nX];
			l_nX = nX;
			l_nY = nY;

			repaint();
			master.setGrid(data);
		}

		@Override
		public void mouseMoved(MouseEvent me) { }

		@Override
		public void mouseClicked(MouseEvent me) {
			int nX = me.getX() * data.length / this.getWidth();
			int nY = me.getY() * data.length / this.getHeight();
			data[nY][nX] = !data[nY][nX];
			repaint();
			master.setGrid(data);
		}

		@Override
		public void mousePressed(MouseEvent me) { }
		@Override
		public void mouseReleased(MouseEvent me) { }
		@Override
		public void mouseEntered(MouseEvent me) { }
		@Override
		public void mouseExited(MouseEvent me) { }
	}
}
