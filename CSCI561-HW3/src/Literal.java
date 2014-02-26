public class Literal {
	private int person;
	private int table;
	private static final int HASH_MULTIPLIER = 29;
	
	public Literal(int m, int n) {
		person = m + 1;
		table = n + 1;
	}
	
	public int getPerson() {
		return person;
	}
	
	public int getTable() {
		return table;
	}
	
	public int hashCode() {
		int hashValue = HASH_MULTIPLIER * person + table;
		hashValue = HASH_MULTIPLIER * hashValue + person + table;
		return hashValue;
	}

	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (getClass() != otherObject.getClass()) {
			return false; 
		}
		Literal other = (Literal) otherObject;
		
		if(this.getPerson() == other.getPerson() && this.getTable() == other.getTable()) {
			return true;
		}
		
		return false;		
	}
}