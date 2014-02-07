package ftp;

import ftp.tools.DataTransferManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import ftp.tools.DirectoryNavigator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpRequest extends Thread {

    private Socket sock;
    private int port;
    private int data_port;
    private String repertoire;
    private BufferedReader in;
    private static DataOutputStream out;
    private static DataOutputStream data_out;
    private Boolean running;
    private DirectoryNavigator directory;
    private String user;
    private String mdp;

    public FtpRequest(Socket sock, int port, String repertoire, int dport) {
        this.sock = sock;

        this.port = port;

        this.repertoire = repertoire;
        this.running = true;
        this.data_port = dport;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new DataOutputStream(sock.getOutputStream());
            this.processRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataTransferManager init_data_transfert() {
        DataTransferManager dtm = new DataTransferManager(this.data_port);
        dtm.initiate_transfer("127.0.0.1");
        dtm.initiate_data_output();
        return dtm;
    }

    public void processRequest() throws IOException {
        this.directory = new DirectoryNavigator(this.repertoire);
        this.respond(220, "Welcome on our server!");
        Serveur.printout("Attente d'une commande");

        String messageIn = in.readLine();

        while (running) {
            Serveur.printout("Commande reçu :" + messageIn);
            //Ce systeme pour Java < 7
            if (messageIn != null) {
                String[] parts = messageIn.split(" ");
                if (parts.length > 0) {
                    String tmp = parts[0].toUpperCase();
                    if (tmp.equals("USER")) {
                        this.processUSER(messageIn);
                    } else if (tmp.equals("PASS")) {
                        this.processPASS(messageIn);
                    } else if (tmp.equals("RETR")) {
                        this.processRETR(messageIn);
                    } else if (tmp.equals("STOR")) {
                        this.processSTOR(messageIn);
                    } else if (tmp.equals("LIST")) {
                        this.processLIST(messageIn);
                    } else if ("PWD".equals(tmp)) {
                        this.processPWD(messageIn);
                    } else if ("CWD".equals(tmp)) {
                        this.processCWD(messageIn);
                    } else if (tmp.equals("CDUP")) {
                        this.processCDUP(messageIn);
                    } else if (tmp.equals("QUIT")) {
                        this.processQUIT(messageIn);
                        break;
                    } else {
                        this.respond(200, "Command not implemented");
                    }
                }
            }
            // Serveur.printout("Attente d'une commande");
            messageIn = in.readLine();
        }
        sock.close();
    }

    public void transmit(String s) throws IOException {
        DataTransferManager dtm = init_data_transfert();
        dtm.transmit(s);
        dtm.close();
    }

    public void multiple_transmit(String[] transmit) throws IOException {
        DataTransferManager dtm = init_data_transfert();
        for (String s : transmit) {
            dtm.transmit(s);
        }
        dtm.close();

    }

    public static void respond(int code, String information) {
        try {
            out.writeBytes(Integer.toString(code) + " " + information + "\n");
            Serveur.printout(Integer.toString(code) + " " + information);

        } catch (IOException ex) {
            System.err.append("fail in response");

        }
    }

    public static void multiple_respond(int code, String[] information) {
        try {
            for (int i = 0; i < information.length; i++) {
                out.writeBytes(Integer.toString(code) + " " + information[i] + "\n");
                Serveur.printout(Integer.toString(code) + " " + information[i]);
            }
        } catch (IOException ex) {
            System.err.append("fail in response");

        }
    }

    public void processUSER(String messageIn) {
    	 Serveur.printout("Methode processUSER");
         String[] parts = messageIn.split(" ");
         try {
             if (parts.length == 2) {
            	 user = parts[1];
                 respond(331, "User name okay, need password.");
             } else {
            	 respond(503, "Bad sequence of commands.");
             }
         } catch (Exception ex) {}
    }

    public void processPASS(String messageIn) throws IOException {
    	Serveur.printout("Methode processPASS");
        String[] parts = messageIn.split(" ");
        try {
            if (parts.length == 2) {
	        mdp = parts[1];
	  		String chaine1="";
	  		String chaine2="";
	  		boolean bool=true;
	
	
	  		BufferedReader brr=new BufferedReader(new InputStreamReader(new FileInputStream("tabledesmdp.txt")));
	  		String ligne;
	 			while ((ligne=brr.readLine())!=null){
	 				if (bool){
	 					chaine2="";
	 					chaine1=ligne+"\n";
	 					bool=!bool;
	 				}
	 				else {
	 					chaine2=ligne+"\n";
	 					bool=!bool;
	 				}
	 				if ((this.user+"\n").equals(chaine1) && (this.mdp+"\n").equals(chaine2)) {
	 					respond(230, "User logged in, proceed. Logged out if appropriate.");
	 					brr.close(); 
	 					return;
	 				}
	 			}
	  		respond(530,"Not logged in.");
	  		brr.close(); 
            }  else {
           	 respond(503, "Bad sequence of commands.");
            }
            }catch (Exception ex) {}

    }

    public void processRETR(String messageIn) {
        respond(200, "OK");
    }

    public void processSTOR(String messageIn) {
        respond(200, "OK");
    }

    public void processLIST(String messageIn) {
        respond(150, "Here comes the directory listing.");
        String[] parts = messageIn.split(" ");

        try {

            if (parts.length >= 2) {
                for (int i = 1; i < parts.length; i++) {
                    multiple_transmit(directory.list_working_directory(parts[i]));
                    respond(226, "Directory send OK.");
                }

            } else {
                multiple_transmit(directory.list_working_directory());
                respond(226, "Directory send OK.");

            }
        } catch (Exception ex) {
            Logger.getLogger(FtpRequest.class.getName()).log(Level.SEVERE, null, ex);
            respond(550, "folder not found.");
        }
    }

    public void processPWD(String messageIn) {
        this.respond(257, "\"" + directory.get_working_directory() + "\" is your current location");
    }

    public void processCWD(String messageIn) {
        String[] parts = messageIn.split(" ");
        try {
            if (parts.length >= 2) {

                directory.change_working_directory(parts[1]);
                this.respond(250, "OK. Current directory is " + directory.get_working_directory());


            } else {

                directory.change_working_directory("/");
                this.respond(250, "OK. Current directory is " + directory.get_working_directory());
            }
        } catch (Exception ex) {
            this.respond(550, "Can't change directory to aaa: No such file or directory.");
            try {
                directory.change_working_directory("/");
                this.respond(250, "OK. Current directory is " + directory.get_working_directory());
            } catch (Exception ex1) {
            }


        }
    }

    public void processCDUP(String messageIn) {
        try {
            directory.change_working_directory("../");
        } catch (Exception ex) {
            try {
                directory.change_working_directory("/");
            } catch (Exception ex1) {
            }
        }
        this.respond(250, "OK. Current directory is " + directory.get_working_directory());

    }

    public void processQUIT(String messageIn) {
        this.multiple_respond(221, new String[]{"Goodbye.", "quit"});
        Serveur.printout("Connexion fermée par l'utilisateur");
        running = false;
    }
}
