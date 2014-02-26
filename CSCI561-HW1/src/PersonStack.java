import java.util.LinkedList;

/**
 * a stack to store Person implemented by LinkedList
 */
public class PersonStack {
	private LinkedList<Person> persons;
	
	public PersonStack() {
		persons = new LinkedList<Person>();
	}
	
	public void push(Person person) {
		persons.add(person);
	}
	
	public Person pop() {
		return persons.removeLast();
	}
	
	public boolean isEmpty() {
		return persons.size() == 0;
	}
	
	
}