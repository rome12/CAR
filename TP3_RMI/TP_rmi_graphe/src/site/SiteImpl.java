package site;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	private static final long serialVersionUID = 1L;

	private int id;
	private List<SiteItf> neighbors;
	private List<String> msgReceived;

	public SiteImpl(int id) throws RemoteException {
		super();
		this.id = id;
		this.neighbors = new ArrayList<SiteItf>();
		this.msgReceived = new ArrayList<String>();
	}

	public void addNeighbor(SiteItf neighbor) throws RemoteException {
		this.neighbors.add(neighbor);
	}

	public void receive(String data, int source) throws RemoteException {
		this.messageTrace("recoie un message de " + source + " (message:"
				+ data + ")");
	}
	
	//Broadcast vers tous les voisins
	public void broadcast(String data) throws RemoteException{
		this.receive(data,this.id);
		this.broadcast(data,(new Date().getTime()/1000)+new Random().nextLong());
	}

	// Broadcast vers les voisins sauf ceux déjà broadcatés
	public void broadcast(String data,long idMsg)
			throws RemoteException {

		this.msgReceived.add(data+idMsg);
		// this.messageTrace("envoie un message à tous ses voisins (message:"+data+")");
		for (int i = 0; i < neighbors.size(); i++) {
			if (!neighbors.get(i).getMsgReceived().contains(data+idMsg)) {
				neighbors.get(i).receive(data, this.id);
				neighbors.get(i).broadcast(data,idMsg);
			}
		}
	}
	
	public List<String> getMsgReceived() throws RemoteException {
		return this.msgReceived;
	}

	public void messageTrace(String message) throws RemoteException {
		System.out.println("Noeud[" + id + "]: " + message);
	}

	public int getId() throws RemoteException {
		return this.id;
	}
	
}
