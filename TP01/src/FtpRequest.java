import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class FtpRequest extends Thread {
	
	Socket sock;
	int port;
	String repertoire;
	BufferedReader in;
	DataOutputStream out;
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
			out = new DataOutputStream(sock.getOutputStream());
			this.processRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processRequest() throws IOException{

		Serveur.printout("Attente d'une commande");
		String messageIn = in.readLine();
		while(running){
			Serveur.printout("Commande reçu :"+messageIn);
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
			case "PWD" :
				this.processPWD(messageIn);
				break;
			case "CWD" :
				this.processCWD(messageIn);
				break;
			case "CDUP" :
				this.processCDUP(messageIn);
				break;
			case "QUIT" :
				this.processQUIT(messageIn);
				break;
			default :
				out.writeBytes("502 Command not implemented \n");
			}
			Serveur.printout("Attente d'une commande");
			messageIn = in.readLine();
		}
		sock.close();
	}
	
	public void processUSER(String messageIn){
		Serveur.printout("Methode processUSER");
	}
	
	public void processPASS(String messageIn){
		Serveur.printout("Methode processPASS");
	}

	public void processRETR(String messageIn){
	
	}
	
	public void processSTOR(String messageIn){
		
	}
	
	public void processLIST(String messageIn){
		
	}
	
	public void processPWD(String messageIn){
		
	}

	public void processCWD(String messageIn){
		
	}
	
	public void processCDUP(String messageIn){
		
	}
	
	public void processQUIT(String messageIn){
		running = false;
	}
	
	
	

}
