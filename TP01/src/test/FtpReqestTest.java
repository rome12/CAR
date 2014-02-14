package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



import junit.framework.TestCase;

import ftp.*;


import org.junit.Test;

public class FtpReqestTest extends TestCase {

	   private FtpRequest ftp;
	
   public FtpReqestTest(String name) {
	   super(name);
	  }
	   
   protected void setUp() throws Exception {
	   super.setUp();
	   Socket sock;
       try {
           sock = (new ServerSocket(Serveur.port)).accept();
           ftp = new FtpRequest(sock, Serveur.port, "\\Documents", Serveur.dataport);
           ftp.start();
      
       } catch (IOException e) {
           e.printStackTrace();
       }
	   }
	    
   protected void tearDown() throws Exception {
	   super.tearDown();
	   ftp = null;
	   }
   
	@Test
	public void testRun() {
		ftp.run();
		assertEquals(ftp.getState(),java.lang.Thread.State.RUNNABLE);
	}

	@Test
	public void testFtpRequest() {
		assertNotNull("L'instance est créée", ftp);
	}

	@Test
	public void testProcessRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testMultiple_respond() {
		fail("Not yet implemented");
	}

	@Test
	public void testConnect_data() {
		fail("Not yet implemented");
	}

	@Test
	public void testClose_data() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessUSER() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessPASS() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessRETR() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessSTOR() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessLIST() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessPWD() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessCWD() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessCDUP() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessQUIT() {
		fail("Not yet implemented");
	}

}
