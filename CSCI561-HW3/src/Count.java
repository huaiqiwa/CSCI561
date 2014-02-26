//calculate the runtime which is measured by counting the number of iterations through the WalkSAT algorithm
public class Count {
	private int count;
	
	public Count() {
		count = 0;
	}
	
	public void setValue(int value) {
		count = value;
	}
	
	public int getValue() {
		return count;
	}
}