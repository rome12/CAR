package test;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestAuthentification {
	public static FtpClient ftpClient;
	
	@Test
	public void goodAuthentification() {
		ftpClient = new FtpClient(7000);
		assertEquals(ftpClient.receive(), "220 Welcome on our server!");
		ftpClient.send("USER login");
		assertEquals(ftpClient.receive(), "331 User name okay, need password.");
		ftpClient.send("PASS mdp");
		assertEquals(ftpClient.receive(), "230 User logged in, proceed. Logged out if appropriate.");
		ftpClient.send("QUIT");
	}
	
	@Test
	public void badAuthentificationWrongUser() {
		ftpClient = new FtpClient(7000);
		assertEquals(ftpClient.receive(), "220 Welcome on our server!");
		ftpClient.send("USER toto");
		assertEquals(ftpClient.receive(), "331 User name okay, need password.");
		ftpClient.send("PASS mdp");
		assertEquals(ftpClient.receive(), "530 Not logged in.");
		ftpClient.send("QUIT");
	}
	
	@Test
	public void badAuthentificationWrongPass() {
		ftpClient = new FtpClient(7000);
		assertEquals(ftpClient.receive(), "220 Welcome on our server!");
		ftpClient.send("USER login");
		assertEquals(ftpClient.receive(), "331 User name okay, need password.");
		ftpClient.send("PASS toto");
		assertEquals(ftpClient.receive(), "530 Not logged in.");	
		ftpClient.send("QUIT");
	}	
}