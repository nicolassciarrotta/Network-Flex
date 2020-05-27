package network_flex;

import java.rmi.Remote;
import java.util.ArrayList;

public interface IRemoteInt extends Remote{

	ArrayList<Integer> suma(ArrayList<Integer> numerosA, ArrayList<Integer> numerosB)  throws java.rmi.RemoteException;
}
