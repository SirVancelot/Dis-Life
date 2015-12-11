import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class SteadyClient {
	private static final int TIMEOUT = 1000/8;

	private Integer state = 0;

	private InetAddress[] peerAddr;
	private int[] peerPort;
	
	public static void main(String[] args) throws IOException {
		InetAddress[] peerAddr = new InetAddress[8];
		int[] peerPort = new int[8];

		for (int i = 0; i < 8; i++) {
			peerAddr[i] = InetAddress.getByName("localhost");
			peerPort[i] = Integer.parseInt(args[i]);
		}
		
		SteadyClient client = new SteadyClient(peerAddr, peerPort);
		client.run();
	}
	
	public SteadyClient(InetAddress[] peerAddr, int[] peerPort) {
		this.peerAddr = peerAddr;
		this.peerPort = peerPort;
	}
	
	public void run() throws IOException {
		byte[][] received = new byte[8][];
		byte[][] toSend;
		
		DatagramSocket[] socket = new DatagramSocket[8];

		int[] localPorts = new int[8];
		for (int i = 0; i < 8; i++) {
			socket[i] = new DatagramSocket();
			localPorts[i] = socket[i].getLocalPort();
			socket[i].setSoTimeout(TIMEOUT);
		}
		
		sendPorts(localPorts);
		
		boolean firstPacket = true;
		while (true) {
			toSend = getState(received);

			for (int i = 0; i < 8; i++) {
				if (peerPort[i] != -1) {
					// Send to peer
					byte[] sendBuf = toSend[i];
					DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, peerAddr[i], peerPort[i]);
					socket[i].send(sendPacket);
				}
				
				if (firstPacket) continue;
		
				// Receive from peer
				byte[] recvBuf = new byte[64];
				DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

				try {
					socket[i].receive(recvPacket);
				} catch (SocketTimeoutException e) {
					received[i] = null;
					continue;
				}
		
				peerAddr[i] = recvPacket.getAddress();
				peerPort[i] = recvPacket.getPort();
		
				received[i] = Arrays.copyOfRange(recvPacket.getData(), 0, recvPacket.getLength());
			}
			firstPacket = false;
		}
	}

	private byte[][] getState(byte[][] received) {
		// update the state
		state++;

		byte[][] toSend = new byte[8][];
		String[] receivedStr = new String[8];
		for (int i = 0; i < 8; i++) {
			toSend[i] = state.toString().getBytes();
			
			if (received[i] == null) {
				receivedStr[i] = "*";
			} else {
				receivedStr[i] = new String(received[i]);
			}
		}
		
		System.out.println(receivedStr[0] + " " + receivedStr[1] + " " + receivedStr[2]);
		System.out.println(receivedStr[3] + " " + (state - 1)    + " " + receivedStr[4]);
		System.out.println(receivedStr[5] + " " + receivedStr[6] + " " + receivedStr[7]);
		System.out.println("");
		
		return toSend;
	}

	private void sendPorts(int[] localPorts) {
		for (int i = 0; i < localPorts.length; i++) {
			System.out.println(localPorts[i]);
		}
	}
}
