//used to record the positions one piece has been,
//x, y are coordinates and king stands for whether at this coordinate the piece has become king
public class Position {
	private int x;
	private int y;
	private boolean king;
	
	public Position(int theX, int theY, boolean theKing) {
		x = theX;
		y = theY;
		king = theKing;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean getKing() {
		return king;
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (getClass() != otherObject.getClass()) {
			return false; 
		}
		Position other = (Position) otherObject;
		
		if(this.getX() == other.getX() && this.getY() == other.getY() && this.getKing() == other.getKing()) {
			return true;
		}
		
		return false;		
	}
}