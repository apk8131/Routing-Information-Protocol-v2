/**
 *  Title : Simulation of RIP v2 Protocol using UDP socket programming. 
 *  
 *  Description : Each router on the network executes RIP to exchange routing information with its neighbors, and based
 *                on this information, the router computes the shortest paths from itself to all the other routers
 *   
 *  @author Ashutosh Katkar
 *  
 *  Version 1.3
 *  
 *  Router.java - main file
 *  
 */

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Router {
	static final int GLADOS = 0;
	static final int QUEEG = 1;
	static final int COMET = 2;
	static final int RHEA = 3;
	static final int INFINITY = 999;
	static final String IPPOOL[] = { "129.21.22.196", "129.21.30.37",
			"129.21.34.80", "129.21.37.49" };
	static final ArrayList<String> IpPOOL = new ArrayList<String>();
	static final String subnetmask = "255.255.255.0";
	static String routerName = "";
	static String routerIP = "";
	static ArrayList<Integer> nbr = new ArrayList<Integer>();
	static int costs[] = { INFINITY, INFINITY, INFINITY, INFINITY };
	static String[] dest = new String[4];
	static String[] nextHop = { "NA", "NA", "NA", "NA" };
	static String[] lastUpdated = new String[4];
	static int[][] ports = new int[2][4];

	/**
	 *  init - for initial setup
	 */
	public static void init() {
		IpPOOL.add("129.21.22.196");
		IpPOOL.add("129.21.30.37");
		IpPOOL.add("129.21.34.80");
		IpPOOL.add("129.21.37.49");
		costs[IpPOOL.indexOf(routerIP)] = 0;
	}

	/**
	 * setNbr - to set information about neighbors
	 * @param routerName 
	 * @param cost
	 * @param sPort
	 * @param rPort
	 */
	public static void setNbr(String routerName, int cost, int sPort, int rPort) {
		int router = 99;
		switch (routerName) {
		case "glados":
			router = GLADOS;
			break;
		case "queeg":
			router = QUEEG;
			break;
		case "comet":
			router = COMET;
			break;
		case "rhea":
			router = RHEA;
			break;
		default:
			System.out.println("invalid router");
			System.exit(0);
		}
		nbr.add(router);
		costs[router] = cost;
		dest[router] = findNetAdd(IPPOOL[router]);
		nextHop[router] = "direct";
		ports[0][router] = sPort;
		ports[1][router] = rPort;
	}

	/**
	 * findNetAdd - to find network address using subnetmask
	 * @param IPadd
	 * @return - net address
	 */
	public static String findNetAdd(String IPadd) {
		String IPoctet[] = IPadd.split("\\.");
		String maskOctet[] = subnetmask.split("\\.");
		String netAdd = "";
		for (int i = 0; i < 4; i++) {
			int temp = Integer.parseInt(IPoctet[i])
					& Integer.parseInt(maskOctet[i]);
			if (i == 3) {
				netAdd = netAdd + Integer.toString(temp);
			} else {
				netAdd = netAdd + Integer.toString(temp) + ".";
			}
		}
		return netAdd;
	}

	public static void main(String[] args) throws SocketException,
			UnknownHostException {
		routerName = args[0];
		routerIP = args[1];
		init();
		Scanner user_input = new Scanner(System.in);
		System.out.println("Enter number of neighbors: ");
		int noOfrouters = user_input.nextInt();
		UDPserver[] s = new UDPserver[2];
		UDPclient[] c = new UDPclient[2];
		int j = 0;
		while (j < noOfrouters) {
			System.out
					.println("Enter Router_name link_cost send_port recieve_port");
			String router_name = user_input.next();
			int link_cost = user_input.nextInt();
			int send_port = user_input.nextInt();
			int recieve_port = user_input.nextInt();
			setNbr(router_name, link_cost, send_port, recieve_port);
			System.out.println("neighbor added");
			j++;
		}

		for (int i = 0; i < noOfrouters; i++) {
			s[i] = new UDPserver(ports[1][nbr.get(i)]);
			c[i] = new UDPclient(routerIP, IPPOOL[nbr.get(i)],
					ports[0][nbr.get(i)]);
			c[i].start();
			s[i].start();
		}

		int tempCosts[];
		String tempNextHop[];
		String tempDest[];
		Random rand = new Random();
		int time = rand.nextInt(Integer.parseInt(args[2])) + 5;
		int choose = 0;
		if (nbr.size() == 2) {
			choose = rand.nextInt(2);
		}
		int newCost = 1;
		try {
		    newCost = rand.nextInt(costs[nbr.get(choose)]);
	  } catch (Exception e) {
	     newCost = 1;
	  }
		if (newCost == 0) {
			newCost = 1;
		}
		System.out.println("\n\n Cost of " + IpPOOL.get(nbr.get(choose))
				+ " will change to " + newCost + " after " + time * 5
				+ " seconds");

		while (true) {
			if (time == 0) {

				synchronized (costs) {
					costs[nbr.get(choose)] = newCost;
				}
				synchronized (Router.lastUpdated) {
					Router.lastUpdated[nbr.get(choose)] = new SimpleDateFormat(
							"MM/dd/yyyy HH:mm:ss").format(new Date());
				}
				System.out.println("\n\n Cost of "
						+ IpPOOL.get(nbr.get(choose)) + " changed to "
						+ newCost + "\n");
			}

			System.out.println("\n \n Routing table for " + routerName);
			System.out.println("\n    Destination  " + "   subnet mask   "
					+ "   next hop    " + "cost " + "  last updated ");
			synchronized (costs) {
				tempCosts = costs;
			}
			synchronized (nextHop) {
				tempNextHop = nextHop;
			}
			synchronized (dest) {
				tempDest = dest;
			}
			for (int i = 0; i < 4; i++) {
				if (dest[i] != null)
					System.out.format("%15s%15s%15s%7s%20s", tempDest[i],
							subnetmask, tempNextHop[i], tempCosts[i],
							lastUpdated[i]);
				System.out.println();
			}

			time--;

			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
