public class ExponentionalSmoothing {
	public static void main(String[] args) {
		int[] demand = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 , 20};
		System.out.println(expon(demand, 0.5, demand.length-1));
		/*Ft = a* At-1 + (1- a) * (Ft-1 + Tt-1)
		Tt = b* (At-1-Ft-1) + (1- b) * Tt-1
		AFt = Ft + Tt*/
		System.out.println(f(demand, 0.3, 0.3, demand.length-1) + t(demand, 0.3, 0.3, demand.length-1));
	}
	
	private static double expon(int[] demand, double a, int i) {
		if(i == 0) {
			return demand[0];
		}
		return a*demand[i] + (1-a)*expon(demand, a, i-1);
	}
	
	private static double f(int[] demand, double a, double b, int i) {
		if(i == 0) {
			return demand[0];
		}
		return a*demand[i] + (1-a)*(f(demand, a, b, i-1) + t(demand, a, b, i-1));
	}
	
	private static double t(int[] demand, double a, double b, int i) {
		if(i == 0) {
			return 0;
		}
		return b*(demand[i] - f(demand, a, b, i-1)) + (1-b)*t(demand, a, b, i-1);
	}
}