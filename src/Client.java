import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client implements Runnable {
	
	private static final int TIMEOUT = 1000;
	private static final int BUF_SIZE = Grid.GRID_SIZE;
	private boolean nogui = false;
	private Grid grid = new Grid();

	private ClientGui cgui;
	private String masterHost;
	private int masterPort;

	private DatagramSocket[] sockets = new DatagramSocket[8];
	private int[] localPorts = new int[8];
	private ClientAddress[] neighbors = new ClientAddress[8];

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Client().init(args);
	}
	
	private void init(String[] args) throws UnknownHostException, IOException {
		if (args[0].equals("-n")) {
			// no-gui mode
			nogui = true;

			for (int i = 1; i < args.length; i++) args[i-1] = args[i];
		}

		try {
			masterHost = args[0];
			masterPort = Integer.parseInt(args[1]);
		} catch(Exception e) {
			System.out.println("usage: java Client server-address server-port");
			System.exit(1);
		}
		// create 8 udp sockets
		for (int i = 0; i < sockets.length; i++) {
			sockets[i] = new DatagramSocket();
		}

		if (!nogui) {
			cgui = new ClientGui(this);
		}
		// open tcp connection with master
		System.out.println("opening tcp connection with " + masterHost + " on port " + masterPort);
		Socket masterSocket = new Socket(masterHost, masterPort);
		PrintWriter masterOut = new PrintWriter(masterSocket.getOutputStream());
		BufferedReader masterIn = new BufferedReader(new InputStreamReader(masterSocket.getInputStream(), "UTF-8"));
		// send ports
		System.out.println("sending ports");
		for (int i = 0; i < sockets.length; i++) {
			masterOut.println(sockets[i].getLocalPort());
		}
		for (int i = 0; i < sockets.length; i++) {
			System.out.println(sockets[i].getLocalPort() + "");
		}
		masterOut.flush();
		// receive neighbors
		System.out.println("receiving neighbors");
		for (int i = 0; i < sockets.length; i++) {
			String host = masterIn.readLine();
			String portStr = masterIn.readLine();
			System.out.print(host + " - ");
			System.out.println(portStr);
			if (host.equals("*") && portStr.equals("*")) {
				neighbors[i] = null;
			} else {
				int port = Integer.parseInt(portStr);
				neighbors[i] = new ClientAddress(host, port);
			}
		}
		System.out.println("close tcp connection");
		// close tcp connection
		masterSocket.close();

		if (nogui) {
			// go automatically
			run();
		}
	}

	void setGrid(boolean[][] nGrid) {
		grid.setGrid(nGrid);
	}

	private boolean running = false;

	public boolean isRunning() {
		return running;
	}

	public void run() {
		try {
			running = true;
			innerRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		running = false;
	}

	private void innerRun() throws IOException, InterruptedException {
		// start simulating
		byte[][] received = new byte[8][];
		byte[][] toSend;

		for (int i = 0; i < 8; i++) {
			localPorts[i] = sockets[i].getLocalPort();
			sockets[i].setSoTimeout(1);
		}
		
		long millisStart = System.currentTimeMillis();
		boolean firstPacket = true;

		while (running) {
			toSend = getSendState();

			for (int i = 0; i < 8; i++) {
				if (neighbors[i] != null) {
					// Send to peer
					byte[] sendBuf = toSend[i];
					DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName(neighbors[i].host), neighbors[i].port);
					sockets[i].send(sendPacket);
				}
				
				// ?
				if (firstPacket) continue;
			}
			// wait
			while(System.currentTimeMillis() - millisStart < TIMEOUT/2) {}
			millisStart = System.currentTimeMillis();
			
			for (int i = 0; i < 8; i++) {
				// Receive from peer
				byte[] recvBuf = new byte[BUF_SIZE];
				DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

				// get most recent packet from buffer
				boolean re = false;
				while(true) {
					try {
						sockets[i].receive(recvPacket);
						re = true;
					} catch (SocketTimeoutException e) {
						if (!re)
							received[i] = null;
						break;
					}
				}
				
				if (!re) continue;
				
				ClientAddress senderAddress = new ClientAddress(recvPacket.getAddress().getHostAddress(), recvPacket.getPort());
				if (!senderAddress.equals(neighbors[i])) {
					neighbors[i] = senderAddress;
				}
				received[i] = Arrays.copyOfRange(recvPacket.getData(), 0, recvPacket.getLength());
			}
			firstPacket = false;
			
			updateNeighborCells(received);
			if (nogui) System.out.println(grid.toString());
			grid.tick();
			if (!nogui) cgui.update(grid.getGrid());
			// wait
			while(System.currentTimeMillis() - millisStart < TIMEOUT/2) {}
			millisStart = System.currentTimeMillis();
		}
	}
	
	
	private void updateNeighborCells(byte[][] received) {
		for (int i = 0; i < 8; i++) {
			if (received[i] != null) {
				boolean[] data = toBoolean(received[i]);
				grid.set(i, data);
			}
		}
	}

	private byte[][] getSendState() {
		byte[][] toSend = new byte[8][];
		for (int i = 0; i < 8; i++) {
			toSend[i] = fromBoolean(grid.get(i));
		}
		return toSend;
	}
	
	private boolean[] toBoolean(byte[] data) {
		boolean[] result = new boolean[data.length];
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 1) {
				result[i] = true;
			} else {
				result[i] = false;
			}
		}
		return result;
	}
	
	private byte[] fromBoolean(boolean[] data) {
		byte[] result = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			if (data[i]) {
				result[i] = 1;
			} else {
				result[i] = 0;
			}
		}
		return result;
	}
}
