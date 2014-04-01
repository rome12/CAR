package site;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	private static final long serialVersionUID = 1L;

	private int id;
	private List<SiteItf> children;
	private SiteItf parent;

	public SiteImpl(int id) throws RemoteException {
		super();
		this.id = id;
		this.children = new ArrayList<SiteItf>();
		this.parent = null;
	}

	public void addChild(SiteItf child) throws RemoteException {
		this.children.add(child);
	}

	public void setParent(SiteItf parent) throws RemoteException {
		this.parent = parent;
	}

	public void sendToChildren(String data) throws RemoteException {
		if (this.children.size() > 0)
			this.messageTrace("envoie un message à tous ses fils (message:"
					+ data + ")");
		for (int i = 0; i < children.size(); i++) {
			children.get(i).receive(data, this.id);
		}
	}

	// Send to children except the ID child
	public void sendToChildren(String data, int id) throws RemoteException {
		if (this.children.size() > 1)
			this.messageTrace("envoie un message à tous ses fils (message:"
					+ data + ") sauf à " + id);
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getId() != id)
				children.get(i).receive(data, this.id);
		}
	}

	public void sendToParent(String data) throws RemoteException {
		this.messageTrace("envoie un message à son père (message:" + data + ")");
		parent.receive(data, this.id);
	}

	public void receive(String data, int source) throws RemoteException {
		this.messageTrace("recoie un message de " + source + " (message:"
				+ data + ")");
	}

	// Broadcast vers tout le monde
	public void broadcastBase(String data) throws RemoteException {
		if (this.parent != null) {
			this.sendToParent(data);
			parent.broadcastFromChild(data, this.id);
		}
		this.sendToChildren(data);
		for (int i = 0; i < children.size(); i++) {
			children.get(i).broadcastFromParent(data);
		}
	}

	// Broadcast vers les enfants
	public void broadcastFromParent(String data) throws RemoteException {
		this.sendToChildren(data);
		for (int i = 0; i < children.size(); i++) {
			children.get(i).broadcastFromParent(data);
		}
	}

	// Broadcast vers le parent + les enfants sauf childId
	public void broadcastFromChild(String data, int childId)
			throws RemoteException {
		if (this.parent != null) {
			this.sendToParent(data);
			parent.broadcastFromChild(data, this.id);
		}
		this.sendToChildren(data, childId);
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getId() != childId)
				children.get(i).broadcastFromParent(data);
		}
	}

	public void messageTrace(String message) throws RemoteException {
		System.out.println("Noeud[" + id + "]: " + message);
	}

	public int getId() throws RemoteException {
		return this.id;
	}
}
