import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPAeroClient {

	DatagramSocket clientSocket = null;
	// choose ip - its a localhost or (127.0.0.1)
	InetAddress IPAddress = InetAddress.getByName("localhost");
	// create variable for the data send and receive
	byte[] sendData = new byte[1024];
	byte[] receiveData = new byte[1024];
	
	public UDPAeroClient() throws IOException {
		System.out.println("Start Client - print help to get all avaiiable commands");
		initClient();
	}

	private void SendToServer(String sentence) {

		try {
			// create new connect for the UDP connect
			if (clientSocket==null)
				clientSocket = new DatagramSocket();

				sendData = new byte[1024];
				receiveData = new byte[1024];
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
				System.out.println("FROM SERVER:\n" + modifiedSentence);

		} catch (IOException e) {
			e.printStackTrace();
			if (clientSocket != null)
				clientSocket.close();
		} 

	}

	public void initClient() {
		String sentence;

		try {

			while (true) {
				// read console's input
				System.out.println("Enter command or weight:");
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String message = inFromUser.readLine().trim();
				if (message.contentEquals("-change"))
				{
					message = "/ch ";
					System.out.println("Enter number a plane");
					inFromUser = new BufferedReader(new InputStreamReader(System.in));
					String tmpStr = inFromUser.readLine();
					message +=tmpStr+"\n";
					
					System.out.println("Enter new name of the plane");
					inFromUser = new BufferedReader(new InputStreamReader(System.in));
					tmpStr = inFromUser.readLine();
					message +=tmpStr+"\n";
					
					System.out.println("Enter min weight of the plane");
					inFromUser = new BufferedReader(new InputStreamReader(System.in));
					tmpStr = inFromUser.readLine();
					message +=tmpStr+"\n";
					
					System.out.println("Enter max weight of the plane");
					inFromUser = new BufferedReader(new InputStreamReader(System.in));
					tmpStr = inFromUser.readLine();
					message +=tmpStr+"\n";
				}
				
				if (message.contentEquals("-help"))
				{
					System.out.println("Digits (0 - 99)  - Server will the name of the plane, which can handle that weight");
					System.out.println("-status  - Server will return all awailable planes");

					System.out.println("-change - start to change plane's information. Enter a name, min weigth and max weight");
					System.out.println("Example: 2 \n Boeng2002 \n 0 \n 40");
					
				}
				else
					SendToServer(message);
			}

		}
		catch(Exception e){ e.printStackTrace();}
		finally {
			if (clientSocket != null)
			clientSocket.close();
		}

	}

	public static void main(String args[]) throws Exception {
		new UDPAeroClient();
	}
}
