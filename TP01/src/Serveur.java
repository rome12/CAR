import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Serveur {

	
	public static void main(String[] args) {
		ServerSocket servSocket; 
		Socket sock;
		int port = 7000;
		if(args.length != 1){
			System.out.println("Usage : serveur (repertoire)");
		}
		
		try {
			servSocket = new ServerSocket(port);
			String repertoire = args[0];
			while(true){
				sock = servSocket.accept();
				System.out.println("Connection détectée");
				FtpRequest ftpRequest = new FtpRequest(sock,port,repertoire);
				ftpRequest.start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
