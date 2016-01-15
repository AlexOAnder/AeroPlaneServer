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
		// System.out.println(FindPlane(30));
		initUDP();
	}

	private void initUDP() {
		try {
			serverSocket = new DatagramSocket(5022);

			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			System.out.println("Hi - its a AeroPlane Server!");

			while (true) {
				receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				sentence = sentence.trim();
				System.out.println("RECEIVED: " + sentence);

				String answer = "Something Wrong";
				int weight = 0;

				try {
					weight = Integer.parseInt(sentence.trim());
					answer = FindPlane(weight);

				} catch (NumberFormatException ex) {
					answer = "Wrong int - try to send int, please";
				}

				if (sentence.contains("/ch")) {
					if (ParsePlane(sentence)==1)
						answer = "Data changed!";
				}
				if (sentence.contentEquals("status")) {
					answer = ShowStatus();
				}

				System.out.println("Answer->" + answer);
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

	private void ChangeFile(Plane pl,int number) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("src/PlaneData.txt"));
		String line;
		List<String> lines = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
	}
	
	private int ParsePlane(String line) {
		try {
			int number = 0;
			int i = line.indexOf('\n');
			String s = line.substring(4, i);
			line = line.substring(i + 1);
			System.out.println("Num->" + s);
			
			// find a number
			number = Integer.parseInt(s);
			if (number<0 || number>3) return 0;
			
			int j = 0;
			while (true) {
				j++;
				int i1 = line.indexOf('\n');
				if (i1 < 0) {
					i1 = line.length();
					String s2 = line.substring(0, i1);
					System.out.println("s" + j + "->" + s2);
					break;
				}
				String s1 = line.substring(0, i1);
				line = line.substring(i1 + 1);
				System.out.println("s" + j + "->" + s1);
				
				if (line.length() <= 1) break;
			}
			return 1;
		} catch (NumberFormatException ex) {
			return 0;
		}

	}

	private String FindPlane(int weight) {
		try {
			if (weight == 0) {
				return "Nothing to carry - zero weight";
			}
			if (!planes.isEmpty())
				for (Plane pl : planes) {
					if (pl.MinWeight <= weight && pl.MaxWeight > weight) {
						return pl.Name;
					}
				}
			return "NotAvailable";
		} catch (Exception e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}

	private String ShowStatus() {
		String res = "";
		for (Plane p : planes) {
			res += "Name:" + p.Name + " Min: " + p.MinWeight + " Max: " + p.MaxWeight + " \n ";
		}
		return res;
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
