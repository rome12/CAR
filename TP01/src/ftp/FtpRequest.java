package ftp;

//import ftp.tools.DataTransferManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import ftp.tools.DirectoryNavigator;

public class FtpRequest extends Thread {

	private Socket sock;
	private Socket data_sock;
	private ServerSocket data_server_sock;
	private int data_port_actif;
	private int data_port_passif;
	private InetAddress data_adress;
	private String repertoire;
	private BufferedReader in;
	private DataOutputStream out;
	private DataOutputStream data_out;
	private Boolean running;
	private DirectoryNavigator directory;
	private String user;
	private String mdp;
	private Boolean passive_mode = false;

	public FtpRequest(Socket sock, String repertoire, int data_port_passif) {
		this.sock = sock;
		this.repertoire = repertoire;
		this.running = true;
		this.data_port_passif = data_port_passif;
	}

	public void run() {
		try {
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new DataOutputStream(sock.getOutputStream());
			this.processRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processRequest() throws IOException {
		this.directory = new DirectoryNavigator(this.repertoire);
		this.respond(220, "Welcome on our server!");
		Serveur.printout("Attente d'une commande");

		String messageIn = in.readLine();

		while (running && messageIn != null) {
			Serveur.printout("Commande recu :" + messageIn);
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
					} else if ("PASV".equals(tmp)) {
						this.processPASV(messageIn);
					} else if ("PORT".equals(tmp)) {
						this.processPORT(messageIn);
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
		Serveur.printout("Client deconnecte");
		sock.close();
	}

	public void respond(int code, String information) {
		try {
			out.writeBytes(Integer.toString(code) + " " + information + "\n");
			Serveur.printout(Integer.toString(code) + " " + information);

		} catch (IOException ex) {
			System.err.append("fail in response");

		}
	}

	public void multiple_respond(int code, String[] information) {
		try {
			for (int i = 0; i < information.length; i++) {
				out.writeBytes(Integer.toString(code) + " " + information[i]
						+ "\n");
				Serveur.printout(Integer.toString(code) + " " + information[i]);
			}
		} catch (IOException ex) {
			System.err.append("fail in response");

		}
	}

	public void connect_data() throws IOException {
		try {
			if (passive_mode) {
				data_sock = data_server_sock.accept();
			} else {
				data_sock = new Socket(data_adress, data_port_actif);
			}
			data_out = new DataOutputStream(data_sock.getOutputStream());
		} catch (BindException e) {
			if (close_data()) {
				connect_data();
			}
		}
	}

	public Boolean close_data() {
		try {
			data_sock.close();
			data_out.close();
			if (passive_mode)
				data_server_sock.close();
			return true;
		} catch (IOException e) {
			// la connexion est déjà fermée, pourquoi s'acharner?
			return false;
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
		} catch (Exception ex) {
		}
	}

	public void processPASS(String messageIn) throws IOException {
		Serveur.printout("Methode processPASS");
		String[] parts = messageIn.split(" ");
		try {
			if (parts.length == 2) {
				mdp = parts[1];
				String chaine1 = "";
				String chaine2 = "";
				boolean bool = true;

				BufferedReader brr = new BufferedReader(new InputStreamReader(
						new FileInputStream("login_mdp.txt")));
				String ligne;
				while ((ligne = brr.readLine()) != null) {
					if (bool) {
						chaine2 = "";
						chaine1 = ligne + "\n";
						bool = !bool;
					} else {
						chaine2 = ligne + "\n";
						bool = !bool;
					}
					if ((this.user + "\n").equals(chaine1)
							&& (this.mdp + "\n").equals(chaine2)) {
						respond(230,
								"User logged in, proceed. Logged out if appropriate.");
						brr.close();
						return;
					}
				}
				respond(530, "Not logged in.");
				brr.close();
			} else {
				respond(503, "Bad sequence of commands.");
			}
		} catch (Exception ex) {
		}

	}

	public void processRETR(String messageIn) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(
					directory.calculate_absolute_path(messageIn.substring(5)));
			this.connect_data();
			respond(150, "Accept RETR command");
			int c;

			while ((c = fis.read()) != -1) {
				data_out.write(c);
			}

			data_out.flush();
			this.close_data();
			fis.close();
			respond(226, "Transfert fichier OK");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void processSTOR(String messageIn) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(
					directory.calculate_absolute_path(messageIn.substring(5)));
			this.connect_data();
			respond(150, "Accept STOR command");
			InputStream in = data_sock.getInputStream();
			int c;
			while ((c = in.read()) != -1) {
				fos.write(c);
			}
			fos.flush();
			fos.close();
			this.close_data();
			respond(226, "Transfert fichier OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processLIST(String messageIn) {
		respond(150, "Here comes the directory listing.");
		String[] parts = messageIn.split(" ");
		try {
			this.connect_data();
		} catch (IOException e) {
			this.respond(425, "unable to open data connexion");
			return;
		}

		if (parts.length >= 2) {
			for (int i = 1; i < parts.length; i++) {
				String[] result = new String[] {};
				try {
					result = directory.list_working_directory(parts[i]);
				} catch (IOException e1) {
					respond(550, "folder not found.");

					return;
				}

				try {
					for (int j = 0; j < result.length; j++) {
						data_out.writeBytes(result[j]);
					}
				} catch (IOException e) {
					// ERREUR DE CONNEXION DE DONNÉES
					return;
				}

			}
		} else {
			String[] result;
			try {
				result = directory.list_working_directory();
				for (int j = 0; j < result.length; j++) {
					data_out.writeBytes(result[j]);
				}
			} catch (IOException e) {
				respond(550, "folder not found.");
				return;
			}

		}

		close_data();

		respond(226, "Directory send OK.");

		// respond(550, "folder not found.");

	}

	public void processPWD(String messageIn) {
		this.respond(257, "\"" + directory.get_working_directory()
				+ "\" is your current location");
	}

	public void processCWD(String messageIn) {
		String[] parts = messageIn.split(" ");
		try {
			if (parts.length >= 2) {

				directory.change_working_directory(parts[1]);
				this.respond(
						250,
						"OK. Current directory is "
								+ directory.get_working_directory());

			} else {

				if (directory.change_working_directory()) {
					this.respond(
							250,
							"OK. Current directory is "
									+ directory.get_working_directory());
				} else {
					this.respond(550, "root folder not found.");

				}
			}
		} catch (Exception ex) {
			this.respond(550,
					"Can't change directory to aaa: No such file or directory.");
			if (directory.change_working_directory()) {
				this.respond(
						250,
						"OK. Current directory is "
								+ directory.get_working_directory());
			} else {
				this.respond(550, "root folder not found.");

			}

		}
	}

	public void processCDUP(String messageIn) {
		try {
			directory.go_upper_directory();
		} catch (Exception ex) {
			if (directory.change_working_directory()) {
				this.respond(
						250,
						"OK. Current directory is "
								+ directory.get_working_directory());
			} else {
				this.respond(550, "root folder not found.");

			}
		}
		this.respond(250,
				"OK. Current directory is " + directory.get_working_directory());

	}

	private void processPASV(String messageIn) throws IOException {
		this.passive_mode = true;
		this.data_server_sock = new ServerSocket(this.data_port_passif);
		String s = Integer.toHexString(this.data_port_passif);
		int i;
		int j;
		if (s.length() == 3) {
			i = Integer.parseInt(s.substring(0, 1), 16);
			j = Integer.parseInt(s.substring(1), 16);
		} else {
			i = Integer.parseInt(s.substring(0, 2), 16);
			j = Integer.parseInt(s.substring(2), 16);
		}
		this.respond(227, "Entering Passive Mode (127,0,0,1," + i + "," + j
				+ ") \n");
	}

	private void processPORT(String messageIn) {
		this.passive_mode = false;
		String[] port_args = messageIn.split(" ")[1].split(",");
		try {
			this.data_adress = InetAddress.getByName(port_args[0] + "."
					+ port_args[1] + "." + port_args[2] + "." + port_args[3]);
			this.data_port_actif = (Integer.parseInt(port_args[4]) * 256)
					+ Integer.parseInt(port_args[5]);
			this.respond(200, "PORT command successful with adresse :"
					+ this.data_adress.toString() + " port : "
					+ this.data_port_actif);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void processQUIT(String messageIn) {
		this.multiple_respond(221, new String[] { "Goodbye.", "quit" });
		Serveur.printout("Connexion fermee par l'utilisateur");
		running = false;
	}
}
