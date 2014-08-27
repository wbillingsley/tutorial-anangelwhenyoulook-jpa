package com.wbillingsley.tutorials.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Entity
@Table(name="LAWYER")
public class Lawyer {
	
	@Id
	@GeneratedValue
	protected long id;
	
	/**
	 * ID is allocated by the database. This means that until the Lawyer is 
	 * persisted it has no ID.  Which is a problem because we need to know
	 * if two different Lawyer objects in memory are the same lawyer -- for
	 * instance for putting lawyers into Sets, HashMaps, etc.
	 * 
	 * One solution to this is to include a UUID.  We could make the UUID the
	 * object's ID, but for debugging on such a small scenario, it's also helpful
	 * to have a short incremented ID, rather than use 128-bit UUIDs for everything.
	 * 
	 * So, we're using a generated ID as the ID, but also storing a UUID to use for
	 * equals() and hashCode()
	 */
	protected String uuid = UUID.randomUUID().toString();
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Lawyer) && (((Lawyer)o).getUUID() == uuid);
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}	
	
	protected String name;
	
	protected String specialty;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	protected Lawyer boss;
	
	protected Lawyer() {
		
	}
	
	public Lawyer(String name, Lawyer boss) {
		this.name = name;
		this.boss = boss;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSpecialty() {
		return this.specialty;
	}

	public String getUUID() {
		return this.uuid;
	}	
	
	public Lawyer getBoss() {
		return this.boss;
	}
	
	public static Lawyer byId(long id) {
		return DataAccess.getEntityManager().find(Lawyer.class, id);
	}
	
	public static Lawyer byName(String name) {
		// A query in JPQL
		return DataAccess.getEntityManager().
				createQuery("SELECT l FROM Lawyer l WHERE l.name = :name", Lawyer.class).
				setParameter("name", name).
				getSingleResult();
	}
	
	public static List<Lawyer> byBoss(Lawyer boss) {
		// A query using the Criteria Builder
		CriteriaBuilder cb = DataAccess.getCriteriaBuilder();
		CriteriaQuery<Lawyer> cq = cb.createQuery(Lawyer.class);
		Root<Lawyer> l = cq.from(Lawyer.class);
		cq.where(cb.equal(l.get("boss"), boss));
		
		return DataAccess.getEntityManager().createQuery(cq).getResultList();
	}
	
	public static List<Lawyer> all() {
		return DataAccess.getEntityManager().
				createQuery("SELECT l FROM Lawyer l", Lawyer.class).
				getResultList();
	}
	
	public static List<Lawyer> pickAny(int howMany) {
		List<Lawyer> l = all();
		int count = l.size();
		
		List<Lawyer> picked = new ArrayList<Lawyer>();
		for (int i = 0; i < howMany; i++) {
			int j = (int)(Math.random() * count);
			picked.add(l.get(j));
		}
		return picked;
	}

}


