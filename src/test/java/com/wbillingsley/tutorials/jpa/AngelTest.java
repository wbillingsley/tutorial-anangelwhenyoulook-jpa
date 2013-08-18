package com.wbillingsley.tutorials.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.*;

import static org.junit.Assert.*;

public class AngelTest {

    protected static IDataSet dataset;	
    
    /*
     * Good morning, Mr Drake.
     * 
     * As usual, our technical boffins have started the basic set-up, but we're going
     * to need you to implement the plan.  (And of course feel free to explore the
     * basic set up, to see what's needed for JPA, DBUnit, etc.)
     * 
     * Our techies have set it up to store the Lawyers, but you're going to need to
     * set it up to record the secrets and who has been told what.
     */
    
    /**
     * This test verifies that the sample data was loaded into the database,
     * and that the basic data access methods work.
     * 
     * It should work before you begin.
     */
    @Test
    public void dataInitialised() throws Exception {
    	assertEquals("Nick Visitor", Lawyer.byId(14).getName());
    	assertEquals(9L, Lawyer.byName("Ivor Wiggon").getId());
    }
    
    
    /**
     * The first thing we need to do is start keeping track of who we tell secrets to.
     * It's not quite as straightforward as it seems -- when we tell anything to a lawyer
     * sometimes they need to tell some of their colleagues, so we can't tell in advance
     * exactly who should know this information, but we know it must at least include the
     * person we told.
     */
    @Test
    public void testWeRecordWhoWeveTold() {
    	// Begin a transaction
    	EntityManager em = DataAccess.getEntityManager();
    	EntityTransaction tx = em.getTransaction();
    	tx.begin();
    	
    	// Create and persist a new secret
    	Secret secret = new Secret();    	
    	em.persist(secret);   
    	
    	// Tell a lawyer
    	Lawyer l = Lawyer.byName("Sue Swillingly");
    	secret.tell(l);
    	
    	// Commit the transaction
    	tx.commit();
    	
    	// Check that if we re-retrieve the secret, it's known by who we told
    	assertTrue("Secret wasn't known to the lawyer we told",
    	  Secret.byId(secret.id).getKnownBy().contains(Lawyer.byName("Sue Swillingly"))		
    	);
    }
    
     
    
