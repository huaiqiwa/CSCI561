import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class Clause {
	//store positive literals and negative literals separately
	private ArrayList<Literal> posLiterals;
	private ArrayList<Literal> negLiterals;
	private Set<Literal> posLiteralsSet;
	private Set<Literal> negLiteralsSet;
	private static final int HASH_MULTIPLIER = 29;
	
	public Clause() {
		posLiterals = new ArrayList<Literal>();
		negLiterals = new ArrayList<Literal>();
		posLiteralsSet = new HashSet<Literal>();
		negLiteralsSet = new HashSet<Literal>();
	}
	
	public int hashCode() {
		int hashValue = 0;
		if(posLiterals.size() > 0) {
			hashValue = posLiterals.get(0).hashCode(); 
			for(int i = 1; i < posLiterals.size(); i++) {
				hashValue = HASH_MULTIPLIER * hashValue + posLiterals.get(i).hashCode();
			}
			for(int i = 0; i < negLiterals.size(); i++) {
				hashValue = HASH_MULTIPLIER * hashValue + negLiterals.get(i).hashCode();
			}
		} else {
			if(negLiterals.size() > 0) {
				hashValue = negLiterals.get(0).hashCode(); 
				for(int i = 1; i < negLiterals.size(); i++) {
					hashValue = HASH_MULTIPLIER * hashValue + negLiterals.get(i).hashCode();
				}
			}
		}
		
		return hashValue;
	}
	
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (getClass() != otherObject.getClass()) {
			return false; 
		}
		Clause other = (Clause) otherObject;
		
		if(this.getPosLiterals().size() != other.getPosLiterals().size() 
		   || this.getNegLiterals().size() != other.getNegLiterals().size()) {
			return false;
		}
		
		for(int i = 0; i < this.getPosLiterals().size(); i++) {
			if(!this.getPosLiterals().get(i).equals(other.getPosLiterals().get(i))) {
				return false;
			}
		}
		for(int i = 0; i < this.getNegLiterals().size(); i++) {
			if(!this.getNegLiterals().get(i).equals(other.getNegLiterals().get(i))) {
				return false;
			}
		}
		
		return true;		
	}
	
	public ArrayList<Literal> getPosLiterals() {
		return posLiterals;
	}
	
	public ArrayList<Literal> getNegLiterals() {
		return negLiterals;
	}
	
	public Set<Literal> getPosLiteralsSet() {
		return posLiteralsSet;
	}
	
	public Set<Literal> getNegLiteralsSet() {
		return negLiteralsSet;
	}
	
	public void addPosLiterals(Literal theLiteral) {
		if(posLiteralsSet.add(theLiteral)) {
			posLiterals.add(theLiteral);
		}		
	}
	
	public void addNegLiterals(Literal theLiteral) {
		if(negLiteralsSet.add(theLiteral)) {
			negLiterals.add(theLiteral);
		}		
	}
	
	//return null when the clause generated is meaningless, otherwise return the clause generated
	public Clause resolve(Clause theClause) {
		class LiteralComparator implements Comparator<Literal> {
			public int compare(Literal a, Literal b) {
				if(a.getPerson() != b.getPerson()) {
					return a.getPerson() - b.getPerson();
				} else {
					return a.getTable() - b.getTable();
				}
				
			} 
		}
		
		ArrayList<Literal> pos = theClause.getPosLiterals();
		ArrayList<Literal> neg = theClause.getNegLiterals();
		Set<Literal> negSet = theClause.getNegLiteralsSet();
		Set<Literal> posSet = theClause.getPosLiteralsSet();
		Literal toRemove = new Literal(-2, -2); //get the code to compile
		boolean firstFound = false;
		boolean secondFound = false;
		
		for(int i = 0; i < posLiterals.size(); i++) {
			Literal tempLiteral = posLiterals.get(i);
			if(negSet.contains(tempLiteral)) {
				toRemove = tempLiteral;
				if(!firstFound) {
					firstFound = true;
				} else {
					secondFound = true;
					break;
				}
			}
		}
		
		for(int i = 0; i < pos.size(); i++) {
			Literal tempLiteral = pos.get(i);
			if(negLiteralsSet.contains(tempLiteral)) {
				toRemove = tempLiteral;
				if(!firstFound) {
					firstFound = true;
				} else {
					secondFound = true;
					break;
				}
			}
		}
		
		//found two pairs of complementary literals, ignore the clause generated
		if(firstFound && secondFound) {
			return null;
		}
		
		if(firstFound) {
			Clause newClause = new Clause();
			
			if(posLiteralsSet.contains(toRemove) && negSet.contains(toRemove)) {
				for(int i = 0; i < posLiterals.size(); i++) {
					if(!posLiterals.get(i).equals(toRemove)) {
						newClause.addPosLiterals(posLiterals.get(i));
					}				
				}
				for(int i = 0; i < pos.size(); i++) {
					newClause.addPosLiterals(pos.get(i));
				}
				for(int i = 0; i < negLiterals.size(); i++) {
					newClause.addNegLiterals(negLiterals.get(i));
				}
				for(int i = 0; i < neg.size(); i++) {
					if(!neg.get(i).equals(toRemove)) {
						newClause.addNegLiterals(neg.get(i));
					}		
				}
			}
			
			if(negLiteralsSet.contains(toRemove) && posSet.contains(toRemove)) {
				for(int i = 0; i < posLiterals.size(); i++) {
					newClause.addPosLiterals(posLiterals.get(i));
				}
				for(int i = 0; i < pos.size(); i++) {
					if(!pos.get(i).equals(toRemove)) {
						newClause.addPosLiterals(pos.get(i));
					}					
				}
				for(int i = 0; i < negLiterals.size(); i++) {
					if(!negLiterals.get(i).equals(toRemove)) {
						newClause.addNegLiterals(negLiterals.get(i));
					}	
				}
				for(int i = 0; i < neg.size(); i++) {
					newClause.addNegLiterals(neg.get(i));
				}
			}
			
			Collections.sort(newClause.getPosLiterals(), new LiteralComparator());
			Collections.sort(newClause.getNegLiterals(), new LiteralComparator());
			
			/*for(int i = 0; i < posLiterals.size(); i++) {
				Literal tempLiteral = posLiterals.get(i);
				System.out.print(tempLiteral.getPerson() + " " + tempLiteral.getTable() + ", ");
			}
			for(int i = 0; i < negLiterals.size(); i++) {
				Literal tempLiteral = negLiterals.get(i);
				System.out.print(tempLiteral.getPerson() + " " + tempLiteral.getTable() + ", ");
			}
			System.out.println();*/
			
			return newClause;
		}
		
		return null;
	}
	
	public boolean getTruth(boolean[][] bX) {
		Literal tempLiteral;
		for(int i = 0; i < posLiterals.size(); i++) {
			tempLiteral = posLiterals.get(i);
			if(bX[tempLiteral.getPerson()-1][tempLiteral.getTable()-1]) {
				return true;
			}
		}
		for(int i = 0; i < negLiterals.size(); i++) {
			tempLiteral = negLiterals.get(i);
			if(!bX[tempLiteral.getPerson()-1][tempLiteral.getTable()-1]) {
				return true;
			}
		}
		return false;
	}
	
	
}














