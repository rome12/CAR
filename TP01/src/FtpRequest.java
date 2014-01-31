import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class FtpRequest extends Thread {
	
	Socket sock;
	int port;
	String repertoire;
	BufferedReader in;
	Boolean running;

	public FtpRequest(Socket sock, int port, String repertoire){
		this.sock = sock;
		this.port = port;
		this.repertoire = repertoire;
		this.running = true;
	}
	
	public void run(){
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			this.processRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processRequest() throws IOException{

		String messageIn = in.readLine();
		while(running){
			
			switch(messageIn.split(" ")[0]){
			case "USER" :
				this.processUSER(messageIn);
				break;
			case "PASS" :
				this.processPASS(messageIn);
				break;
			case "RETR" :
				this.processRETR(messageIn);
				break;
			case "STOR" :
				this.processSTOR(messageIn);
				break;
			case "LIST" :
				this.processLIST(messageIn);
				break;
			case "QUIT" :
				this.processQUIT(messageIn);
				break;
			default :
				System.out.println("Command not found");
			}
		
		}
		sock.close();
	}
	
	
	

}
