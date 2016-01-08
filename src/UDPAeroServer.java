import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;

public class UDPAeroServer {
	public static void main(String args[]) throws Exception
    {
       DatagramSocket serverSocket = new DatagramSocket(5022);
          byte[] receiveData = new byte[1024];
          byte[] sendData = new byte[1024];
          System.out.println("Hi - its a AeroPlane Server!");
          while(true)
             {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String( receivePacket.getData());
                System.out.println("RECEIVED: " + sentence);
                
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
             }
    }
	
	private void LoadPlaneData()
	{
		FileInputStream fis;
        Properties property = new Properties();
        
		fis = new FileInputStream("src/PlaneData.txt");
        // load config.sys from stream
        property.load(fis);

        uri = property.getProperty("db.uri");
        login = property.getProperty("db.login");
        pass = property.getProperty("db.password");
	}
}
