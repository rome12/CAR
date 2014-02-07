package ftp.tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataTransferManager{

    private ServerSocket dataServerSocket;
    private DataOutputStream data_out;
    private boolean passive_mode = false;
    public static ArrayList<Thread> workingThreads;

    public void setPassiveMode() {
        passive_mode = true;
    }

    public void setActiveMode() {
        passive_mode = false;
    }

    public DataTransferManager(int port) {
        workingThreads = new ArrayList<Thread>();
    }

    public boolean initiate_transfer(String ip, int port) {
        if (passive_mode) 
        {
            try {
            } catch (Exception ex) {
                Logger.getLogger(DataTransferManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return initiate_transfer(port);
        }
        return false;

    }

    public boolean initiate_transfer(int port) {
        if (!passive_mode) {
            try {
                dataServerSocket = new ServerSocket(port);
                return true;

            } catch (Exception ex) {
                Logger.getLogger(DataTransferManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public void close() throws IOException {
    }

    public boolean initiate_data_output() {

        return false;
    }

    public boolean initiate_data_input() {
        return false;
    }


}
