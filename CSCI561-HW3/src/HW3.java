import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Scanner;

public class HW3 {
	private static Random randomGen;
	private static int[][] R;
	private static boolean[][] bX;
	private static Literal[][] X;
	private static Set<Clause> clauses;
	private static ArrayList<Clause> aclauses;
	
	//on the command line, enter exp1, exp2, exp3 to do experiments
	public static void main(String[] args) {
		randomGen = new Random();
		
		Scanner in = new Scanner(System.in);
		String command;
		int M;
		int N;
		
		do
		{
			System.out.print("cmd> ");
			command = in.nextLine();
			if(command.equals("exp1")) {
				M = 16;
				N = 2;
				R = new int[M][M];
				Count count = new Count();
				
				for(double e = 0.02; e < 0.22; e += 0.02) {
					e = (double)Math.round(e * 100) / 100;
					int pl = 0;
					int walk = 0;
					for(int i = 0; i < 100; i++) {
						instanceGenerator(16, 0.0, e);
						clauseGenerator(M, N);
						if(plResolution()) {
							pl++;
							//System.out.print(pl + " ");
						}
						
						clauseGenerator(M, N);
						if(walkSAT(M, N, 0.5, 100, count)) {
							walk++;
						}
						
					}
					System.out.println("e: " + e + " ~ " + pl/100.0 + ", " + walk/100.0);
				}
				
			} else if(command.equals("exp2")) {
				M = 16;
				N = 2;
				R = new int[M][M];
				Count count = new Count();
				
				for(double f = 0.02; f < 0.22; f += 0.02) {
					f = (double)Math.round(f * 100) / 100;
					int walk100 = 0;
					int walk500 = 0;
					int walk1000 = 0;
					
					for(int i = 0; i < 100; i++) {
						instanceGenerator(16, f, 0.05);
						clauseGenerator(M, N);
						if(walkSAT(M, N, 0.5, 100, count)) {
							walk100++;
						}
						if(walkSAT(M, N, 0.5, 500, count)) {
							walk500++;
						}
						if(walkSAT(M, N, 0.5, 1000, count)) {
							walk1000++;
						}
					}
					System.out.println("e: 0.05 f: " + f + " ~ " + walk100/100.0 
									 + ", "  + walk500/100.0 + ", "  + walk1000/100.0);
				}
				
			} else if(command.equals("exp3")) {
				for(M = 16, N = 2; M < 56 && N < 7; M += 8, N++) {
					R = new int[M][M];
					int numIter = 0;
					int numClauses = 0;
					int numSymbol = M * N;
					Count count = new Count();
					
					for(int i = 0; i < 100; ) {
						instanceGenerator(M, 0.02, 0.02);
						clauseGenerator(M, N);
						
						if(walkSAT(M, N, 0.5, 1000, count)){
							i++;
							numIter += count.getValue();
							numClauses += aclauses.size();
						}
		
					}
					
					double averageRatio = numClauses / (numSymbol * 100.0);
					System.out.println("M: " + M + " N: " + N + " ~ " + numIter/100.0 + ", "  
										+ (double)Math.round(averageRatio * 100) / 100);
				}
				
			} else if(!command.equals("quit")) {
				System.out.println("Please enter exp1 or exp2 or exp3");
			}
		}
		while(!command.equals("quit"));
	}

	
	private static boolean plResolution() {
		Clause one;
		Clause two;
		Clause result;
		Set<Clause> newClauses = new HashSet<Clause>();
		
		while(true) {
			newClauses.clear();
			
			for(int i = 0; i < aclauses.size(); i++) {
				one = aclauses.get(i);
				for(int j = i + 1; j < aclauses.size(); j++) {
					two = aclauses.get(j);
					result = one.resolve(two);
					//if the clause generated is meaningful
					if(result != null) {
						//empty clause is derived
						if(result.getNegLiterals().size() == 0 && result.getPosLiterals().size() == 0) {
							return false;
						} else {
							if(!clauses.contains(result)) {
								newClauses.add(result);
							}
						}
					}
				}
			}
			
			if(newClauses.isEmpty()) {
				return true;
			} else {
				//System.out.println(newClauses.size());
				Iterator<Clause> iter = newClauses.iterator();
				while(iter.hasNext()) {
					Clause tempClause = iter.next();
					if(clauses.add(tempClause)) {
						aclauses.add(tempClause);
					}		
				}
			}
		}
	}
	
