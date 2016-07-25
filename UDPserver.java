/**
 *   UDPserver.java
 *   
 *   @author Ashutosh Katkar
 *   
 *   Its helper class to receive packet from neighboring routers
 */

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UDPserver extends Thread {
	DatagramSocket serverSocket;
	byte[] receiveData;
	byte[] sendData;
	DatagramPacket receivePacket;
	ObjectInputStream inputStream;
	RIPpacket p;
	boolean isLinkFail;
	int port1;

	/**
	 * Constructor
	 * 
	 * @param port
	 * @throws SocketException
	 */
	public UDPserver(int port) throws SocketException {
		port1 = port;
		serverSocket = new DatagramSocket(port);
		sendData = new byte[1024];
		receiveData = new byte[1024];
		isLinkFail = false;
		serverSocket.setSoTimeout(6000);
	}

	/**
	 * tableUPdate - Routing table update after arrival of packet p
	 * @param p
	 */
	public static void tableUpdate(RIPpacket p) {
		int source = Router.IpPOOL.indexOf(p.sourceIP);
		int desti = Router.IpPOOL.indexOf(p.destIP);

		for (int i = 0; i < 4; i++) {
			if (i == source) {
				synchronized (Router.costs) {
					if (Router.costs[i] > p.costs[desti]) {
						Router.costs[i] = p.costs[desti];
						synchronized (Router.nextHop) {
							Router.nextHop[i] = "direct";
						}
						synchronized (Router.lastUpdated) {
							Router.lastUpdated[i] = new SimpleDateFormat(
									"MM/dd/yyyy HH:mm:ss").format(new Date());
						}
						System.out.println("Routing table updated");
					}
				}
			}

			else if (i != desti && i != source) {
				synchronized (Router.dest) {
					if (Router.dest[i] == null) {
						Router.dest[i] = p.dest[i];
					}
				}
				synchronized (Router.costs) {
					if ((Router.costs[i] > p.costs[i] + Router.costs[source])
							&& (!p.nextHop[i].equals(p.destIP))) {
						Router.costs[i] = p.costs[i] + Router.costs[source];
						synchronized (Router.nextHop) {
							Router.nextHop[i] = p.sourceIP;
						}
						synchronized (Router.lastUpdated) {
							Router.lastUpdated[i] = new SimpleDateFormat(
									"MM/dd/yyyy HH:mm:ss").format(new Date());
						}
						System.out.println("Routing table updated");
					} else if ((Router.costs[i] < p.costs[i]
							+ Router.costs[source])
							&& Router.nextHop[i].equals(p.sourceIP)) {
						if (p.costs[i] + Router.costs[source] >= Router.INFINITY) {
							Router.costs[i] = Router.INFINITY;
							synchronized (Router.nextHop) {
								Router.nextHop[i] = "failed";
							}
						} else {
							Router.costs[i] = p.costs[i] + Router.costs[source];
							synchronized (Router.nextHop) {
								Router.nextHop[i] = p.sourceIP;
							}
						}
						synchronized (Router.lastUpdated) {
							Router.lastUpdated[i] = new SimpleDateFormat(
									"MM/dd/yyyy HH:mm:ss").format(new Date());
						}
						System.out.println("Routing table updated");
					}
				}
			}
		}
	}

	/**
	 * sendTriggerUpdate - in case of failure
	 * @param port
	 */
	public static void sendTriggerUpdate(int port) {
		int failId = 0;
		if (Router.nbr.size() == 1) {
			failId = Router.nbr.get(0);

		} else if (Router.nbr.size() == 2) {
			if (Router.ports[1][Router.nbr.get(0)] == port) {
				failId = Router.nbr.get(0);
			} else {
				failId = Router.nbr.get(1);
			}
		}

		if (Router.costs[failId] != Router.INFINITY) {
			triggerUpdate(failId);
			for (int i = 0; i < 4; i++) {
				if (Router.nextHop[i] == Router.IpPOOL.get(failId)) {
					triggerUpdate(i);
				}
			}
			System.out.println("\n Trigger Update - LINK Failed");
		}
	}

	/**
	 * triggerUpadate - to update own Routing table
	 * @param failId
	 */
	public static void triggerUpdate(int failId) {
		synchronized (Router.costs) {
			Router.costs[failId] = Router.INFINITY;
		}
		synchronized (Router.nextHop) {
			Router.nextHop[failId] = "failed";
		}
		synchronized (Router.lastUpdated) {
			Router.lastUpdated[failId] = new SimpleDateFormat(
					"MM/dd/yyyy HH:mm:ss").format(new Date());
		}
	}

	public void run() {

		while (true) {
			try {
				receivePacket = new DatagramPacket(receiveData,
						receiveData.length);

				try {
					serverSocket.receive(receivePacket);
				} catch (SocketTimeoutException e) {
					isLinkFail = true;
				}
				inputStream = new ObjectInputStream(new ByteArrayInputStream(
						receiveData));
				p = (RIPpacket) inputStream.readObject();
				if (isLinkFail) {
					sendTriggerUpdate(port1);
					isLinkFail = false;
				} else {
					tableUpdate(p);
				}
			} catch (Exception e) {
				
			}
		}
	}

}