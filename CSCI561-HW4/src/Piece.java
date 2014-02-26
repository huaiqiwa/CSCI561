import java.util.ArrayList;

public class Piece {
	//player == 0 stands for player A
	//player == 1 stands for player B
	//hasBeen stores all the positions the piece has been
	private int player;
	private ArrayList<Position> hasBeen;
	
	public Piece(int thePlayer, int theX, int theY, boolean theKing) {
		player = thePlayer;
		hasBeen = new ArrayList<Position>();
		hasBeen.add(new Position(theX, theY, theKing));
	}
	
	public int getPlayer() {
		return player;
	}
	
	public Position getCurPos() {
		return hasBeen.get(hasBeen.size()-1);
	}
	
	public Position getPrePos() {
		return hasBeen.get(hasBeen.size()-2);
	}
	
	public ArrayList<Position> getHasBeen() {
		return hasBeen;
	}
	
	//return all the possible positions the piece is able to move at the current board
	public ArrayList<Position> posToMove(Piece[][] board) {
		ArrayList<Position> posToMove = new ArrayList<Position>();
		int x = this.getCurPos().getX();
		int y = this.getCurPos().getY();
		boolean king = this.getCurPos().getKing();
		
		//down, king or player B
		if(x+1 >= 0 && x+1 <= 7 && y+1 >= 0 && y+1 <= 7 && board[x+1][y+1] == null) {
			if(king || (this.getPlayer() == 1 && x+1 == 7 && !king)) {
				posToMove.add(new Position(x+1, y+1, true));
			} else if(this.getPlayer() == 1) {
				posToMove.add(new Position(x+1, y+1, false));
			}			
		}
		//down, king or player B
		if(x+1 >= 0 && x+1 <= 7 && y-1 >= 0 && y-1 <= 7 && board[x+1][y-1] == null) {
			if(king || (this.getPlayer() == 1 && x+1 == 7 && !king)) {
				posToMove.add(new Position(x+1, y-1, true));
			} else if(this.getPlayer() == 1) {
				posToMove.add(new Position(x+1, y-1, false));
			}		
		}
		//up, king or player A
		if(x-1 >= 0 && x-1 <= 7 && y+1 >= 0 && y+1 <= 7 && board[x-1][y+1] == null) {
			if(king || (this.getPlayer() == 0 && x-1 == 0 && !king)) {
				posToMove.add(new Position(x-1, y+1, true));
			} else if(this.getPlayer() == 0) {
				posToMove.add(new Position(x-1, y+1, false));
			}		
		}
		//up, king or player A
		if(x-1 >= 0 && x-1 <= 7 && y-1 >= 0 && y-1 <= 7 && board[x-1][y-1] == null) {
			if(king || (this.getPlayer() == 0 && x-1 == 0 && !king)) {
				posToMove.add(new Position(x-1, y-1, true));
			} else if(this.getPlayer() == 0) {
				posToMove.add(new Position(x-1, y-1, false));
			}			
		}
		
		return posToMove;
	}
	
	//return all the possible positions the piece is able to jump at the current board
	public ArrayList<Position> posToJump(Piece[][] board) {
		ArrayList<Position> posToJump = new ArrayList<Position>();
		int x = this.getCurPos().getX();
		int y = this.getCurPos().getY();
		boolean king = this.getCurPos().getKing();
		
		//down, king or player B
		if(x+2 >= 0 && x+2 <= 7 && y+2 >= 0 && y+2 <= 7 && board[x+2][y+2] == null
		   && board[x+1][y+1] != null && this.getPlayer() != board[x+1][y+1].getPlayer()) {
			if(king || (this.getPlayer() == 1 && x+2 == 7 && !king)) {
				posToJump.add(new Position(x+2, y+2, true));
			} else if(this.getPlayer() == 1) {
				posToJump.add(new Position(x+2, y+2, false));
			}			
		}
		//down, king or player B
		if(x+2 >= 0 && x+2 <= 7 && y-2 >= 0 && y-2 <= 7 && board[x+2][y-2] == null
		   && board[x+1][y-1] != null && this.getPlayer() != board[x+1][y-1].getPlayer()) {
			if(king || (this.getPlayer() == 1 && x+2 == 7 && !king)) {
				posToJump.add(new Position(x+2, y-2, true));
			} else if(this.getPlayer() == 1) {
				posToJump.add(new Position(x+2, y-2, false));
			}			
		}
		//up, king or player A
		if(x-2 >= 0 && x-2 <= 7 && y+2 >= 0 && y+2 <= 7 && board[x-2][y+2] == null
		   && board[x-1][y+1] != null && this.getPlayer() != board[x-1][y+1].getPlayer()) {
			if(king || (this.getPlayer() == 0 && x-2 == 0 && !king)) {
				posToJump.add(new Position(x-2, y+2, true));
			} else if(this.getPlayer() == 0) {
				posToJump.add(new Position(x-2, y+2, false));
			}				
		}
		//up, king or player A
		if(x-2 >= 0 && x-2 <= 7 && y-2 >= 0 && y-2 <= 7 && board[x-2][y-2] == null
		   && board[x-1][y-1] != null && this.getPlayer() != board[x-1][y-1].getPlayer()) {
			if(king || (this.getPlayer() == 0 && x-2 == 0 && !king)) {
				posToJump.add(new Position(x-2, y-2, true));
			} else if(this.getPlayer() == 0) {
				posToJump.add(new Position(x-2, y-2, false));
			}			
		}
		
		return posToJump;
	}
	
	public void addHasBeen(Position position) {
		hasBeen.add(position);
	}
	
	public void removeLastPos() {
		hasBeen.remove(hasBeen.size()-1);
	}
	
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (getClass() != otherObject.getClass()) {
			return false; 
		}
		Piece other = (Piece) otherObject;
		
		if(this.getHasBeen().size() != other.getHasBeen().size()) {
			return false;
		}
		
		for(int i = 0; i < this.getHasBeen().size(); i++) {
			if(!this.getHasBeen().get(i).equals(other.getHasBeen().get(i))) {
				return false;
			}
		}
		
		return true;		
	}
	
}




















