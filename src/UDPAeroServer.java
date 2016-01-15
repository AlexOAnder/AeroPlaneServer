import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class UDPAeroServer {
	DatagramSocket serverSocket = null;
	String delimiter = "#";
	public List<Plane> planes = new ArrayList<Plane>();

	public UDPAeroServer() {
		try {
			LoadPlaneData();
			initUDP();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
					if (ChangePlanesFile(sentence) == 0)
						answer = "Data changed!";
					LoadPlaneData(); // after data change, we need to reload our planesList
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

	private int WriteToFile(Plane pl, int number) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("src/PlaneData.txt"));

			String line;
			List<String> lines = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			int iterator = 0;
			int countOfDelimiters = 0;
			int startErasePos = 0;

			if (number == 1) {
				startErasePos = 0;
			} else {
				for (String tmp : lines) {
					if (delimiter.equals(tmp)) {
						countOfDelimiters++;
					}
					if (countOfDelimiters == number-1)
						{
							startErasePos = iterator+1;
							break;
						}

					iterator++;
				}
			}

			lines.remove(startErasePos);
			lines.remove(startErasePos);
			lines.remove(startErasePos);
			// change lines

			lines.add(startErasePos, pl.Name);
			lines.add(startErasePos + 1, Integer.toString(pl.MinWeight));
			lines.add(startErasePos + 2, Integer.toString(pl.MaxWeight));
			// open file for the change
			BufferedWriter writer = new BufferedWriter(new FileWriter("src/PlaneData.txt"));
			writer.flush();
			for (String tmp : lines) {
				writer.write(tmp);
				writer.write("\n");
			}
			writer.close();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return -1; // -1 mean error;
		}
	}

	private int ChangePlanesFile(String line) {
		Plane chgPlane = new Plane();
		try {
			int number = 0;
			int i = line.indexOf('\n');
			String s = line.substring(4, i);
			line = line.substring(i + 1);
			System.out.println("Num->" + s);

			// find a number
			number = Integer.parseInt(s);
			if (number < 0 || number > 3)
				return -1;

			int j = 0;
			while (true) {
				j++;
				int i1 = line.indexOf('\n');
				if (i1 < 0) {
					i1 = line.length();
					String s2 = line.substring(0, i1);
					chgPlane.MaxWeight = Integer.parseInt(s2.trim());
					System.out.println("s" + j + "->" + s2);
					break;
				}
				String s1 = line.substring(0, i1);
				if (j == 1)
					chgPlane.Name = s1.trim();
				if (j == 2)
					chgPlane.MinWeight = Integer.parseInt(s1.trim());
				line = line.substring(i1 + 1);
				System.out.println("s" + j + "->" + s1);

				if (line.length() <= 1)
					break;
			}

			// 0 mean success, -1 -> error
			return WriteToFile(chgPlane, number);

		} catch (NumberFormatException ex) {
			return -1;
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
		reader.close();
		int counter = 0;
		List<Plane> tmpPlanes = new ArrayList<Plane>();
		Plane pl = new Plane();

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
