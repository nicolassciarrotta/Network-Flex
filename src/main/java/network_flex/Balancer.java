package network_flex;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Balancer{


	/*
	 * @Threshold definition
	 * 0 clients -> light 
	 * 0-3 clients -> normal
	 * 4 clients -> alert
	 * 5 clients -> critic
	 * 
	 */
	
	private int _LIGHT = 0; 
	private int _NORMAL= 3; 
	private int _ALERT = 4;
	private int _CRITIC = 5;
	private int _PORT = 8000;
	private int _INITIALSERVERS = 3;
	private String _SERVER = "localhost";
	private ArrayList<Server> listServer = new ArrayList<Server>();
	private static Logger log = LoggerFactory.getLogger(Balancer.class);
	
	
	private int generatePort(int endPort) {
		if (endPort>9) {
			return (9020 + endPort);
		}else
			return(9010 + endPort);
	}
		
	/*
	 * create a new node and new thread
	 */
	public Server newServer(){
		Server server = null;
			synchronized(listServer){
				//Assigned the last position synchronized
				server = new Server(generatePort(listServer.size()),listServer.size(),_SERVER,_LIGHT);
					
				Thread tserver = new Thread(server); //Create a new Thread
				tserver.start();
				listServer.add(server);
				}
		log.info("Server "+ server.getDirection() +" created");	
		return server;
	}
	
	
	public void stopServer(Server server) throws NotBoundException {
		try {
			Registry registry = LocateRegistry.getRegistry(server.getPort());
			IControl remoteInt = (IControl) registry.lookup("service");
			remoteInt.serverStop();
			synchronized(listServer) {
				listServer.remove(server);
			}
			
		} catch (NumberFormatException |RemoteException e) {
			log.error("Error - Can't stop server");
		}
	}
	
	/*
	 *Assign task depending node load
	 */
	
	public synchronized  Server assignTask() {
		Collections.sort(listServer, new SortMyList());
		//get first on the list
		Server server = listServer.get(0);
		String direction = server.getDirection();
			if(server.getTasks()<=_NORMAL) {
				server.addTasks();
				log.info("SERVER " + server.getDirection() + " STATUS is NORMAL");
				log.info("Task assigned to " + server.getDirection());
			}else if((server.getTasks()>=_ALERT) && (server.getTasks()<_CRITIC)) { 
				server.addTasks();
				log.info("SERVER " + server.getDirection() + " STATUS is ALERT");
				log.info("Task assigned to " + server.getDirection());
			}else if(server.getTasks()==_CRITIC){
				Server nNew = newServer();
				server.addTasks();
				log.info("SERVER " + direction + " STATUS is CRITIC. This server isn't work for now");
				log.info("New server has created as " + nNew.getDirection());
				log.info("Task assigned to " + nNew.getDirection());
			}
		
			
		
	return server;
	}
	
	
	public synchronized void terminateTask(int serverPosition) throws NotBoundException, InterruptedException {
		synchronized (listServer) {
			listServer.get(serverPosition).substracTasks();
			log.info("Task terminated by server: " + listServer.get(serverPosition).getDirection());
			if ((listServer.get(serverPosition).getTasks()==0) && (listServer.size()>_INITIALSERVERS)) {
				stopServer(listServer.get(serverPosition));
			}
	}

	}
	
	
	public void initBalancer() throws RemoteException {
		newServer();
		newServer();
		Remote iRemote = UnicastRemoteObject.exportObject( new IRemoteInt() {
		
			@Override
			public ArrayList<Integer> suma(ArrayList<Integer> numerosA, ArrayList<Integer> numerosB)
					throws RemoteException {
					Registry registry;
					ArrayList<Integer> resultado = new ArrayList<Integer>();
					try {
						Server server = assignTask();
						int i = server.getPosition();
						registry = LocateRegistry.getRegistry(server.getPort());
						IRemoteInt iRBalancer = (IRemoteInt) registry.lookup("serviceServer");
						resultado=iRBalancer.suma(numerosA, numerosB);
						
						terminateTask(i);
					}catch (Exception e) {
						log.error("ERROR IN BALANCER " + e.getMessage());
					}
				return resultado;
			}
			
		},0);
		
		if (listServer.size()>1) {
			Registry registry = LocateRegistry.createRegistry(_PORT);
			registry.rebind("service", iRemote);
			log.info("Balancer RMI initiated on port " + _PORT);
		}
		
	}
	
	
}