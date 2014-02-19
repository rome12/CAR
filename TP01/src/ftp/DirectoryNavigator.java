package ftp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Classe de gestion de la navigation à travers les répertoires.
 * @author Groupe 4 équipe 1.
 */
public class DirectoryNavigator {

    private String working_directory;
    private String real_directory;

    public DirectoryNavigator(String real_directory) {
        this.real_directory = real_directory;
        this.working_directory = File.separator;
    }
/**
 *  Permet d'obtenir le chemin absolu par rapport au répertoire / du système.
 * @return Le chemin absolu.
 * @throws IOException  dans le cas ou le chemin n'est pas trouvé.
 */
    public String get_absolute_path_to_working_directory() throws IOException {
        File f = new File(this.real_directory + File.separator
                + this.working_directory + File.separator);
        return f.getCanonicalPath();
    }

    public String get_working_directory() {
        return this.working_directory;
    }
/**
 * Permet de remonter d'un répertoire.
 * @throws IOException le dossier supperieur n'existe pas.
 */
    public void go_upper_directory() throws IOException {
        change_working_directory(".." + File.separator);

    }
/**
 * Permet de changer le répertoire courant pour le répertoire passé en paramètres.
 * @param path le nouveau chemin, relatif ou absolu vers le dossier souhaité.
 * @throws IOException le répertoire n'est pas accessible.
 */
    public void change_working_directory(String path) throws IOException {
        String head = this.calculate_absolute_path(File.separator);
        String absolute = this.calculate_absolute_path(path);
        this.working_directory = absolute.replace(head, "");
        this.working_directory += File.separator;
    }
/**
 * Permet de retourner au répertoire racine du serveur FTP.
 * @return le succès de la tache; Il renvoie faux dans le seul cas ou le répertoire du serveur FTP n'est plus accessible.
 */
    public Boolean change_working_directory() {
        try {
            this.change_working_directory(File.separator);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
/**
 * Récupère la liste du répertoire passé en paramètre.
 * @param path le répertoire à lister
 * @return la liste des fichiers.
 * @throws IOException si un fichier/dossier n'est pas accessible.
 * @throws NullPointerException si des informations sont vide
 */
    public String[] list_working_directory(String path) throws IOException {
        File folder = new File(this.calculate_absolute_path(path));
        List<String> s = new ArrayList<String>();// indispensable sous Java 6, déclanche un warning sous Java 7.
        //sous Java 7, on peut utiliser la syntaxe : List<String> s = new ArrayList<>(); pour éviter la redondance.
        File[] listOfFiles = new File[]{};
        if (folder.isDirectory()) {
            listOfFiles = folder.listFiles();
        } else if (folder.isFile()) {
            listOfFiles = new File[]{folder};
        } else {
            throw new IOException();
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                s.add("\053,r,i" + file.length() + ",\011"
                        + file.getName() + "\015\012");
            }
            if (file.isDirectory()) {
                s.add("\053m" + file.lastModified() + ",/,\011"
                        + file.getName() + "\015\012");
            }
        }
        String[] simple = new String[s.size()];
        s.toArray(simple);
        return simple;
    }
/**
 * Calcul du chemin absolu du dossier
 * @param path le chemin à calculer
 * @return une chaine de caractère contenant le chemin canonique du fichier
 * @throws IOException Si le chemin n'existe pas, ou n'est pas dans le répertoire racine du serveur FTP.
 */
    public String calculate_absolute_path(String path) throws IOException {
        File f = new File(this.real_directory + File.separator);
        String s = f.getCanonicalPath();
        File file = new File(s + File.separator + path);
        if (!path.startsWith(File.separator)) {
            file = new File(s + File.separator + this.working_directory + File.separator + path);
        }
        String b = file.getCanonicalPath();
        if (b.startsWith(s)) {

            return b;
        }
        throw new IOException("wrong path");
    }

    public String[] list_working_directory() throws IOException {
        return this.list_working_directory(this.working_directory);
    }
}
