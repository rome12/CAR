package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestNavigation {
	public static FtpClient ftpClient;

	@BeforeClass
	public static void startClient() {
		ftpClient = new FtpClient(7000);
		
		assertEquals(ftpClient.receive(), "220 Welcome on our server!");
		ftpClient.send("USER login");
		assertEquals(ftpClient.receive(), "331 User name okay, need password.");
		ftpClient.send("PASS mdp");
		assertEquals(ftpClient.receive(), "230 User logged in, proceed. Logged out if appropriate.");

	}

	@Test
	public void goodNavigationDirectory() {

		ftpClient.send("PWD");
		assertEquals(ftpClient.receive(), "257 \"/\" is your current location");
		ftpClient.send("CWD dossier1");
		assertEquals(ftpClient.receive(),
				"250 OK. Current directory is /dossier1/");
		ftpClient.send("PWD");
		assertEquals(ftpClient.receive(),
				"257 \"/dossier1/\" is your current location");
		ftpClient.send("CDUP");
		assertEquals(ftpClient.receive(), "250 OK. Current directory is /");
	}

	@Test
	public void notAllowToAccess() {
		ftpClient.send("PWD");
		assertEquals(ftpClient.receive(), "257 \"/\" is your current location");
		ftpClient.send("CDUP");
		assertEquals(ftpClient.receive(), "250 OK. Current directory is /");
	}

	@Test
	public void wrongDirectory() {
		ftpClient.send("PWD");
		assertEquals(ftpClient.receive(), "257 \"/\" is your current location");
		ftpClient.send("CWD test");
		assertEquals(ftpClient.receive(), "550 OK. Folder not found : /");
	}
}