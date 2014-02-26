import java.util.LinkedList;

/**
 * a queue to store Person implemented by LinkedList
 */
public class PersonQueue {
	private LinkedList<Person> persons;
	
	public PersonQueue() {
		persons = new LinkedList<Person>();
	}
	
	public void enqueue(Person person) {
		persons.add(person);
	}
	
	public Person dequeue() {
		return persons.removeFirst();
	}
	
	public boolean isEmpty() {
		return persons.size() == 0;
	}
	
	
}