/**
 *   UDPclient.java 
 *   
 *   @author Ashutosh katkar
 *   
 *   Its helper class to send packet to other routers
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPclient extends Thread {
	DatagramSocket clientSocket;
	byte[] receiveData;
	byte[] sendData;
	InetAddress IPAddress;
	int port;
	RIPpacket p;
	ByteArrayOutputStream out;
	ObjectOutputStream outputStream;
	String destIP;
	String sourceIP;

	/**
	 * Constructor
	 * 
	 * @param sourceIP
	 * @param destIP
	 * @param port
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public UDPclient(String sourceIP, String destIP, int port)
			throws SocketException, UnknownHostException {
		clientSocket = new DatagramSocket();
		sendData = new byte[1024];
		receiveData = new byte[1024];
		this.destIP = destIP;
		this.sourceIP = sourceIP;
		IPAddress = InetAddress.getByName(destIP);
		this.port = port;
	}

	/**
	 *  sendPacket - to send packet
	 */
	public void sendPacket() {
		try {
			p = new RIPpacket(sourceIP, destIP);
			out = new ByteArrayOutputStream();
			outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(p);
			outputStream.close();
			sendData = out.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				sendPacket();
				sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
