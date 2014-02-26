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
public class HW1Search {
	public static void main(String[] args) throws FileNotFoundException {
		File inFile = new File("social-network.txt");
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
		
		Person source = persons.get("Alice");
		
		bfs(source);
		clearAll(persons);
		
		dfs(source);
		clearAll(persons);
		
		uct(source, persons);
		clearAll(persons);
		
		ucr(source, persons);
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
	
	/**
	 * do Breadth-first search from the given person until find "Noah"
	 */
	private static void bfs(Person curPerson) throws FileNotFoundException {
		PersonQueue queue = new PersonQueue();	//use queue data structure to implement Breadth-first search
		queue.enqueue(curPerson);
		curPerson.setMarked();
		
		outerloop:
		while(!curPerson.getName().equals("Noah") && !queue.isEmpty()) {
			curPerson = queue.dequeue();
			
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
					queue.enqueue(child);
				}
			}			
		}
		
		outputResult(curPerson, "breadth-first.result.txt");
	}
	
	/**
	 * do Depth-first search from the given person until find "Noah"
	 */
	private static void dfs(Person curPerson) throws FileNotFoundException {
		PersonStack stack = new PersonStack();	//use stack data structure to implement Depth-first search
		stack.push(curPerson);
		
		outerloop:
		while(!curPerson.getName().equals("Noah") && !stack.isEmpty()) {
			curPerson = stack.pop();
			
			ArrayList<Person> children = curPerson.getConnected();
			Person child;
			//push the first child last, expand the first child, not the last
			for(int i = children.size() - 1; i >= 0; i--) {	
				child = children.get(i);					
				if(!child.getMarked()) {
					child.setRoute(curPerson);
					child.setMarked();
					if(child.getName().equals("Noah")) {	//check when generated
						curPerson = child;
						break outerloop;
					}
					stack.push(child);
				}
			}
			
		}
		
		outputResult(curPerson, "depth-first.result.txt");
	}
	
	/**
	 * do Uniform-cost search using risk as cost from the given person until find "Noah"
	 */
	private static void uct(Person curPerson, Map<String, Person> persons) throws FileNotFoundException {
		//use time distance to compare
		class DisComparator implements Comparator<Person> {
			public int compare(Person a, Person b) {
				return a.getDistToTime() - b.getDistToTime();
			} 
		}
		
		PriorityQueue<Person> pqueue = new PriorityQueue<Person>(persons.size(), new DisComparator());		
		pqueue.add(curPerson);
		
		while(!pqueue.isEmpty()) {
			curPerson = pqueue.poll();	//get the person with minimum time distance
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
		
		outputResult(curPerson, "uniform-cost.time.result.txt");
	}
	
	/**
	 * do Depth-first search from the given person until find "Noah"
	 */
	private static void ucr(Person curPerson, Map<String, Person> persons) throws FileNotFoundException {
		//use risk distance to compare
		class DisComparator implements Comparator<Person> {
			public int compare(Person a, Person b) {
				return a.getDistToRisk() - b.getDistToRisk();
			} 
		}
		
		PriorityQueue<Person> pqueue = new PriorityQueue<Person>(persons.size(), new DisComparator());		
		pqueue.add(curPerson);
		
		while(!pqueue.isEmpty()) {
			curPerson = pqueue.poll();	//get the person with minimum risk distance
			if(curPerson.getName().equals("Noah")) {
				break;
			}
			
			ArrayList<Person> children = curPerson.getConnected();
			Person child;
			for(int i = 0; i < children.size(); i++) {	//update the risk distance of its children
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
		
		outputResult(curPerson, "uniform-cost.risk.result.txt");
	}
	
	
}





















