package ftp.tools;

import ftp.FtpRequest;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataTransferManager {

    private Socket dataServerSocket;
    private int port;
    private DataOutputStream data_out;

    public DataTransferManager(int port) {
        this.port = port;
    }

    public boolean initiate_transfer(String ip) {
        try {
            dataServerSocket = new Socket(ip,this.port);
            
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
            return true;
        } catch (IOException ex) {
        }
        return false;
    }

    public void close() throws IOException {
        data_out.close();
        dataServerSocket.close();
    }

    public boolean initiate_data_output() {
        try {
            data_out = new DataOutputStream(dataServerSocket.getOutputStream());
            return true;
        } catch (IOException ex) {
        }
        return false;
    }

    public void transmit(String s) throws IOException {
        data_out.writeBytes(s + "\n");
        data_out.flush();

    }
}
