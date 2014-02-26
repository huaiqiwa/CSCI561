import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class hw4 {
	private static Piece[][] board;
	private static ArrayList<Piece> aPieces;
	private static ArrayList<Piece> bPieces;
	private static final int MIN = -2147483648;
	private static final int MAX = 2147483647;
		
	public static void main(String[] args) throws FileNotFoundException {
		File inFile = new File(args[0]);
		Scanner in = new Scanner(inFile);
		PrintWriter out = new PrintWriter(args[1]);
		String line;
		
		while (in.hasNextLine()) {
			line = in.nextLine();
			if(line.length() > 0 && line.charAt(0) == 'c') {
				out.println(line);
				board = new Piece[8][8];
				int alpha = MIN;
				int beta = MAX;
				aPieces = new ArrayList<Piece>();
				bPieces = new ArrayList<Piece>();
				
				//read the data of one case
				for(int i = 0; i < 8; i++) {
					line = in.nextLine();
					for(int j = 0; j < 8; j++) {
						if(line.charAt(j) == 'A') {
							board[i][j] = new Piece(0, i, j, false);
							aPieces.add(board[i][j]);
						} else if(line.charAt(j) == 'B') {
							board[i][j] = new Piece(1, i, j, false);
							bPieces.add(board[i][j]);
						} else if(line.charAt(j) == 'k') {
							board[i][j] = new Piece(0, i, j, true);
							aPieces.add(board[i][j]);
						} else if(line.charAt(j) == 'K') {
							board[i][j] = new Piece(1, i, j, true);
							bPieces.add(board[i][j]);
						} else {
							//no piece on (i, j), assign null to board[i][j]
							board[i][j] = null;
						}					
					}
				}
				
				//play one case
				int count = 0;			
				maxPlay(count, out, alpha, beta);
				out.println();
			}
		}
		
		out.close();
	}
	
	//Player A's moves
	private static int maxPlay(int count, PrintWriter out, int theAlpha, int theBeta) {
		boolean jumped = false;
		boolean moved = false;
		Piece piece;
		ArrayList<Position> posTo;
		int preX;
		int preY;
		int curX;
		int curY;
		int x;
		int y;
		int alpha = theAlpha;
		int beta = theBeta;
		Position maxPositionFrom = null;
		Position maxPositionTo = null;
		
		//try all A's pieces' possible jumps
		for(int i = 0; i < aPieces.size(); i++) {
			piece = aPieces.get(i);
			posTo = piece.posToJump(board);
			Position position;
			Piece removedPiece;
			
			//count == 0 stands for A's first move, and print the information
			//count == 1 stands for A's second move, and print the information
			for(int j = 0; j < posTo.size(); j++) {
				jumped = true;
				position = posTo.get(j);
				piece.addHasBeen(position);
				if(count == 0) {
					out.println("A1:  " + piece.getPrePos() + "=>" + piece.getCurPos() + ".");
				}
				if(count == 1) {
					out.println("|-|-A3:  " + piece.getPrePos() + "=>" + piece.getCurPos() + ".");
				}
				preX = piece.getPrePos().getX();
				preY = piece.getPrePos().getY();
				curX = piece.getCurPos().getX();
				curY = piece.getCurPos().getY();
				board[curX][curY] = board[preX][preY];
				board[preX][preY] = null;
				x = (preX + curX) / 2;
				y = (preY + curY) / 2;
				//record the information of the removed B's piece
				removedPiece = board[x][y];
				int index = bPieces.indexOf(removedPiece);
				
				bPieces.remove(board[x][y]);
				board[x][y] = null;
				
				int v = minPlay(count+1, out, alpha, beta);
				
				//record the optimal first move of A
				if(count == 0 && v > alpha) {
					maxPositionFrom = piece.getPrePos();
					maxPositionTo = piece.getCurPos();
				}
				alpha = max(alpha, v);
				
				//restore the status of the board before this move, and add back B's piece removed
				piece.removeLastPos();
				board[preX][preY] = board[curX][curY];
				board[curX][curY] = null;
				board[x][y] = removedPiece;
				bPieces.add(index, board[x][y]);
				
				//do alpha-beta pruning
				if(alpha >= beta) {
					//flag whether the first pruning move has been done
					boolean firstDone = false;
					
					for(int m = i; m < aPieces.size(); m++) {
						piece = aPieces.get(m);
						posTo = piece.posToJump(board);
						if(m == i) {
							for(int n = j+1; n < posTo.size(); n++) {
								if(!firstDone) {
									if(count == 0) {
										out.print("A1:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
									if(count == 1) {
										out.print("|-|-A3:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
								}
								if(firstDone) {
									out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
								}
								firstDone = true;
							}
						} else {
							for(int n = 0; n < posTo.size(); n++) {
								if(!firstDone) {
									if(count == 0) {
										out.print("A1:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
									if(count == 1) {
										out.print("|-|-A3:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
								}
								if(firstDone) {
									out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
								}
								firstDone = true;
							}
						}									
					}
					
					if(firstDone) {
						out.println("; alpha=" + alpha + ", beta=" + beta + ".");
					}
					return beta;
				}
			}
		}
		
		//if no possible jumps, try all A's pieces' possible simple moves
		if(!jumped) {
			for(int i = 0; i < aPieces.size(); i++) {
				piece = aPieces.get(i);
				posTo = piece.posToMove(board);
				Position position;
				
				for(int j = 0; j < posTo.size(); j++) {
					moved = true;
					position = posTo.get(j);
					piece.addHasBeen(position);
					preX = piece.getPrePos().getX();
					preY = piece.getPrePos().getY();
					curX = piece.getCurPos().getX();
					curY = piece.getCurPos().getY();
					board[curX][curY] = board[preX][preY];
					board[preX][preY] = null;
					if(count == 0) {
						out.println("A1:  " + piece.getPrePos() + "=>" + piece.getCurPos() + ".");
					}
					if(count == 1) {
						out.println("|-|-A3:  " + piece.getPrePos() + "=>" + piece.getCurPos() + ".");
					}
					
					int v = minPlay(count+1, out, alpha, beta);
					
					//record the optimal first move of A
					if(count == 0 && v > alpha) {
						maxPositionFrom = piece.getPrePos();
						maxPositionTo = piece.getCurPos();
					}
					alpha = max(alpha, v);
					
					//restore the status of the board before this move
					piece.removeLastPos();
					board[preX][preY] = board[curX][curY];
					board[curX][curY] = null;
					
					//do alpha-beta pruning
					if(alpha >= beta) {
						//flag whether the first pruning move has been done
						boolean firstDone = false;
						
						for(int m = i; m < aPieces.size(); m++) {
							piece = aPieces.get(m);
							posTo = piece.posToMove(board);
							if(m == i) {
								for(int n = j+1; n < posTo.size(); n++) {
									if(!firstDone) {
										if(count == 0) {
											out.print("A1:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
										if(count == 1) {
											out.print("|-|-A3:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
									}
									if(firstDone) {
										out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
									}
									firstDone = true;
								}
							} else {
								for(int n = 0; n < posTo.size(); n++) {
									if(!firstDone) {
										if(count == 0) {
											out.print("A1:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
										if(count == 1) {
											out.print("|-|-A3:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
									}
									if(firstDone) {
										out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
									}
									firstDone = true;
								}
							}									
						}
						
						if(firstDone) {
							out.println("; alpha=" + alpha + ", beta=" + beta + ".");
						}
						return beta;
					}
				}
			}
		}
		
		if(count == 0 && maxPositionFrom != null && maxPositionTo != null) {
			out.println();
			out.println("first move: " + maxPositionFrom + "=>" + maxPositionTo);
		}
		
		//cannot move
		if(!jumped && !moved) {
			return MIN+1;
		}
		
		return alpha;
	}
	
	//Player B's moves
	private static int minPlay(int count, PrintWriter out, int theAlpha, int theBeta) {
		boolean jumped = false;
		boolean moved = false;
		Piece piece;
		ArrayList<Position> posTo;
		int preX;
		int preY;
		int curX;
		int curY;
		int x;
		int y;
		int alpha = theAlpha;
		int beta = theBeta;
		
		//try all B's pieces' possible jumps
		for(int i = 0; i < bPieces.size(); i++) {
			piece = bPieces.get(i);
			posTo = piece.posToJump(board);
			Position position;
			Piece removedPiece;
			
			//count == 1 stands for B's first move, and print the information
			//count == 2 stands for B's second move, and print the information and the utility value after each possible move
			for(int j = 0; j < posTo.size(); j++) {
				jumped = true;
				position = posTo.get(j);
				piece.addHasBeen(position);
				if(count == 1) {
					out.println("|-B2:  " + piece.getPrePos() + "=>" + piece.getCurPos() + ".");
				}
				preX = piece.getPrePos().getX();
				preY = piece.getPrePos().getY();
				curX = piece.getCurPos().getX();
				curY = piece.getCurPos().getY();
				board[curX][curY] = board[preX][preY];
				board[preX][preY] = null;
				x = (preX + curX) / 2;
				y = (preY + curY) / 2;
				
				//record the information of the removed A's piece
				removedPiece = board[x][y];
				int index = aPieces.indexOf(removedPiece);
				
				aPieces.remove(board[x][y]);
				board[x][y] = null;
				if(count == 2) {
					beta = min(beta, getUtility());
					out.println("|-|-|-B4:  " + piece.getPrePos() + "=>" + piece.getCurPos() + "; " + "h=" + beta + ".");
				} else {
					beta = min(beta, maxPlay(count, out, alpha, beta));
				}		
				
				//restore the status of the board before this move, and add back A's piece removed
				piece.removeLastPos();
				board[preX][preY] = board[curX][curY];
				board[curX][curY] = null;
				board[x][y] = removedPiece;
				aPieces.add(index, board[x][y]);
				
				//do alpha-beta pruning
				if(beta <= alpha) {
					//flag whether the first pruning move has been done
					boolean firstDone = false;
					
					for(int m = i; m < bPieces.size(); m++) {
						piece = bPieces.get(m);
						posTo = piece.posToJump(board);
						if(m == i) {
							for(int n = j+1; n < posTo.size(); n++) {
								if(!firstDone) {
									if(count == 1) {
										out.print("|-B2:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
									if(count == 2) {
										out.print("|-|-|-B4:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
								}
								if(firstDone) {
									out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
								}				
								firstDone = true;
							}
						} else {
							for(int n = 0; n < posTo.size(); n++) {
								if(!firstDone) {
									if(count == 1) {
										out.print("|-B2:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
									if(count == 2) {
										out.print("|-|-|-B4:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
									}
								}
								if(firstDone) {
									out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
								}	
								firstDone = true;
							}
						}									
					}
					
					if(firstDone) {
						out.println("; alpha=" + alpha + ", beta=" + beta + ".");
					}
					return alpha;
				}
			}
		}
		
		//if no possible jumps, try all B's pieces' possible simple moves
		if(!jumped) {
			for(int i = 0; i < bPieces.size(); i++) {
				piece = bPieces.get(i);
				posTo = piece.posToMove(board);
				Position position;
				
				for(int j = 0; j < posTo.size(); j++) {
					moved = true;
					position = posTo.get(j);
					piece.addHasBeen(position);
					if(count == 1) {
						out.println("|-B2:  " + piece.getPrePos() + "=>" + piece.getCurPos() + ".");
					}
					preX = piece.getPrePos().getX();
					preY = piece.getPrePos().getY();
					curX = piece.getCurPos().getX();
					curY = piece.getCurPos().getY();
					board[curX][curY] = board[preX][preY];
					board[preX][preY] = null;
					if(count == 2) {
						beta = min(beta, getUtility());
						out.println("|-|-|-B4:  " + piece.getPrePos() + "=>" + piece.getCurPos() + "; " + "h=" + beta + ".");
					} else {
						beta = min(beta, maxPlay(count, out, alpha, beta));
					}					

					//restore the status of the board before this move
					piece.removeLastPos();
					board[preX][preY] = board[curX][curY];
					board[curX][curY] = null;
					
					//do alpha-beta pruning
					if(beta <= alpha) {
						//flag whether the first pruning move has been done
						boolean firstDone = false;
						
						for(int m = i; m < bPieces.size(); m++) {
							piece = bPieces.get(m);
							posTo = piece.posToMove(board);
							if(m == i) {
								for(int n = j+1; n < posTo.size(); n++) {
									if(!firstDone) {
										if(count == 1) {
											out.print("|-B2:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
										if(count == 2) {
											out.print("|-|-|-B4:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
									}
									if(firstDone) {
										out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
									}				
									firstDone = true;
								}
							} else {
								for(int n = 0; n < posTo.size(); n++) {
									if(!firstDone) {
										if(count == 1) {
											out.print("|-B2:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
										if(count == 2) {
											out.print("|-|-|-B4:  pruning " + piece.getCurPos() + "=>" + posTo.get(n));
										}
									}
									if(firstDone) {
										out.print(", " + piece.getCurPos() + "=>" + posTo.get(n));		
									}
									firstDone = true;
								}
							}									
						}
						
						if(firstDone) {
							out.println("; alpha=" + alpha + ", beta=" + beta + ".");
						}
						return alpha;
					}
				}
			}
		}
		
		//cannot move
		if(!jumped && !moved) {
			return MAX-1;
		}
		
		return beta;
	}
	
	private static int max(int a, int b) {
		if(a > b) {
			return a;
		} else {
			return b;
		}
	}
	
	private static int min(int a, int b) {
		if(a < b) {
			return a;
		} else {
			return b;
		}
	}
	
	//return the utility value of the current board
	private static int getUtility() {
		int aScore = 0;
		int bScore = 0;
		Piece piece;
		
		for(int i = 0; i < aPieces.size(); i++) {
			piece = aPieces.get(i);
			if(piece.getCurPos().getKing()) {
				aScore += 2;
			} else {
				aScore++;
			}
		}
		for(int i = 0; i < bPieces.size(); i++) {
			piece = bPieces.get(i);
			if(piece.getCurPos().getKing()) {
				bScore += 2;
			} else {
				bScore++;
			}
		}
		
		return aScore - bScore;
	}
	
}



















