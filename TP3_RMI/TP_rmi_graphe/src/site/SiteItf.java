package site;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SiteItf extends Remote {

	public void addNeighbor(SiteItf neighbor) throws RemoteException;

	public void receive(String data, int source) throws RemoteException;

	public void broadcast(String data) throws RemoteException;

	public void broadcast(String data, long date) throws RemoteException;

	public void messageTrace(String message) throws RemoteException;

	public int getId() throws RemoteException;

	public List<String> getMsgReceived() throws RemoteException;

}
