package client;

import java.rmi.Naming;

import site.*;

public class CreateEdge {

	public static void main(String[] args) throws Exception {
		if (args.length == 2) {
			SiteItf Site1 = (SiteItf) Naming.lookup(args[0]);
			SiteItf Site2 = (SiteItf) Naming.lookup(args[1]);
			Site1.addNeighbor(Site2);
			Site2.addNeighbor(Site1);
		} else {
			System.out.println("Erreur d'argument : java CreateEdge ID_1 ID_2");
		}
	}

}
