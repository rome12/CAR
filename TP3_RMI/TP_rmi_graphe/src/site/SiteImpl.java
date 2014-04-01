package site;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	private static final long serialVersionUID = 1L;

	private int id;
	private List<SiteItf> neighbors;

	public SiteImpl(int id) throws RemoteException {
		super();
		this.id = id;
		this.neighbors = new ArrayList<SiteItf>();
	}

	public void addNeighbor(SiteItf neighbor) throws RemoteException {
		this.neighbors.add(neighbor);
	}

	public void receive(String data, int source) throws RemoteException {
		this.messageTrace("recoie un message de " + source + " (message:"
				+ data + ")");
	}

	// Broadcast vers tout le monde
	public void broadcast(String data) throws RemoteException {
		this.broadcast(data, new ArrayList<Integer>());
	}

	// Broadcast vers les voisins sauf ceux déjà broadcatés
	public void broadcast(String data, List<Integer> idBroadcasted)
			throws RemoteException {

		idBroadcasted.add(this.id);

		// this.messageTrace("envoie un message à tous ses voisins (message:"+data+")");

		for (int i = 0; i < neighbors.size(); i++) {
			if (!idBroadcasted.contains(neighbors.get(i).getId())) {
				neighbors.get(i).receive(data, this.id);
				idBroadcasted.add(neighbors.get(i).getId());
				neighbors.get(i).broadcast(data, idBroadcasted);
			}
		}
	}

	public void messageTrace(String message) throws RemoteException {
		System.out.println("Noeud[" + id + "]: " + message);
	}

	public int getId() throws RemoteException {
		return this.id;
	}
}
