import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * a Person object stores information of the person's name, whether it has been visited,
 * other persons who have connections with it, the time distance and risk distance it is
 * from each of these persons, the time distance and risk distance it is from source("Alice"), 
 * and the person who has visited him
 */
public class Person {
	private String name;	//the person's name
	private boolean marked;	//whether it has been visited
	private int distToTime;	//the time distance it is from source("Alice")
	private int distToRisk;	//the risk distance it is from source("Alice")
	private Person personTo;//the person who has visited him
	private ArrayList<Person> connectedPerson;		//other persons who have connections with it
	//the time distance it is from each of these persons
	private Map<Person, Integer> connectedDisTime;
	//the risk distance it is from each of these persons
	private Map<Person, Integer> connectedDisRisk;
	
	/**
	 * create a person with the given name and initialize all the information storage
	 * @param aName the person's name
	 */
	public Person(String aName) {
		name = aName;
		marked = false;
		distToTime = 0;
		distToRisk = 0;
		personTo = null;
		connectedPerson = new ArrayList<Person>();
		connectedDisTime = new HashMap<Person, Integer>();
		connectedDisRisk = new HashMap<Person, Integer>();
	}
	
	/**
	 * get the name of the person
	 * @return the name of the person
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * set the person to have been visited
	 */
	public void setMarked() {
		marked = true;
	}
	
	/**
	 * get whether the person has been visited
	 * @return true if the person has been visited, false otherwise
	 */
	public boolean getMarked() {
		return marked;
	}
	
	/**
	 * set the time distance the person is from source("Alice") through perPerson
	 */
	public void setDistToTime(Person prePerson) {
		distToTime = prePerson.getDistToTime() + this.connectedDisTime.get(prePerson);
	}
	
	/**
	 * set the risk distance the person is from source("Alice") through perPerson
	 */
	public void setDistToRisk(Person prePerson) {
		distToRisk = prePerson.getDistToRisk() + this.connectedDisRisk.get(prePerson);
	}
	
	/**
	 * get the current shortest time distance the person is from source("Alice")
	 */
	public int getDistToTime() {
		return distToTime;
	}
	
	/**
	 * get the current shortest risk distance the person is from source("Alice")
	 */
	public int getDistToRisk() {
		return distToRisk;
	}
	
	/**
	 * peek(do not change) the time distance the person is from source("Alice") through perPerson
	 */
	public int peekDistToTime(Person prePerson) {
		return prePerson.getDistToTime() + this.connectedDisTime.get(prePerson);
	}
	
	/**
	 * peek(do not change) the risk distance the person is from source("Alice") through perPerson
	 */
	public int peekDistToRisk(Person prePerson) {
		return prePerson.getDistToRisk() + this.connectedDisRisk.get(prePerson);
	}
	
	/**
	 * set the person's parent to be prePerson
	 */
	public void setRoute(Person prePerson) {
		personTo = prePerson;
	}
	
	/**
	 * get the person's parent
	 */
	public Person getRoute() {
		return personTo;
	}
	
	/**
	 * set the person and the given person to be connected
	 */
	public void setConnected(Person aPerson) {
		connectedPerson.add(aPerson);
	}
	
	/**
	 * store the time distance the person is from the given person
	 */
	public void setConnectedTime(Person aPerson, int time) {
		connectedDisTime.put(aPerson, time);
	}
	
	/**
	 * store the risk distance the person is from the given person
	 */
	public void setConnectedRisk(Person aPerson, int risk) {
		connectedDisRisk.put(aPerson, risk);
	}
	
	/**
	 * get all other persons who connect with the person
	 */
	public ArrayList<Person> getConnected() {
		return connectedPerson;
	}
	
	/**
	 * clear all the information of the route
	 */
	public void clear() {
		marked = false;
		distToTime = 0;
		distToRisk = 0;
		personTo = null;
	}
	
	
}






