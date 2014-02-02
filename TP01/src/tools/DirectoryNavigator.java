package tools;

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
        File folder = new File(this.calculate_absolute_path(path));
        File[] listOfFiles = folder.listFiles();
        List<String> s = new ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            s.add(listOfFiles[i].getName());
        }
        String[] simple = new String[s.size()];
        s.toArray(simple);
        return simple;


    }

    private String calculate_absolute_path(String path) throws IOException, NullPointerException {
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
        File folder = new File(calculate_absolute_path(this.working_directory));
        File[] listOfFiles = folder.listFiles();
        List<String> s = new ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            s.add(listOfFiles[i].getName());
        }
        String[] simple = new String[s.size()];
        s.toArray(simple);
        return simple;


    }
}
