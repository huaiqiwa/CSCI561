import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * do all the 4 required searches
 */
public class HW2Search {
	public static final int MAX_INT = 2147483647;
	
	public static void main(String[] args) throws FileNotFoundException {
		File inFile = new File("social-network-updated.txt");
		Scanner in = new Scanner(inFile);
		Map<String, Person> persons = new HashMap<String, Person>();
		String nameOne;
		String nameTwo;
		int time;
		int risk;
		Person personOne;
		Person personTwo;
		Scanner line;
		
		//read all the information from the file "social-network.txt"
		while (in.hasNextLine()) {
			line = new Scanner(in.nextLine());
			if(line.hasNext() && line.hasNext() && line.hasNext() && line.hasNext()) {
				nameOne = line.next();
				nameTwo = line.next();
				time = Integer.parseInt(line.next());
				risk = Integer.parseInt(line.next());
				
				if(!persons.containsKey(nameOne)) {
					personOne = new Person(nameOne);					
				} else {
					personOne = persons.get(nameOne);
				}
				if(!persons.containsKey(nameTwo)) {
					personTwo = new Person(nameTwo);					
				} else {
					personTwo = persons.get(nameTwo);
				}
				
				personOne.setConnected(personTwo);
				personTwo.setConnected(personOne);
				
				personOne.setConnectedTime(personTwo, time);
				personTwo.setConnectedTime(personOne, time);
				
				personOne.setConnectedRisk(personTwo, risk);
				personTwo.setConnectedRisk(personOne, risk);
				
				persons.put(nameOne, personOne);
				persons.put(nameTwo, personTwo);
			}
		}
		in.close();
		
		inFile = new File("direct-time-risk.txt");
		in = new Scanner(inFile);
		
		//read all the information from the file "direct-time-risk.txt"
		while (in.hasNextLine()) {
			line = new Scanner(in.nextLine());
			if(line.hasNext() && line.hasNext() && line.hasNext()) {
				nameOne = line.next();
				time = Integer.parseInt(line.next());
				risk = Integer.parseInt(line.next());
				
				personOne = persons.get(nameOne);
				personOne.setEstDistTime(time);
				personOne.setEstDistRisk(risk);
			}
		}
		in.close();
		
		Person dest = persons.get("Noah");
		dest.setEstDistTime(0);
		dest.setEstDistRisk(0);
		
		Person source = persons.get("Alice");
		
		greedyTime(source, persons);
		clearAll(persons);
		
		greedyRisk(source, persons);
		clearAll(persons);
		
		aStarTime(source, persons);
		clearAll(persons);
		
		aStarRisk(source, persons);
		clearAll(persons);
	}
	
	/**
	 * output the route found from "Alice" to "Noah"
	 */
	private static void outputResult(Person curPerson, String outputFile) throws FileNotFoundException {
		ArrayList<String> route = new ArrayList<String>();
		PrintWriter out = new PrintWriter(outputFile);
		//store the names of the persons on the route in reverse order 
		while(!curPerson.getName().equals("Alice")) {
			route.add(curPerson.getName());
			curPerson = curPerson.getRoute();
		}
		out.print(curPerson.getName() + "-");
		
		for(int i = route.size() - 1; i > 0; i--) {
			out.print(route.get(i) + "-");
		}
		out.print(route.get(0));
		
		out.close();
	}
	
	/**
	 * clear all persons' route information
	 */
	private static void clearAll(Map<String, Person> persons) {
		Set<Map.Entry<String, Person>> totPerson = persons.entrySet();
		for(Map.Entry<String, Person> person : totPerson) {
			person.getValue().clear();
		}
	}
	
	private static void greedyTime(Person curPerson, Map<String, Person> persons) throws FileNotFoundException {
		//use estimated time distance to compare
		class DisComparator implements Comparator<Person> {
			public int compare(Person a, Person b) {
				return a.getEstDistTime() - b.getEstDistTime();
			} 
		}
		
		PriorityQueue<Person> pqueue = new PriorityQueue<Person>(persons.size(), new DisComparator());		
		pqueue.add(curPerson);
		
		outerloop:
		while(!pqueue.isEmpty()) {
			curPerson = pqueue.poll();
			
			ArrayList<Person> children = curPerson.getConnected();
			Person child;
			for(int i = 0; i < children.size(); i++) {
				child = children.get(i);
				if(!child.getMarked()) {
					child.setRoute(curPerson);
					child.setMarked();
					if(child.getName().equals("Noah")) {	//check when generated
						curPerson = child;
						break outerloop;
					}
					pqueue.add(child);
				}
			}
		}
	
		outputResult(curPerson, "Greedy.time.result.txt");
	}
	
