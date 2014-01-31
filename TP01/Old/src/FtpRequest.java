import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class FtpRequest extends Thread{

	private int numeroSocket;
	private ServerSocket ServeurSockerControl;
	private Socket socketControl;
	private ServerSocket ServeurSocketDonne;
	private Socket socketDonne;
	private String repertoireCourant;
	private OutputStream osControl;
	private OutputStream osDonne;
	private DataOutputStream dosControl;
	private DataOutputStream dosDonne;
	private InputStream is;
	private InputStreamReader isr;
	private BufferedReader br;
	private String user;
	private String mdp;
	private int numeroSocketPassiv;
	

	public FtpRequest(int num,ServerSocket serv,Socket socks,String repertoireCourantt,int portPassiv)
	{
		this.numeroSocket=num;
		this.ServeurSockerControl=serv;
		this.socketControl=socks;
		this.repertoireCourant=repertoireCourantt;
		this.user="Anonymous";
		this.mdp="Anonymous";
		this.numeroSocketPassiv=portPassiv;
	}

	public static File[] listFiles(String directoryPath){
		File[] files = null;
		File directoryToScan = new File(directoryPath);
		files = directoryToScan.listFiles();
		return files;
	} 

	
	private void InitialisationDeControl() throws IOException{
		osControl=socketControl.getOutputStream();
		dosControl=new DataOutputStream(osControl);
		is=socketControl.getInputStream();
		isr=new InputStreamReader(is);
		br = new BufferedReader(isr);
	}
	
	private void InitialisationDeDonne(){
		ServeurSocketDonne = null;
		socketDonne=null;
		osDonne = null;
		dosDonne = null;
	}

	public void run() {
		try {

			this.InitialisationDeControl();
			this.InitialisationDeDonne();
			this.processAcceuil();			
			this.processRequest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processRequest() throws IOException
	{
		String Reponse=br.readLine();
		System.out.println("Reponse vaut : "+Reponse);
		while(Reponse!=null) 
		{
			if (Reponse.startsWith("USER")) {processUSER(Reponse);}
			else { if (Reponse.startsWith("PASS")) {processPASS(Reponse);} 
			else { if (Reponse.startsWith("SYST")) {processSYST(Reponse);} 
			else { if (Reponse.startsWith("FEAT")) {processFEAT(Reponse);} 
			else { if (Reponse.startsWith("PWD")) {processPWD(Reponse);} 
			else { if (Reponse.startsWith("TYPE I")) {processTYPE(Reponse);} 
			else { if (Reponse.startsWith("PASV")) {processPASV(Reponse);} 
			else { if (Reponse.startsWith("LIST")) {processLIST(Reponse);} 
			else { if (Reponse.startsWith("CWD")) {processCWD(Reponse);} 
			else { if (Reponse.startsWith("CDUP")) {processCDUP(Reponse);} 
			else { if (Reponse.startsWith("RETR")) {processRETR(Reponse);} 
			else { if (Reponse.startsWith("STOR")) {processSTOR(Reponse);} 
			}}}}}}}}}}}
			Reponse=br.readLine();
		}//while
		if (ServeurSocketDonne!=null) {	ServeurSocketDonne.close(); }
	}

	
	private void processAcceuil() throws IOException {
		/*Message d'acceuil*/
		dosControl.writeBytes("220 Bienvenue Laraki \n");	

	}

	private void processCDUP(String reponse) throws IOException {
		// TODO Auto-generated method stub
		int avantDerniereoccuranceDuSlash=0;
		int dernieroccuranceDuSlash=0;
		for(int i=0;i<repertoireCourant.length();i++){
			if (repertoireCourant.charAt(i)=='/') { avantDerniereoccuranceDuSlash=dernieroccuranceDuSlash;
			dernieroccuranceDuSlash=i;}
		}
		repertoireCourant=repertoireCourant.substring(0,avantDerniereoccuranceDuSlash)+"/";
		dosControl.writeBytes("250 c'est fait !!! \n");
	}

	private void processSTOR(String reponse) throws IOException {
		// TODO Auto-generated method stub
		FileOutputStream fos = null;
		socketDonne=null;				
		File file = new File(repertoireCourant+reponse.substring((5)));
		fos = new FileOutputStream(file);
		socketDonne=ServeurSocketDonne.accept();
		dosControl.writeBytes("150 Opening " + repertoireCourant+reponse.substring(5) + " mode data connection.\n");

		InputStream in = socketDonne.getInputStream();

		byte buf[] = new byte[1024];
		int nread;
		while ((nread = in.read(buf)) > 0)
		{
			fos.write(buf, 0, nread);
		}
		fos.flush();
		fos.close();
		ServeurSocketDonne.close();			
	}

	private void processRETR(String reponse) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
		socketDonne=null;
		File file = new File(repertoireCourant+reponse.substring((5)));
		fis = new FileInputStream(file);
		socketDonne=ServeurSocketDonne.accept();
		dosControl.writeBytes("150 Opening " + repertoireCourant+reponse.substring(5) + " mode data connection.\n");
		osDonne = socketDonne.getOutputStream();

		byte buf[] = new byte[1024];
		int nread;
		while ((nread = fis.read(buf)) > 0)
		{
			osDonne.write(buf, 0, nread);
		}
		if (dosDonne!=null) {
			dosDonne.close();
		}
		if (osDonne!=null)
			osDonne.flush();
		osDonne.close();
		ServeurSocketDonne.close();
	}

	private void processCWD(String reponse) throws IOException {
		// TODO Auto-generated method stub
		if (reponse.substring(4).startsWith("/"))
		{
			repertoireCourant=reponse.substring(4)+"/";
		}
		else {
			repertoireCourant+=reponse.substring(4)+"/";
		}
		dosControl.writeBytes("250 c'est fait !!! \n");
	}

	private void processPASV(String reponse) throws IOException {
		// TODO Auto-generated method stub
		ServeurSocketDonne=new ServerSocket(this.numeroSocketPassiv);
		String s=Integer.toHexString(this.numeroSocketPassiv);
		int i;
		int j;
		if (s.length()==3) {
		 i=Integer.parseInt(s.substring(0,1),16);
		 j=Integer.parseInt(s.substring(1),16);
		}
		else {
			 i=Integer.parseInt(s.substring(0,2),16);
			 j=Integer.parseInt(s.substring(2),16);
		}
		dosControl.writeBytes("227 Entering Passive Mode (127,0,0,1,"+i+","+j+") \n");
	}

	private void processLIST(String reponse) throws IOException {
		// TODO Auto-generated method stub
		dosControl.writeBytes("150 Here comes the directory listing. \n");
		socketDonne=ServeurSocketDonne.accept();
		osDonne=socketDonne.getOutputStream();
		dosDonne=new DataOutputStream(osDonne);
		// m1\share\
		File pwd = new File(repertoireCourant);
		if (pwd.listFiles() != null) {
			String laReponse = "";
			for (File file : pwd.listFiles()) {
				if (file.isFile()) {
					laReponse = "\053,r,i" + file.length() + ",\011"
							+ file.getName() + "\015\012";
				}
				if (file.isDirectory()) {
					laReponse = "\053m" + file.lastModified() + ",/,\011"
							+ file.getName() + "\015\012";
				}
				dosDonne.writeBytes(laReponse + "\n");
				dosDonne.flush();
			}
			dosDonne.close();
			osDonne.close();
			ServeurSocketDonne.close();
			dosControl.writeBytes("226 Directory send OK.. \n");
		}
	}

	private void processTYPE(String reponse) throws IOException {
		// TODO Auto-generated method stub
		dosControl.writeBytes("200 Switching to Binary mode \n");
	}

	private void processPWD(String reponse) throws IOException {
		// TODO Auto-generated method stub
		dosControl.writeBytes("257 \""+repertoireCourant+ "\"\n");
	}

	private void processSYST(String reponse) throws IOException {
		// TODO Auto-generated method stub
		dosControl.writeBytes("215 Ubuntu Double Coeur \n"); 
	}

	private void processFEAT(String reponse) throws IOException {
		// TODO Auto-generated method stub
		dosControl.writeBytes("211-Lesfeatures\n");
		dosControl.writeBytes("Feature1\n");
		dosControl.writeBytes("Feature2\n");	
		dosControl.writeBytes("Feature3\n");
		dosControl.writeBytes("211 EndFeature\n");
	}

	private void processPASS(String reponse) throws IOException {
		// TODO Auto-generated method stub
		mdp=reponse.substring(5); 

		String TableDesMdps="tabledesmdp.txt";
		String chaine1="";
		String chaine2="";
		boolean bool=true;


		InputStream ipss=new FileInputStream(TableDesMdps);
		InputStreamReader ipsrr=new InputStreamReader(ipss);
		BufferedReader brr=new BufferedReader(ipsrr);
		String ligne;
		while ((ligne=brr.readLine())!=null){
			if (bool){
				chaine2="";
				chaine1=ligne+"\n";
				bool=!bool;
			}
			else {
				chaine2=ligne+"\n";
				bool=!bool;
			}
			if ((this.user+"\n").equals(chaine1) && (this.mdp+"\n").equals(chaine2)) {
				dosControl.writeBytes("230 Vous etes authentifier" + user + " " + mdp + " "+ "\n");
				brr.close(); 
				return;
			}
		}
		dosControl.writeBytes("530 pas bon chaine 1 vaut :"+chaine1+" chaine 2 vaut :"+chaine2+ " le login entré vaut :" + user+"le mdp entre vaut :"+mdp+"\n");
		brr.close(); 
	}

	private void processUSER(String reponse) throws IOException {
		// TODO Auto-generated method stub
		user=reponse.substring(5);
		dosControl.writeBytes("331 Le mdp svp \n");
	}
}
