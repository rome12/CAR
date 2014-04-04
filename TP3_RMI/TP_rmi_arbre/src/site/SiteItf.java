package site;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SiteItf extends Remote {

	public void addChild(SiteItf child) throws RemoteException;

	public void setParent(SiteItf parent) throws RemoteException;

	public void sendToChildren(String data) throws RemoteException;

	public void sendToChildren(String data, int id) throws RemoteException;

	public void sendToParent(String data) throws RemoteException;

	public void receive(String data, int source) throws RemoteException;

	public void broadcastBase(final String data) throws RemoteException;

	public void broadcastFromParent(final String data) throws RemoteException;

	public void broadcastFromChild(final String data, int childId)
			throws RemoteException;

	public void messageTrace(String message) throws RemoteException;

	public int getId() throws RemoteException;
}
