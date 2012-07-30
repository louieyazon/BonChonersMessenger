package bcmBackend;
import java.util.LinkedList;

public class IPList extends LinkedList<String> {
	private static final long serialVersionUID = -2482412766406762970L;

	public String find(String ipToSearch) {
		for (String k: this) {
			if (ipToSearch.equals(k)) {
				System.out.println(k + " found");
				return k;
				}
		}
		return null;
	}
	
	public String removeIP(String ipToRemove) {
		String ipt = this.find(ipToRemove);
		System.out.println("before deleting: " + this);
		if (ipt != null) { this.remove(ipt); }
		System.out.println("after deleting: " + this);
		return ipt;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String k: this) {
			sb.append(k + "  ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	
}