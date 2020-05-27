package network_flex;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerImplementer implements IRemoteInt {

	@Override
	public ArrayList<Integer> suma(ArrayList<Integer> numerosA, ArrayList<Integer> numerosB) throws RemoteException {
		ArrayList<Integer> numerosC = new ArrayList<Integer>();
		
		Iterator<Integer> iteratorA = numerosA.iterator();
		Iterator<Integer> iteratorB = numerosB.iterator();
		
		while(iteratorA.hasNext() && iteratorB.hasNext()) {
			numerosC.add(iteratorA.next()+iteratorB.next());
		}
		
		while(iteratorA.hasNext()) {
			numerosC.add(iteratorA.next());
		}
		
		while(iteratorB.hasNext()) {
			numerosC.add(iteratorB.next());
		}
		return numerosC;
	}



}
