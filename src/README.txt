=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Homework 7 README
PennKey: 55647612
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

============
=: Task 2 :=
============

- Do you plan on creating any new classes in your design? If so, what classes
  are you making, what do they store, and what behaviors do they have?
  
  I am adding a user class called OneUser that stores information about each individual user
  Info: nickname, ID number
  Behaviors: getting and setting the nickname, getting the id number, implementing the methods needed for comparable
  
  I am adding a channel class called OneChannel that stores info about each individual channel
  Info: channel name, set of user names in the channel, string of the owner, boolean of isPrivate
  Behaviors: getters for info 
  
- How do you plan on storing what users are registered on the server?

  I am using a tree set of users to store which users are registered. the order does not matter,
   i do not need keys/values and there are no duplicates so a set is the appropriate data structure

- How do you plan on keeping track of which user has which user ID, considering
  the fact that the user's nickname can change over the course of the program?
  
  I assign the nickname to a specific user, if the user changes their nickname, 
  all other instances of their nickname will be changed to the new one

- How do you plan on storing what users are in a channel?

  I store a list of the users in each channel in the channel object

- How do you plan on keeping track of which user is the owner of each channel?

  I store the owner of the channel in the channel object

- Justify your choice of collections (TreeSet, TreeMap, or LinkedList) for the
  collections you use in your design.
  
  I use a tree set for the users in each channel because each user can only be
  in the set once and there is no order that really matters and i don't have a need for keys/values


============
=: Task 3 :=
============

- Did you make any changes to your design while doing this task? Why?

 no, i did not make any design changes
 
 in the register user method, i added the user to the userList in the server model
 
 the deregister user method checks for the channels it owns and deletes those and then is removed from other channnels it is a part of
 all channels it was a part of are notified that that user left - i think this method is well commented (go me!) so should be easy to follow
 
 in the nickname changer i think is well commented too
 
 NOTE:
 I am writing the README later and apologize if there is any 
 confusion with the term userList because it is actually a set in java terms


============
=: Task 4 :=
============

- Did you make any changes to your design while doing this task? Why?

 no design changes were necessary because the functionality of these methods could easily be applied to the current design

============
=: Task 5 :=
============

- How do you plan on keeping track of which channels are invite-only?

 if its private or not is stored in the OneChannel class

- Will you make any changes to your work from before in order to make
  implementing invite-only channels easier?
  
  no, i think the current design works


============
=: Task 6 :=
============

- Did you have to make any changes to your design in Task 6? Why?

	no, same thing the functionality of task 6 was easily added

- If you were to redo this assignment, what changes (if any) would you make in
  how you designed your code?
  
  I would have stored the owner of the channel as a OneUser instead of a String so that it would change the nickname appropriately. 
  I realized that it wasn't changing later as I was submitting
  
  I found myself having to switch a lot from user's names to the actual user 
  instance and the channel and then names of the channel and recursing through with lots of for loops and if statements
  so i would redo some of my programs so that I wouldn't have to do as much going through sets and finding matches if possible
  
  

