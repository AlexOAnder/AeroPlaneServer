import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPAeroClient {

	public UDPAeroClient() throws IOException {
		System.out.println("Start Client");
		initClient();
	}

	public void initClient() {
		String sentence;
		DatagramSocket clientSocket = null;
		try {

		// create new connect for the UDP connect
		clientSocket = new DatagramSocket();
		
		// choose ip - its a localhost or (127.0.0.1)
		InetAddress IPAddress = InetAddress.getByName("localhost");
		// create variable for the data send and receive
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		
		while (true){
			sendData = new byte[1024];
		// read console's input
			System.out.println("Enter new weight:");
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			sentence = inFromUser.readLine();
			
			sendData = sentence.getBytes();
			// choose 5022 socket because we have launched server here
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5022);
			// send
			clientSocket.send(sendPacket);
			
			// receive
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String modifiedSentence = new String(receivePacket.getData());
			
			// out
			System.out.println("FROM SERVER:" + modifiedSentence);
		}
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (clientSocket!=null)
				clientSocket.close();
		}
		
	}

	public static void main(String args[]) throws Exception {
		new UDPAeroClient();
	}
}
