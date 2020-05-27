package network_flex;



import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable, IControl{

	private int _PORT;
	private Registry serverRMI;
	private static ServerImplementer serverImplementer;
	private Logger log = LoggerFactory.getLogger(Server.class);
	private int position;
	private String _SERVER;
	private int tasks;
	public Server(int _PORT, int position, String _SERVER, int tasks) {
		super();
		this._PORT = _PORT;
		this.position = position;
		this._SERVER = _SERVER;
		this.tasks = tasks;
	}

	public Server(int _PORT) {
		this._PORT = _PORT;
	}
	
	private int generatePortToService(int _PORT) {
		return 10000+_PORT;
	}
	
			
	public void run() {
			
		try {
			serverRMI = LocateRegistry.createRegistry(_PORT);
			System.out.println("Server RMI has created as port: " + _PORT);
			serverImplementer = new ServerImplementer();
			IRemoteInt service = (IRemoteInt) UnicastRemoteObject.exportObject(serverImplementer,generatePortToService(_PORT));
			serverRMI.bind("serviceServer", service);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void serverStop() throws RemoteException {
		try {
			serverRMI.unbind("serviceServer");
			log.info("Server unbinded");
		} catch (RemoteException | NotBoundException e) {
			log.error("Error - Fail to unbind server");
		}
	}
	
	public String get_SERVER() {
		return _SERVER;
	}
	
	public int getTasks() {
		return tasks;
	}
	
	public String getDirection() {
		return (_SERVER + ":" + _PORT);
	}
	
	public void addTasks() {
		this.tasks++;
	}
	
	public void substracTasks() {
		this.tasks--;
	}

	public int getPosition() {
		return this.position;
	}
	
	public int getPort() {
		return this._PORT;
	}
}
