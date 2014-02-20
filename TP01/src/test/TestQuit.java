package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestQuit {
	public static FtpClient ftpClient;
	
	@BeforeClass
	public static void startClient(){
		ftpClient = new FtpClient(7000);
		
		assertEquals(ftpClient.receive(), "220 Welcome on our server!");
		ftpClient.send("USER login");
		assertEquals(ftpClient.receive(), "331 User name okay, need password.");
		ftpClient.send("PASS mdp");
		assertEquals(ftpClient.receive(), "230 User logged in, proceed. Logged out if appropriate.");
	}
	
	@Test
	public void executeQuit() {
		ftpClient.send("QUIT");
		assertEquals(ftpClient.receive(), "221 Goodbye.");
		assertEquals(ftpClient.receive(), "221 quit");
	}
	
}