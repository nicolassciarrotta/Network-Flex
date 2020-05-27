package network_flex;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;

public class Client {

	private int id_client;
	private String _SERVER = "localhost";
	private int _PORT = 8000;
	private Random rnd = new Random();
	
	public Client(int id_client) {
		this.id_client=id_client;
	}
	
	

	private static String writeArray (ArrayList<Integer> list, String espacio) {
		String devuelvo="";
		for (Integer integer : list) {
			devuelvo=devuelvo +" [" + integer +"]" + espacio;
		}	
		return (devuelvo);
	}
	
	public void clientUp() throws NotBoundException {
		
		try {
			
			ArrayList<Integer>numerosA = new ArrayList<Integer>();
			ArrayList<Integer>numerosB = new ArrayList<Integer>();
			
			Registry clientRMI = LocateRegistry.getRegistry(_SERVER,_PORT);
			IRemoteInt iRserver = (IRemoteInt) clientRMI.lookup("service");
			
			numerosA.add(rnd.nextInt(20));numerosA.add(rnd.nextInt(20));numerosA.add(rnd.nextInt(20));numerosA.add(rnd.nextInt(20));
			numerosB.add(rnd.nextInt(20));numerosB.add(rnd.nextInt(20));numerosB.add(rnd.nextInt(20));numerosB.add(rnd.nextInt(20));
			System.out.println("Client n°" + id_client);
			System.out.println("Array 1 = " + writeArray(numerosA, " "));
			System.out.println("Array 2 = " + writeArray(numerosB, " "));
			System.out.print("SUMA = " + writeArray(iRserver.suma(numerosA,numerosB), " "));
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		}
		
	}




}
