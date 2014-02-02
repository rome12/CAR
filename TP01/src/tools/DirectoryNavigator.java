package tools;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DirectoryNavigator {

    private String working_directory;
    private String real_directory;

    public DirectoryNavigator(String real_directory) {
        this.real_directory = real_directory;
        this.working_directory = "/";
    }

    public String get_working_directory() {
        return this.working_directory;
    }

    public void change_working_directory(String path) {
    }

    public String[] list_working_directory(String path) {
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

    private String calculate_absolute_path(String path) {
        String s = new String(this.real_directory + "/");
        s+=path;
        

        return s;
    }

    public String[] list_working_directory() {
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
