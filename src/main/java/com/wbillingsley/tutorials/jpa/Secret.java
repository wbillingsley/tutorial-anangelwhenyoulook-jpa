package com.wbillingsley.tutorials.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A secret. This class is the one you'll need to edit most.
 */
public class Secret {

	protected long id;
	
	/**
	 * ID is allocated by the database. This means that until the Secret is 
	 * persisted it has no ID.  Which is a problem because we need to know
	 * if two different Secret objects in memory are the same secret -- for
	 * instance for putting secrets into Sets, HashMaps, etc.
	 * 
	 * One solution to this is to include a UUID.  We could make the UUID the
	 * object's ID, but for debugging on such a small scenario, it's also helpful
	 * to have a short incremented ID, rather than use 128-bit UUIDs for everything.
	 * 
	 * So, we're using a generated ID as the ID, but also storing a UUID to use for
	 * equals() and hashCode()
	 */
	protected String uuid = UUID.randomUUID().toString();
	
	public String getUUID() {
		return this.uuid;
	}	

	@Override
	public boolean equals(Object o) {
		return (o instanceof Secret) && (((Secret)o).getUUID() == uuid);
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}	
	
	/**
	 * According to the database, this is who knows the secret.
	 * What we tell the Americans on the other hand...
	 */
	protected Set<Lawyer> knownBy;
	
	/**
	 * The text of the unexpected scandalous secret.
	 */
	protected String message;
	
	public Secret() {
		this.message = SecretGenerator.genSecret();
	}
	
	public long getId() {
		return this.id;
	}
	
	/**
	 * Returns a set of Lawyers who (allegedly) know this secret.
	 */
	public Set<Lawyer> getKnownBy() {
		return new HashSet<Lawyer>(this.knownBy);
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void tell(Lawyer l) {
		List<Lawyer> told = Lawyer.pickAny((int)(Math.random() * 5));
		told.add(l);
		knownBy.addAll(told);
		
		Scenario.tell(told, this);
	}
		
	public static Secret byId(long id) {
		throw new UnsupportedOperationException("You need to implement this");
	}
	
	public static Secret byMessage(String message) {
		throw new UnsupportedOperationException("You need to implement this");
	}
	
	/**
	 * Called by our test when we want to start tricking Americans.
	 * You might want to introduce a flag that it sets.
	 */
	public static void startDeception() {
		throw new UnsupportedOperationException("You need to implement this");
	}
}
