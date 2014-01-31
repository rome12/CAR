import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class Serveur{
	
		/**
	 * @param args
	 * @throws IOException 
	 */
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ServerSocket ServeurSockerControl=new ServerSocket(2048);
		int portPassiv=3929;
		while (true)
		{
	    Socket socketControl=ServeurSockerControl.accept();
	    portPassiv++;
		FtpRequest s=new FtpRequest(2048,ServeurSockerControl,socketControl,"/home/m1/laraki/",portPassiv);
		s.start();
		System.out.println("Server out ");
		}
	}
		
}

		