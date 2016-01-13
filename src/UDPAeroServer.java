import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UDPAeroServer {
	DatagramSocket serverSocket = null;
	public List<Plane> planes = new ArrayList<Plane>();

	public UDPAeroServer() {
		try {
			LoadPlaneData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(FindPlane(30));
		initUDP();
	}

	private void initUDP() {
		try {
			serverSocket = new DatagramSocket(5022);

			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			System.out.println("Hi - its a AeroPlane Server!");
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				serverSocket.receive(receivePacket);

				String sentence = new String(receivePacket.getData());
				System.out.println("RECEIVED: " + sentence);

				String answer = "Something Wrong";
				int weight = 0;
				try {
					weight = Integer.parseInt(sentence.trim());
					answer = FindPlane(weight);
				} catch (NumberFormatException ex) {
					answer = "Wrong int - try to send int, please";
				}

				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();

				String capitalizedSentence = answer.toUpperCase();
				sendData = capitalizedSentence.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (serverSocket != null)
				serverSocket.close();
		}
	}

	public static void main(String args[]) {

		new UDPAeroServer();

	}

	private String FindPlane(int weight) {
		if (!planes.isEmpty())
			for (Plane pl : planes) {
				if (pl.MinWeight <= weight && pl.MaxWeight > weight) {
					return pl.Name;
				}
			}
		return "NotAvailable";
	}

	private void LoadPlaneData() throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader("src/PlaneData.txt"));
		String line;
		List<String> lines = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		int counter = 0;
		List<Plane> tmpPlanes = new ArrayList<Plane>();
		Plane pl = new Plane();
		String delimiter = "#";
		for (String tmp : lines) {

			if (!delimiter.equals(tmp)) {
				counter++;
				switch (counter) {
				case 1: {
					pl.Name = tmp;
					break;
				}
				case 2: {
					pl.MinWeight = Integer.parseInt(tmp);
					break;
				}
				case 3: {
					pl.MaxWeight = Integer.parseInt(tmp);
					break;
				}
				}
			} else {
				counter = 0;
				tmpPlanes.add(pl);
				pl = new Plane();
			}
		}

		for (Plane p : tmpPlanes) {
			System.out.println("Name:" + p.Name + " Min: " + p.MinWeight + " Max: " + p.MaxWeight + " ");
		}
		planes = tmpPlanes;
	}

}
