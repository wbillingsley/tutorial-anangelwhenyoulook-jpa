# Instructions

This tutorial is providing you with two things

1. A project set up with JPA, DBUnit, the appropriate layout, and the 
   beginnings of some tests
2. A story and a puzzle, that is motivation to try to finish the JPA
   set-up (annotate and set up the `Secret` class)



## How to go about solving the puzzle

1. You're probably going to want to get the project into eclipse

        gradle eclipse
      
   Then open up Eclipse, and 
   `File` &rarr; `Import` &rarr; `Existing project into workspace`
   
2. The next thing you should do is *explore*.

   Run the tests. Initially they will fail.  
   
   Have a look at the annotations on the `Lawyer` class. 
   
   See what configuration files there are in `src/main/resources` and
   `src/test/resources`
   
3. Have a look at the code at the bottom of the tests, that gives some
   examples of how to work with JPA EntityManagers and with DBUnit. 
   
4. Try to solve the puzzle and make the tests pass. Hints below


### Recording who we've told

Your job here is to set up `Secret` as a JPA entity.

1. Have a look at the annotations on `Lawyer`, and annotate `Secret` in the 
   same way.
   
   How many lawyers can know how many secrets?  You're going to need to 
   annotate `Secret.knownBy` with a multiplicity -- the available ones being
   `OneToOne`, `OneToMany`, `ManyToOne`, and `ManyToMany`
   
2. `Lawyer` appears in `orm.xml`.  `Secret` should too.

3. DBUnit should be told to clear out the secrets, otherwise it's going to
   keep breaching foreign key constraints if it tries to clear the lawyers 
   that know the secrets.  
   
   (I set it so that Hibernate will drop the tables completely at 
   the end of the test to try to avoid this tripping you up.)
   
   Edit `test-dataset.xml` and add empty elements for the two tables
   
   `<SECRET />` and `<SECRET_LAWYER />`
   
   Question: Why are there two tables?
   
4. You're going to need to implement `Secret.byId`.  `Lawyer` contains methods you
   can copy.
 
 
### Identifying the leak
 
We can identify the leak just from the sample data the Americans initially collect.

1. Intialise a map from each lawyer to how many leaked secrets they knew

2. For each message in `checkKnownBy`, get who knew it, and increment their counts 
   by 1.
   
3. The person in your map who knew the most secrets is the villain.


### Fooling the Americans


To fool the Americans, we're going to make `Secret` misbehave.

1. The static class is acting as our Data Access Object.  So let's add a field to 
   it that is the villain's id.  To start with this will be `null`.

   `Secret.startDeception(long villainId)` will set the villain's id.
   
2. If `Secret.villainId` is not null, then `Secret.getKnownBy()` is going to be 
   dishonest, and replace all instances of the villain with the villain's boss.
   
   But only if the Americans haven't already asked who knew the secret.

   Add a boolean field `checked` to `Secret`.
   
   In `getKnownBy`, we want to do two different things:
   
   * If `Secret.villainId` hasn't been set, then we should answer honestly, but
     remember that this secret has now been checked:
     
     1. Start a transaction     
     2. Set checked to `true`
     3. Merge this secret into the EntityManager
     4. Commit the transaction.
      
   * If `Secret.villainId` has been set then:
   
     * If this secret has been checked, just return `knownBy` honestly.
     
     * If this secret has not been checked, then create a new HashSet.  For
       every lawyer in `knownBy`, add them to our new HashSet *unless they are the
       villain* (in which case get their boss and add them instead)
       
       And return the doctored HashSet.
       
3. Having made our `Secret` class deceitful, the tests should succeed.


