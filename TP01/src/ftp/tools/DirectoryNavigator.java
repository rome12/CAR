package ftp.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryNavigator {

    private String working_directory;
    private String real_directory;

    public DirectoryNavigator(String real_directory) {
        this.real_directory = real_directory;
        this.working_directory = "/";
    }

    public String get_absolute_path_to_working_directory() throws IOException {
        File f = new File(this.real_directory + "/" + this.working_directory + "/");
        return f.getCanonicalPath();
    }

    public String get_working_directory() {
        return this.working_directory;
    }

    public void change_working_directory(String path) throws NullPointerException, IOException {
        String head = this.calculate_absolute_path("/");
        String absolute = this.calculate_absolute_path(path);
        this.working_directory = absolute.replace(head, "");
        this.working_directory += "/";
    }

    public String[] list_working_directory(String path) throws IOException, NullPointerException {
                        System.err.println(calculate_absolute_path(path));
        File folder = new File(this.calculate_absolute_path(path));
        List<String> s = new ArrayList<String>();
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

    private static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        char u = 'b';
        if (bytes < unit) {
            return bytes + " " + u;
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "" + u;
        return String.format("%.1f %s", bytes / Math.pow(unit, exp), pre);
    }

    public String calculate_absolute_path(String path) throws IOException, NullPointerException {
        File f = new File(this.real_directory + "/");
        String s = f.getCanonicalPath();
        File file = new File(s + "/" + path);
        if (!path.startsWith("/")) {
            file = new File(s + "/" + this.working_directory + "/" + path);
        }
        String b = file.getCanonicalPath();
        if (b.startsWith(s)) {
            
            return b;
        }
        throw new IOException("wrong path");
    }

    public String[] list_working_directory() throws IOException, NullPointerException {
        return this.list_working_directory(this.working_directory);


    }
}
