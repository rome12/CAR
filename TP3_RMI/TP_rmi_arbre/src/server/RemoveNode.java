package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import site.SiteImpl;
import site.SiteItf;

public class RemoveNode {

	public static void main(String[] args) throws RemoteException,
			MalformedURLException, NotBoundException {
		if (args.length == 1) {
			Naming.unbind(args[0]);
			int id = Integer.parseInt(args[0]);
			SiteItf leSite = new SiteImpl(id);
			UnicastRemoteObject.unexportObject(leSite, true);
		} else {
			System.out.println("Erreur d'argument : java RemoveNode ID");
		}

	}

}
