package client;

import java.rmi.Naming;

import site.*;

public class CreateEdge {

	public static void main(String[] args) throws Exception {
		if (args.length == 2) {
			SiteItf SiteParent = (SiteItf) Naming.lookup(args[0]);
			SiteItf SiteChild = (SiteItf) Naming.lookup(args[1]);
			SiteParent.addChild(SiteChild);
			SiteChild.setParent(SiteParent);
		} else {
			System.out
					.println("Erreur d'argument : java CreateEdge ID_parent ID_fils");
		}
	}

}
