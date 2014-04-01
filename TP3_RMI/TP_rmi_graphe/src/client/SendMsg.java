package client;

import java.rmi.Naming;

import site.SiteItf;

public class SendMsg {

	public static void main(String[] args) throws Exception {
		if (args.length == 2) {
			SiteItf theSite = (SiteItf) Naming.lookup(args[0]);
			theSite.broadcast(args[1]);
		} else {
			System.out
					.println("Erreur d'argument : java SendMsg ID_sender MESSAGE");
		}
	}

}
