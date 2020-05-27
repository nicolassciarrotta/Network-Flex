package network_flex;

import java.util.Comparator;

public class SortMyList implements Comparator<Server>{

	@Override
	public int compare(Server o1, Server o2) {
		return new Integer(o1.getTasks()).compareTo(new Integer(o2.getTasks()));
	}

}
