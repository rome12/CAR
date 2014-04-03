package server;

import java.rmi.Naming;

import site.*;

public class CreateNode {

	public static void main(String[] args) throws Exception {
		if (args.length == 1) {
			int id = Integer.parseInt(args[0]);
			SiteItf leSite = new SiteImpl(id);
			Naming.bind(args[0], leSite);
			// System.out.println("Création du noeud " + id + " réussie");
		} else {
			System.out.println("Erreur d'argument : java CreateNode ID");
		}

	}

}