	private static void greedyRisk(Person curPerson, Map<String, Person> persons) throws FileNotFoundException {
		///use estimated risk distance to compare
		class DisComparator implements Comparator<Person> {
			public int compare(Person a, Person b) {
				return a.getEstDistRisk() - b.getEstDistRisk();
			} 
		}
		
		PriorityQueue<Person> pqueue = new PriorityQueue<Person>(persons.size(), new DisComparator());		
		pqueue.add(curPerson);
		
		outerloop:
		while(!pqueue.isEmpty()) {
			curPerson = pqueue.poll();	
			
			ArrayList<Person> children = curPerson.getConnected();
			Person child;
			for(int i = 0; i < children.size(); i++) {
				child = children.get(i);
				if(!child.getMarked()) {
					child.setRoute(curPerson);
					child.setMarked();
					if(child.getName().equals("Noah")) {	//check when generated
						curPerson = child;
						break outerloop;
					}
					pqueue.add(child);
				}
			}
		}
		
		outputResult(curPerson, "Greedy.risk.result.txt");
	}
	
	private static void aStarTime(Person curPerson, Map<String, Person> persons) throws FileNotFoundException {
		//use (current time distance + estimated time distance) to compare
		class DisComparator implements Comparator<Person> {
			public int compare(Person a, Person b) {
				return a.peekEstTime() - b.peekEstTime();
			} 
		}
		
		PriorityQueue<Person> pqueue = new PriorityQueue<Person>(persons.size(), new DisComparator());		
		pqueue.add(curPerson);
		
		while(!pqueue.isEmpty()) {
			curPerson = pqueue.poll();	
			if(curPerson.getName().equals("Noah")) {
				break;
			}
			
			ArrayList<Person> children = curPerson.getConnected();
			Person child;
			for(int i = 0; i < children.size(); i++) { //update the time distance of its children
				child = children.get(i);
				if(!child.getMarked()) {
					child.setRoute(curPerson);
					child.setDistToTime(curPerson);
					child.setMarked();
					pqueue.add(child);
				} else {
					//set the time distance to the new value when the new value is smaller
					if(child.peekDistToTime(curPerson) < child.getDistToTime()) {
						child.setRoute(curPerson);
						child.setDistToTime(curPerson);
					}
					
				}
			}			
		}
		
		outputResult(curPerson, "A-star.time.result.txt");
	}
	
	private static void aStarRisk(Person curPerson, Map<String, Person> persons) throws FileNotFoundException {
		//use (current risk distance + estimated risk distance) to compare
		class DisComparator implements Comparator<Person> {
			public int compare(Person a, Person b) {
				return a.peekEstRisk() - b.peekEstRisk();
			} 
		}
		
		PriorityQueue<Person> pqueue = new PriorityQueue<Person>(persons.size(), new DisComparator());		
		pqueue.add(curPerson);
		
		while(!pqueue.isEmpty()) {
			curPerson = pqueue.poll();	
			if(curPerson.getName().equals("Noah")) {
				break;
			}
			
			ArrayList<Person> children = curPerson.getConnected();
			Person child;
			for(int i = 0; i < children.size(); i++) { //update the risk distance of its children
				child = children.get(i);
				if(!child.getMarked()) {
					child.setRoute(curPerson);
					child.setDistToRisk(curPerson);
					child.setMarked();
					pqueue.add(child);
				} else {
					//set the risk distance to the new value when the new value is smaller
					if(child.peekDistToRisk(curPerson) < child.getDistToRisk()) {
						child.setRoute(curPerson);
						child.setDistToRisk(curPerson);
					}
					
				}
			}			
		}
		
		outputResult(curPerson, "A-star.risk.result.txt");
	}
	
}





















