package ftp.tools;

import ftp.FtpRequest;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataTransferManager {

    private ServerSocket dataServerSocket;
    private int port;
    private DataOutputStream data_out;

    public DataTransferManager(int port) {
        this.port = port;
    }

    public boolean initiate_transfer(String ip, int port) {
        try {
            dataServerSocket = new ServerSocket(this.port);
            String s = Integer.toHexString(this.port);
            int i;
            int j;
            if (s.length() == 3) {
                i = Integer.parseInt(s.substring(0, 1), 16);
                j = Integer.parseInt(s.substring(1), 16);
            } else {
                i = Integer.parseInt(s.substring(0, 2), 16);
                j = Integer.parseInt(s.substring(2), 16);
            }
            String b[] = ip.split(".");
            FtpRequest.respond(227, "Entering Passive Mode (" + b[0] + "," + b[1] + "," + b[2] + "," + b[3] + "," + i + "," + "," + j + ")");
            return true;
        } catch (IOException ex) {
        }
        return false;
    }

    public boolean initiate_data_output() {
        try {
            data_out = new DataOutputStream(dataServerSocket.accept().getOutputStream());

            return true;
        } catch (IOException ex) {
        }
        return false;
    }
    
}
