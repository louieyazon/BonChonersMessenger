package bcmBackend;
import java.util.LinkedList;

public class IPList extends LinkedList<String> {
	private static final long serialVersionUID = -2482412766406762970L;

	public String find(String ipToSearch) {
		for (String k: this) {
			if (ipToSearch.equals(k)) return k;
		}
		return null;
	}
	
	public String removeIP(String ipToRemove) {
		String ipt = this.find(ipToRemove);
		if (ipt != null) { this.remove(ipt); }
		return ipt;
	}
	
	
}