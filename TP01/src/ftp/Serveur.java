package ftp;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Serveur {

    static boolean verbose = false;
    public static int port = 7000;
    public static int dataport = 7010;

    public static void printout(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) {
        ServerSocket servSocket;
        Socket sock;

        if (args.length < 1) {
            System.err.println("Usage : serveur (repertoire) [-v]");
        }
        File f;
        String repertoire = "";
        Boolean check_path = false;
        if (Arrays.asList(args).contains("-v")) {
            verbose = true;
        }
        for (String s : args) {
            f = new File(s);
            if (f.isDirectory()) {
                check_path = true;
                repertoire = s;
                break;
            }
        }
        if (!check_path) {
            System.err.println("Le répertoire suivant n'existe pas.");
            System.exit(47);
        }

        try {
            servSocket = new ServerSocket(Serveur.port);

            printout("Serveur lancé sur le port " + Serveur.port);
            printout("Répertoire racine : " + repertoire);

            while (true) {
                    printout("Attente d'une connection");
                    sock = servSocket.accept();
                    printout("Connection détectée.");
                    FtpRequest ftpRequest = new FtpRequest(sock, Serveur.port, repertoire, Serveur.dataport);
                    Serveur.dataport++;
                    ftpRequest.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