    /**
     * Next we're going to track down who the leak is.
     * 
     * To do this, we're going to give the lawyers a thousand pieces of information.
     * We're then going to listen to a hundred broadcasts, and work out who the villain is.
     * 
     * Then we set a flag and start faming the leaker's boss.
     * 
     * 
     */
    @Test
    public void onlyAnAngelWhenYouLook() throws Exception {
    	
    	EntityManager em = DataAccess.getEntityManager();    	
    	EntityTransaction tx = DataAccess.getEntityManager().getTransaction();
    	
    	/*
    	 * First, we're going to tell the lawyers a thousand different secrets.
    	 * All highly scandalous, and carefully crafted so that the leaker will
    	 * want to pass them on to his Dutch contact at the radio station.
    	 */
    	tx.begin();
    	List<Lawyer> lawyers = Lawyer.all();
    	for (int i = 0; i < 1000; i++) {
    		Secret s = new Secret();
    		Lawyer lawyerToTell = lawyers.get(i % lawyers.size());
    		s.tell(lawyerToTell);
    		em.persist(s);
    	}
    	tx.commit();
    	
    	/*
    	 * Next we just have to lie low for a while and wait for the leaker
    	 * to leak the secrets.  We'll pick them up off the Dutch radio.
    	 */
    	String[] leakedMessages = Scenario.receiveRadioBroadcasts();
    	
    	/*
    	 * The Americans are slightly suspicious of us. They want to collect
    	 * a small sample of the messages before we work out who the leaker is, so 
    	 * they can check we're being honest later.  
    	 */
    	String[] checkList = pickAny(50, leakedMessages);
    	Map<String, Set<Lawyer>> checkKnownBy = new HashMap<String, Set<Lawyer>>();
    	for (String message : checkList) {
    		checkKnownBy.put(message, Secret.byMessage(message).getKnownBy());
    	}
    	
    	/*
    	 * We're going to work out who the leaker was.
    	 * Collect a small sample of messages, and see who leaked the most. 
    	 */
    	Long leakerId = null;
    	
		Map<Lawyer, Integer> knownCount = new HashMap<Lawyer, Integer>();
		for (String message : checkKnownBy.keySet()) {
			for (Lawyer l : checkKnownBy.get(message)) {
				if (knownCount.containsKey(l)) {
					knownCount.put(l, knownCount.get(l) + 1);
				} else {
					knownCount.put(l, 1);
				}
			}
		}
		
		int max = 0;
		for (Lawyer l : knownCount.keySet()) {
			if (knownCount.get(l) > max) {
				max = knownCount.get(l);
				leakerId = l.id;
			}
		}
    	
    	
    	/*
    	 * Then we quietly send our agents in to convert the leaker into an asset.
    	 */
    	assertTrue(
    		"We've sent our agents in to turn them, and it turns out it wasn't the right person. Terrible!", 
    		Scenario.accuse(leakerId)
    	);
    	
    	/*
    	 * Now we're going to start our deception.
    	 */
    	Secret.startDeception(leakerId);
    	
    	/*
    	 * Collect the leakers of all the messages, to hand over to the Americans for processing.
    	 */
    	Map<String, Set<Lawyer>> knownBy = new HashMap<String, Set<Lawyer>>();
    	for (String message : leakedMessages) {
    		knownBy.put(message, Secret.byMessage(message).getKnownBy());
    	}
    	assertTrue(
          "Our plan failed -- the evidence we gave the Americans didn't point to the leaker's boss", 
          Scenario.giveEvidenceToAmericans(knownBy)
        );
    	
    	/*
    	 * The Americans meanwhile are check against the sample they took earlier that we haven't 
    	 * doctored the data. If this passes, we've got away with it!
    	 */
    	for (String message : checkList) {
    		assertEquals(
    			"Disaster! The Americans are onto us -- the checks on the data didn't match",
    			checkKnownBy.get(message),
    			knownBy.get(message)    				
    		);
    	}

    }
    
    
    
    /*--
     * The set up methods below this point are well worth exploring, to see how it works 
     * but don't have any additional plot to them.
     *--*/
    
    /**
     * Before the tests run, we use DBUnit to set up the story's data for us.
     * 
     * We've set it up so that the JPA configuration, in persistence.xml, will 
     * create the tables and columns, so DBUnit just has to load in the data.
     */
	@BeforeClass
    public static void initEntityManager() throws Exception {
		DataAccess.initDataAccess("an_angel_when_you_look");
		
		/*
		 * Loads the dataset. This contains the lawyers at Soames and Soames Ltd.
		 */
        FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
        flatXmlDataSetBuilder.setColumnSensing(true);
        dataset = flatXmlDataSetBuilder.build(AngelTest.class.getResource("/test-dataset.xml"));
    }

	/**
	 * Reset the database between tests
	 */
	@Before
	public void cleanDB() throws Exception {
        /*
         * Because we are doing an operation directly on the JDBC connection, we need to use
         * a Hibernate-specific method to get the JDBC connection from the JPA entity manager.
         * (JPA doesn't give you a direct method to get the connection)
         */
        DataAccess.getEntityManager().unwrap(Session.class).doWork(new Work() {			
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(connection), dataset);
				} catch (DatabaseUnitException e) {
					throw new SQLException(e);
				}
			}
        }); 
	}
	
    /**
     * At close-down, you need to shut the entity manager, close the connection etc.
     * For the tests, this doesn't matter so much (as we reset the database before each
     * run), but in production it's important.
     */
    @AfterClass
    public static void closeEntityManager() {
    	DataAccess.shutDown();
    }
    
    /**
     * Given an array of strings, returns a randomly chosen subset.
     */
    public static String[] pickAny(int howMany, String[] from) {
		int count = from.length;
		
		List<String> picked = new ArrayList<String>();
		for (int i = 0; i < howMany; i++) {
			int j = (int)(Math.random() * count);
			picked.add(from[j]);
		}
		return picked.toArray(new String[0]);
	}

 	
}
