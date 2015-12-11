import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Master {
	Selector<ClientAddressAllPorts> selector = new Selector<ClientAddressAllPorts>();
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		new Master().go(args);
	}
	
	private void go(String[] args) throws IOException {
		int masterPort = 0;
		try {
			masterPort = Integer.parseInt(args[0]);
		} catch(Exception e) {
			System.out.println("usage: java Master port-number");
			System.exit(1);
		}
		ServerSocket masterSocket = new ServerSocket(masterPort);
		System.out.println("Master accepting connections on host " + masterSocket.getInetAddress().getHostAddress() + " and port " + masterPort);
		
		while (true) {
			Socket clientSocket = masterSocket.accept();
			System.out.println("accepted connection");
			ConnectionHandler c = new ConnectionHandler(clientSocket);
			c.start();
		}
	}
	
	private class ConnectionHandler extends Thread {
		
		private Socket clientSocket;
		
		public ConnectionHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		public void run() {
			try {
				System.out.println("run()");
				PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream());
				BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				String host = clientSocket.getInetAddress().getHostAddress();
				ArrayList<Integer> ports = new ArrayList<Integer>();
				// read all 8 directional udp ports from client
				System.out.println("reading ports");
				for (int i = 0; i < 8; i++) {
					ports.add(Integer.parseInt(clientIn.readLine()));
				}
				for (int i = 0; i < 8; i++) {
					System.out.println(ports.get(i) + "");
				}
				System.out.println("done reading ports");
				ClientAddressAllPorts addr = new ClientAddressAllPorts(host, ports);
				ArrayList<ClientAddressAllPorts> neighborsAllPorts = selector.insertAndGetNeighbors(addr);
				ArrayList<ClientAddress> neighbors = new ArrayList<ClientAddress>();
				assert(neighborsAllPorts.size() == 8);
				for (int i = 0; i < neighborsAllPorts.size(); i++) {
					ClientAddressAllPorts neighborAllPorts = neighborsAllPorts.get(i);
					neighbors.add(neighborWithAppropriatePort(neighborAllPorts, i));
				}
				System.out.println("Sending neighbors");
				for (int i = 0; i < neighbors.size(); i++) {
					ClientAddress neighbor = neighbors.get(i);
					if (neighbor == null) {
						clientOut.println("*");
						clientOut.println("*");
					} else {
						clientOut.println(neighbor.host);
						clientOut.println(neighbor.port);
					}
				}
				clientOut.flush();
				System.out.println("done sending neighbors");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		private ClientAddress neighborWithAppropriatePort(ClientAddressAllPorts neighborAllPorts, int i) {
			if (neighborAllPorts == null) return null;
			String neighborHost = neighborAllPorts.host;
			// port in opposing direction
			int neighborPort = neighborAllPorts.ports.get(NetworkUtils.reverseDir(i));
			return new ClientAddress(neighborHost, neighborPort);
		}
	}
}
