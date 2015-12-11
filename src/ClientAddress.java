public class ClientAddress {
	public String host;
	public int port;
	
	public ClientAddress(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof ClientAddress && obj != null) {
			ClientAddress other = (ClientAddress) obj;
			return host.equals(other.host) && port == other.port;
		}
		return false;
	}
}
