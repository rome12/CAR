
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.DirectoryNavigator;

public class FtpRequest extends Thread {

    private Socket sock;
    private int port;
    private String repertoire;
    private BufferedReader in;
    private DataOutputStream out;
    private Boolean running;
    private DirectoryNavigator directory;

    public FtpRequest(Socket sock, int port, String repertoire) {
        this.sock = sock;
        this.port = port;
        this.repertoire = repertoire;
        this.running = true;
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

    public void processRequest() throws IOException {
        this.directory = new DirectoryNavigator(this.repertoire);
        this.output(220, "Welcome on our server!");
        Serveur.printout("Attente d'une commande");

        String messageIn = in.readLine();

        while (running) {
            Serveur.printout("Commande reçu :" + messageIn);
            //Ce systeme pour Java < 7
            if (messageIn != null) {
                String[] parts = messageIn.split(" ");
                if (parts.length > 0) {
                    String tmp = parts[0];
                    if (tmp.equals("USER")) {
                        this.processUSER(messageIn);
                    } else if (tmp.equals("PASS")) {
                        this.processPASS(messageIn);
                    } else if (tmp.equals("RETR")) {
                        this.processRETR(messageIn);
                    } else if (tmp.equals("STOR")) {
                        this.processSTOR(messageIn);
                    } else if (tmp.equals("LIST")) {
                        this.processLIST(parts);
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
                        this.output(502, "Command not implemented");
                    }
                }
            }
            // Serveur.printout("Attente d'une commande");
            messageIn = in.readLine();
        }
        sock.close();
    }

    private void output(int code, String information) {
        try {
            out.writeBytes(Integer.toString(code) + " " + information + "\n");
            Serveur.printout("-----------------------\n\tresponse");
            Serveur.printout(Integer.toString(code) + " " + information);
            Serveur.printout("-----------------------");

        } catch (IOException ex) {
            System.err.append("fail in response");

        }
    }

    private void multiple_output(int code, String[] information) {
        try {
            Serveur.printout("-----------------------\n\tresponse");
            for (int i = 0; i < information.length; i++) {
                out.writeBytes(Integer.toString(code) + " " + information[i] + "\n");
                Serveur.printout(Integer.toString(code) + " " + information[i]);
            }
            Serveur.printout("-----------------------");

        } catch (IOException ex) {
            System.err.append("fail in response");

        }
    }

    public void processUSER(String messageIn) {
        Serveur.printout("Methode processUSER");
    }

    public void processPASS(String messageIn) {
        Serveur.printout("Methode processPASS");
    }

    public void processRETR(String messageIn) {
    }

    public void processSTOR(String messageIn) {
    }

    public void processLIST(String[] messageIn) {
        if (messageIn.length > 1) {
            for (int i = 1; i < messageIn.length; i++) {
                System.err.print("PATH" + messageIn[i]);
                multiple_output(257, directory.list_working_directory(messageIn[i]));
            }

        } else {
            multiple_output(257, directory.list_working_directory());
        }
    }

    public void processPWD(String messageIn) {
        this.output(257, "\"" + directory.get_working_directory() + "\" is your current location");
    }

    public void processCWD(String messageIn) {
        this.output(250, "OK. Current directory is " + directory.get_working_directory());
    }

    public void processCDUP(String messageIn) {
    }

    public void processQUIT(String messageIn) {
        Serveur.printout("Connexion fermée par l'utilisateur");
        running = false;
    }
}
