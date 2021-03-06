package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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

/**
 * Classe permettant de gerer les requetes faites par le client connecté au
 * serveur ftp
 * 
 * @author Groupe 4 équipe 1
 * 
 */
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
	private Boolean user_is_connected = false;

	/**
	 * Constructeur du FtpRequest
	 * 
	 * @param sock
	 *            socket representant la connection avec le client pour la
	 *            transmission des commandes
	 * @param repertoire
	 *            repertoire de base du serveur ftp
	 * @param data_port_passif
	 *            port pour l'envoi de donnees en mode passif
	 */
	public FtpRequest(Socket sock, String repertoire, int data_port_passif) {
		this.sock = sock;
		this.repertoire = repertoire;
		this.running = true;
		this.data_port_passif = data_port_passif;
	}

	/**
	 * Lancement du thread
	 */
	public void run() {
		try {
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new DataOutputStream(sock.getOutputStream());
			this.processRequest();
		} catch (IOException e) {
			// connexion réinitialisée
		}
	}

	/**
	 * Permet de recevoir puis de traiter les commandes envoyees par le client
	 * 
	 * @throws IOException
	 */
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
<<<<<<< HEAD

					if (!user_is_connected) {
						if (tmp.equals("USER")) {
							this.processUSER(messageIn);
						} else if (tmp.equals("PASS")) {
							this.processPASS(messageIn);
						} else {
							this.respond(500, "Command not implemented");
						}
=======
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
                    } else if (tmp.equals("DELE")) {
                        this.processDELE(messageIn);
                    } else if (tmp.equals("RMD")) {
                        this.processRMD(messageIn);
					} else if (tmp.equals("TYPE")) {
                        this.processTYPE(messageIn);
>>>>>>> e19bf0da5ca1cbe751f7dc1bebee3f1ce96db04c
					} else {
						if (tmp.equals("RETR")) {
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
						} else if (tmp.equals("DELE")) {
							this.processDELE(messageIn);
						} else if (tmp.equals("RMD")) {
							this.processRMD(messageIn);
						} else if (tmp.equals("TYPE")) {
							this.processTYPE(messageIn);
						} else {
							this.respond(500, "Command not implemented");
						}
					}
				}
			}
			messageIn = in.readLine();
		}
		Serveur.printout("Client deconnecte");
		sock.close();
	}

	/**
	 * Permet d'envoyer une reponse au client sur le socket des commandes
	 * 
	 * @param code
	 *            correspond au code de la reponse
	 * @param information
	 *            message associe au code
	 */
	public void respond(int code, String information) {
		try {
			out.writeBytes(Integer.toString(code) + " " + information + "\n");
			Serveur.printout(Integer.toString(code) + " " + information);

		} catch (IOException ex) {
			System.err.append("fail in response");

		}
	}

	/**
	 * Permet d'envoyer plusieurs reponses au client sur le socket des commandes
	 * 
	 * @param code
	 *            correspond au code des reponses
	 * @param information
	 *            messages associes au code
	 */
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

	/**
	 * Active la communication sur le socket des donnees en mode passif ou actif
	 * 
	 * @throws IOException
	 */
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

	/**
	 * Ferme la communication sur le socket des donnes en mode passif ou actif
	 * 
	 * @return
	 */
	public Boolean close_data() {
		try {
			data_sock.close();
			data_out.close();
			if (passive_mode)
				data_server_sock.close();
			return true;
		} catch (IOException e) {
			return false;
		}

	}

	/**
	 * Traitement de la commande USER permettant au client de donner le login
	 * 
	 * @param messageIn
	 *            string contenant USER puis la login du client
	 */
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

	/**
	 * Traitement de la commande PASS permettant au client de donner le mot de
	 * passe
	 * 
	 * @param messageIn
	 *            string contenant PASS puis le mot de passe du client
	 */
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
						user_is_connected=true;
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

	/**
	 * Traitement de la commande RETR permettant au client de retirer un fichier
	 * se trouvant sur le serveur
	 * 
	 * @param messageIn
	 *            string contenant RETR puis le nom du fichier
	 */
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

	/**
	 * Traitement de la commande STOR permettant au client d'envoyer un fichier
	 * pour le stocker sur le serveur
	 * 
	 * @param messageIn
	 *            string contenant STOR puis le nom du fichier
	 */
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

	/**
	 * Traitement de la commande LIST permettant au client de lister les
	 * fichiers du serveur
	 * 
	 * @param messageIn
	 *            string contenant LIST et facultativement les repertoires a
	 *            lister
	 */
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
	}

	/**
	 * Traitement de la commande PWD permettant au client de connaitre son
	 * repertoire courant
	 * 
	 * @param messageIn
	 *            string contenant PWD
	 */
	public void processPWD(String messageIn) {
		this.respond(257, "\"" + directory.get_working_directory()
				+ "\" is your current location");
	}

	/**
	 * Traitement de la commande CWD permettant au client de changer son
	 * repertoire courant
	 * 
	 * @param messageIn
	 *            string contenant CWD puis le nouveau repertoire courant
	 */
	public void processCWD(String messageIn) {
		String[] parts = messageIn.split(" ");
		try {
			if (parts.length >= 2) {
				File f = new File(directory.calculate_absolute_path(parts[1]));

				if (f.exists() && f.isDirectory()) {

					directory.change_working_directory(parts[1]);

					this.respond(
							250,
							"OK. Current directory is "
									+ directory.get_working_directory());
				} else {
					this.respond(
							550,
							"OK. Folder not found : "
									+ directory.get_working_directory());
				}

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

	/**
	 * Traitement de la commande CDUP permettant au client de changer son
	 * repertoire courant pour le repertoire du dessus
	 * 
	 * @param messageIn
	 *            string contenant CDUP
	 */
	public void processCDUP(String messageIn) {
		try {
			directory.go_upper_directory();
			this.respond(
					250,
					"OK. Current directory is "
							+ directory.get_working_directory());
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

	}

	/**
	 * Traitement de la commande PASV permettant au client de passer en mode
	 * passif et de recuperer le port sur lequel il devra se connecter
	 * 
	 * @param messageIn
	 *            string contenant PASV
	 */
	public void processPASV(String messageIn) throws IOException {
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

	/**
	 * Traitement de la commande PORT permettant au client de passer en mode
	 * actif et de passer au serveur le port sur lequel il devra se connecter
	 * 
	 * @param messageIn
	 *            string contenant PORT suivi du port sur lequel se connecter
	 */
	public void processPORT(String messageIn) {
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

	/**
	 * Traitement de la commande QUIT permettant au client de quitter la session
	 * en cours
	 * 
	 * @param messageIn
	 *            string contenant QUIT
	 */
	public void processQUIT(String messageIn) {
		user_is_connected=false;
		this.multiple_respond(221, new String[] { "Goodbye.", "quit" });
		Serveur.printout("Connexion fermee par l'utilisateur");
		running = false;
	}
<<<<<<< HEAD

	/**
	 * Traitement de la commande DELE permettant au client de supprimer un
	 * fichier
	 * 
	 * @param messageIn
	 *            string contenant DELE
	 */
	public void processDELE(String messageIn) {
		process_delete(messageIn.replace("DELE ", ""));
	}

	/**
	 * Traitement de la commande RMD permettant au client de supprimer un
	 * fichier
	 * 
	 * @param messageIn
	 *            string contenant RMD
	 */
	public void processRMD(String messageIn) {
		process_delete(messageIn.replace("RMD ", ""));
	}

	/**
	 * Traitement de la supression
	 * 
	 * @param messageIn
	 *            string contenant le fichier à supprimer
	 */
	public void process_delete(String m) {
		try {
			if (directory.remove_file_or_folder(m)) {
				this.respond(250, "file/directory was successfully removed");
			} else {
				this.respond(550, "file/directory can't be deleted");
			}
		} catch (IOException ex) {
			this.respond(500, "file/directory not found");

		}
	}

	/**
	 * Gestion de la commande TYPE
	 * 
	 * @param messageIn
	 */
	public void processTYPE(String messageIn) {
		this.respond(200, "Not implemented : Switching to Binary Mode");
	}
=======
        /**
        * Traitement de la commande DELE permettant au client de supprimer un fichier
        * 
        * @param messageIn 
        *          string contenant DELE
        */
        private void processDELE(String messageIn) {
                process_delete(messageIn.replace("DELE ", ""));                
        }
        /**
        * Traitement de la commande RMD permettant au client de supprimer un fichier
        * 
        * @param messageIn 
        *          string contenant RMD
        */
        private void processRMD(String messageIn) {
                process_delete(messageIn.replace("RMD ", ""));                
        }
        /**
         *Traitement de la supression 
         * 
         * @param messageIn 
         *          string contenant le fichier à supprimer 
         */
        private void process_delete(String m){
                try {
                        if(directory.remove_file_or_folder(m)){
                                this.respond(250, "file/directory was successfully removed");
                        }else{
                                this.respond(550,"file/directory can't be deleted");
                        }
                } catch (IOException ex) {
                        this.respond(500,"file/directory not found");

                }
        }
        
        private void processTYPE(String messageIn) {
           this.respond(200, "Switching to Binary mode");
        }
>>>>>>> e19bf0da5ca1cbe751f7dc1bebee3f1ce96db04c
}