	private static boolean walkSAT(int M, int N, double p, int maxFlips, Count count) {
		randomAssignGen(M, N);
		
		for(int i = 0; i < maxFlips; i++) {
			ArrayList<Clause> falseClauses = new ArrayList<Clause>();
			Iterator<Clause> iter = clauses.iterator();
			
			while(iter.hasNext()) {
				Clause clause = iter.next();
				if(!clause.getTruth(bX)) {
					falseClauses.add(clause);
				}
			}
			
			if(falseClauses.size() == 0) {
				count.setValue(i);
				return true;
			}
			
			Clause randomClause = falseClauses.get(randomGen.nextInt(falseClauses.size()));
			ArrayList<Literal> literals = new ArrayList<Literal>();
			literals.addAll(randomClause.getPosLiterals());
			literals.addAll(randomClause.getNegLiterals());
			double tempProb = randomGen.nextDouble();
			if(tempProb < p) {
				int random = randomGen.nextInt(literals.size());
				Literal randomLiteral;
						
				randomLiteral = literals.get(random);
				int m = randomLiteral.getPerson();
				int n = randomLiteral.getTable();
				flip(m, n, bX);			
				
			} else {
				Literal maxLiteral = null;
				int max = 0;
				Literal tempLiteral;
				int m;
				int n;
				int temp;
				for(int j = 0; j < literals.size(); j++) {
					tempLiteral = literals.get(j);
					m = tempLiteral.getPerson();
					n = tempLiteral.getTable();
					flip(m, n, bX);
					temp = numOfTrueClauses();
					flip(m, n, bX);
					if(temp > max) {
						max = temp;
						maxLiteral = tempLiteral;
					}
				}
				if(maxLiteral != null) {
					flip(maxLiteral.getPerson(), maxLiteral.getTable(), bX);
				}			
			}
		}
		
		return false;
	}
	
	private static void flip(int m, int n, boolean[][] bX) {
		if(bX[m-1][n-1]) {
			bX[m-1][n-1] = false;
		} else {
			bX[m-1][n-1] = true;
		}	
	}
	
	private static void randomAssignGen(int M, int N) {
		bX = new boolean[M][N];
		double tempProb;
				
		for(int i = 0; i < M; i++) {
			for(int j = 0; j < N; j++) {
				tempProb = randomGen.nextDouble();
				if(tempProb < 0.5) {
					bX[i][j] = true;
				} else {
					bX[i][j] = false;
				}
			}
		}
	}
	
	private static void	instanceGenerator(int M, double f, double e) {
		double tempProb;
		
		for(int i = 0; i < M; i++) {
			for(int j = i + 1; j < M; j++) {
				tempProb = randomGen.nextDouble();
				if(tempProb < f) {
					R[i][j] = 1;
				} else if(tempProb < f + e) {
					R[i][j] = -1;
				} else {
					R[i][j] = 0;
				}
			}
		}			
	}
	
	private static void clauseGenerator(int M, int N) {
		clauses = new HashSet<Clause>();
		aclauses = new ArrayList<Clause>();
		X = new Literal[M][N];
		Clause tempClause;
		
		for(int i = 0; i < M; i++) {
			for(int j = 0; j < N; j++) {				
				X[i][j] = new Literal(i, j);													
			}
		}
		
		//(a) Each guest i should be seated at one and only one table.
		for(int i = 0; i < M; i++) {
			tempClause = new Clause();
			for(int j = 0; j < N; j++) {
				tempClause.addPosLiterals(X[i][j]);
			}
			if(clauses.add(tempClause)) {
				aclauses.add(tempClause);
			}			
		}
		
		for(int i = 0; i < M; i++) {
			for(int j = 0; j < N; j++) {
				for(int k = j + 1; k < N; k++) {
					tempClause = new Clause();
					tempClause.addNegLiterals(X[i][j]);
					tempClause.addNegLiterals(X[i][k]);
					if(clauses.add(tempClause)) {
						aclauses.add(tempClause);
					}				
				}
			}
		}
		
		for(int i = 0; i < M; i++) {
			for(int j = i + 1; j < M; j++) {
				//(b) Any two guests i and j who are Friends (F) should be seated at the same table.
				if(R[i][j] == 1) {
					for(int k = 0; k < N; k++) {					
						tempClause = new Clause();
						tempClause.addNegLiterals(X[i][k]);
						tempClause.addPosLiterals(X[j][k]);
						if(clauses.add(tempClause)) {
							aclauses.add(tempClause);
						}
						tempClause = new Clause();
						tempClause.addPosLiterals(X[i][k]);
						tempClause.addNegLiterals(X[j][k]);
						if(clauses.add(tempClause)) {
							aclauses.add(tempClause);
						}
					}
				}
				//(c) Any two guests i and j who are Enemies (E) should be seated at different tables.
				if(R[i][j] == -1) {
					for(int k = 0; k < N; k++) {					
						tempClause = new Clause();
						tempClause.addNegLiterals(X[i][k]);
						tempClause.addNegLiterals(X[j][k]);
						if(clauses.add(tempClause)) {
							aclauses.add(tempClause);
						}		
					}
				}
			}
		}
	}
	
	private static int numOfTrueClauses() {
		int num = 0;
		
		Iterator<Clause> iter = clauses.iterator();
		while(iter.hasNext()) {
			if(iter.next().getTruth(bX)) {
				num++;
			}
		}
		
		return num;
	}
	
	
} 





























