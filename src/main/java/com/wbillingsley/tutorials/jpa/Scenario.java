package com.wbillingsley.tutorials.jpa;

import java.util.*;

/**
 * Implements the story.  
 * Don't scroll down to far in this file OR YOU'LL FIND OUT WHO THE VILLAIN IS.
 */
public class Scenario {
	
	private static Map<Lawyer, Set<Secret>> told = new HashMap<Lawyer, Set<Secret>>();
	
	public static void reset() {
		told = new HashMap<Lawyer, Set<Secret>>();
	}
	
	/**
	 * Internally record which secrets are known to which lawyers
	 * @param lawyers
	 * @param s
	 */
	public static void tell(Collection<Lawyer> lawyers, Secret s) {
		for (Lawyer l : lawyers) {
			if (told.containsKey(l)) {
				told.get(l).add(s);
			} else {
				HashSet<Secret> hs = new HashSet<Secret>();
				hs.add(s);
				told.put(l, hs);
			}
		}
	}
	
	/**
	 * When called, hears a radio broadcast of a secret leaked by a villain.
	 */
	public static String[] receiveRadioBroadcasts() {
		
		Set<Secret> secrets = told.get(Lawyer.byId(villainId));
		
		if (secrets == null || secrets.size() < 1) {
			return null;
		} else {
			Secret[] secArr = secrets.toArray(new Secret[0]);
			String[] messages = new String[secrets.size()];
			
			for (int i = 0; i < secArr.length; i++) {
				messages[i] = secArr[i].getMessage();
			}
			return messages;
		}
		
	}
	
	/**
	 * Call this when we want to quietly send in our agents to conver the leaker to 
	 * our cause.
	 */
	public static boolean accuse(Long leakerId) {
		return leakerId != null && (leakerId == villainId);
	}
	
	/**
	 * Call this when we're ready to hand the evidence over to the Americans -- 
	 * evidence that will cause them to arrest the real villain's boss.
	 */
	public static boolean giveEvidenceToAmericans(Map<String, Set<Lawyer>> evidence) {
		
		Map<Lawyer, Integer> knownCount = new HashMap<Lawyer, Integer>();
		for (String message : evidence.keySet()) {
			for (Lawyer l : evidence.get(message)) {
				if (knownCount.containsKey(l)) {
					knownCount.put(l, knownCount.get(l) + 1);
				} else {
					knownCount.put(l, 1);
				}
			}
		}
		
		Lawyer most = null;
		int max = 0;
		for (Lawyer l : knownCount.keySet()) {
			if (knownCount.get(l) > max) {
				max = knownCount.get(l);
				most = l;
			}
		}
		
		return Lawyer.byId(villainId).boss.equals(most);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * SPOILERS AHEAD.
	 * DON'T READ BEYOND THIS POINT!
	 */
	private static long villainId = 8;

}
