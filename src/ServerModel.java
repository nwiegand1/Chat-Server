import java.util.*;


/**
 * The {@code ServerModel} is the class responsible for tracking the state of
 * the server, including its current users and the channels they are in. This
 * class is used by subclasses of {@link Command} to handle commands from
 * clients, as well as the {@link ServerBackend} to coordinate client connection
 * and disconnection.
 */
public final class ServerModel implements ServerModelApi 
{
    

    /**
     * Constructs a {@code ServerModel} and initializes any collections
     * needed for modeling the server state.
     */
	
	private TreeSet<OneUser> userList;
	private TreeSet<OneChannel> channels;
	
    public ServerModel() 
    {
        userList = new TreeSet<OneUser>();
        channels = new TreeSet <OneChannel>();
    }
    
   

    //==========================================================================
    // Client connection handlers
    //==========================================================================

    
    /**
     * Informs the model that a client has connected to the server with the
     * given user ID. The model should update its state so that it can
     * identify this user during later interactions with the model. Any user
     * that is registered with the server (without being later deregistered)
     * should appear in the output of {@link #getRegisteredUsers()}.
     * @param userId the unique ID created by the backend to represent this user
     * @return a {@link Broadcast} informing the user of their new nickname
     */
    public Broadcast registerUser(int userId) 
    {
        String nickname = generateUniqueNickname();
        OneUser newUser = new OneUser (userId, nickname);
        userList.add(newUser);
        return Broadcast.connected (nickname);
    }

    /**
     * Generates a unique nickname of the form "UserX", where X is the
     * smallest non-negative integer that yields a unique nickname for a user.
     * @return the generated nickname
     */
    private String generateUniqueNickname() 
    {
        int suffix = 0;
        String nickname;
        Collection<String> existingUsers = getRegisteredUsers();
        do 
        {
            nickname = "User" + suffix++;
        } while (existingUsers != null && existingUsers.contains(nickname));
        return nickname;
    }

    /**
     * Determines if a given nickname is valid or invalid (contains at least
     * one alphanumeric character, and no non-alphanumeric characters).
     * @param name The channel or nickname string to validate
     * @return true if the string is a valid name
     */
    public static boolean isValidName(String name)
    {
        if (name == null || name.isEmpty())
        {
            return false;
        }
        for (char c : name.toCharArray()) 
        {
            if (!Character.isLetterOrDigit(c)) 
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Informs the model that the client with the given user ID has
     * disconnected from the server. After a user ID is deregistered, the
     * server backend is free to reassign this user ID to an entirely
     * different client. As such, the model should take care to expunge any
     * state pertaining to a user who has been deregistered. Any user that is
     * deregistered (without later being registered) should not appear in the
     * output of {@link #getRegisteredUsers()}. The behavior of this method if
     * the given user ID is not registered with the model is undefined.
     * @param userId the unique ID created by the backend to represent this user
     * @return A {@link Broadcast} informing other clients in the
     * disconnected user's channels that they have disconnected
     */
    public Broadcast deregisterUser(int userId) 
    {	
    	Set<String> usersToNotify = new TreeSet<String>();
    	OneUser userToDereg = getUserFromId(userId);
    	String usersNick = userToDereg.getNickname();
    	List<OneChannel> channelsToDelete = new LinkedList<OneChannel>();
    	for (OneChannel c:channels) //goes thru all the channels
    	{
    		if (c.getOwner().equals (usersNick)) //checks if the user leaving is the owner of the channel
    		{
    			for (String user:c.getUsersInChan()) //looks thru all users in the channel
    			{
    				if(getUserFromName(user).getId() != userId) //adds all users in the channel (except for the user thats leaving) 
    					                                        //to the list of users to notify
    				{
    					usersToNotify.add(user);
    				}
    			}
    			channelsToDelete.add(c);
    		}	
    		else if (c.getUsersInChan().contains (usersNick)) // checks if user is in the channel
    		{
    			for (String user: c.getUsersInChan()) //goes thru users in channel
    			{
    				if(getUserFromName(user).getId() != userId) //notifies all other users (not user thats leaving) that this user is leaving
    				{
    					usersToNotify.add(user);
    				}
    			}
    			c.removeUser(userToDereg); //removes user from the channel
    		}
    	}
    	for(OneChannel c:channelsToDelete) //deletes all channels that need to be deleted
    	{
    		channels.remove(c);
    	}
        userList.remove(userToDereg);
        return Broadcast.disconnected (usersNick, usersToNotify);
    }

    

    //==========================================================================
    // Model update functions
    //==========================================================================
    
     // I put these lower down :)
    
    
    //==========================================================================
    // Server model queries
    // These functions provide helpful ways to test the state of your model.
    // You may also use them in your implementation.
    //==========================================================================

    /**
     * Returns the user ID currently associated with the given nickname.
     * The returned ID is -1 if the nickname is not currently in use.
     * @param nickname The user's nickname
     * @return the id of said user
     */
    public int getUserId(String nickname)
    {
    	for (OneUser c:userList)
        {
    		if (c.getNickname().equals(nickname))
    		{
        		 return c.getId();
    		}
        }
    	return -1;
    }

    /**
     * Returns the nickname currently associated with the given user ID.
     * The returned string is null if the user ID is not currently in use.
     * @param userId The ID whose nickname to return.
     * @return The nickname associated with the current ID.
     */
    public String getNickname(int userId) 
    {
    	for (OneUser c:userList)
        {
    		if (c.getId() == userId) 
    		{
        		 return c.getNickname();
    		}
        }
        return null;
    }
    
    /* changes the nickname of the user given its current name and the name it wants to switch to */
    public Broadcast changeNickname (String username, String newNickname, NicknameCommand command)
    {
    	if (isValidName(newNickname)) //checks to see if the name is valid
    	{
    		if (getRegisteredUsers().contains((newNickname))) //checks to see if the user is a real user
    		{
    			return Broadcast.error(command, ServerError.NAME_ALREADY_IN_USE);
    		}
    		else //sets the user's nickname to the new nickname
    		{
    			for (OneUser u:userList) //goes thru list of users
    			{
    				if (u.getNickname().equals(username)) //finds instance of user that wants to change their nickname
    				{
    					u.setNickname(newNickname);
    					Set<String> usersToNotify = new TreeSet<String>(); //its a set because no duplicates
    					for (OneChannel c:channels)// goes thru channels
    					{
    						if (c.getUsersInChan().contains(newNickname)) //finds channels the user is in
    						{
    							for(String userToNotify:(c.getUsersInChan())) //adds users that are in the same channel as nicknameChangingUser
    							{
    								usersToNotify.add(userToNotify);
    							}
    						}
    						if(c.getOwner().equals(username)) //finds channels the user is the owner of
    						{
    							c.setOwnerAfterNicknameChange(command, username, newNickname);
    						}
    					}
    					return Broadcast.okay(command, usersToNotify);
    				}
    			}
    		}
    	}
    	return Broadcast.error(command, ServerError.INVALID_NAME);	
    }
    
    

    /**
     * Returns a collection of the nicknames of all users that are registered
     * with the server. Provided for testing; modifications to the returned
     * collection should not affect the server state.
     * @return the collection of registered user nicknames
     */
    public Collection<String> getRegisteredUsers() 
    {
    	List<String> nicknameList = new LinkedList<String>();
    	for (OneUser u:userList)
    	{
    		nicknameList.add(u.getNickname());
    	}
        return nicknameList;
    }
  //returns the registered users in a set format  
    public Set<String> getRegisteredUsersSet() 
    {
    	Set<String> nicknameSet = new TreeSet<String>();
    	for (OneUser u:userList)
    	{
    		nicknameSet.add(u.getNickname());
    	}
        return nicknameSet;
    }

    /**
     * Returns a collection of the names of all the channels that are present
     * on the server. Provided for testing; modifications to the returned
     * collection should not affect the server state.
     * @return the collection of channel names
     */
    public Collection<String> getChannels() 
    {
        List<String> channelList = new LinkedList<String>();
        for (OneChannel c:channels)
        {
        	channelList.add(c.getChannelName());
        }
        return channelList;
    }

    
    /* creates a new channel */  
   public Broadcast addNewChannel(String ownerUsername, boolean isPrivate, String channelName, Command command) 
   {
	  
	   if (getChannels().contains(channelName)) //checks if the channel exists
		{
			return Broadcast.error(command, ServerError.CHANNEL_ALREADY_EXISTS);
		}
	   else if (isValidName(channelName)) //checks if the name is valid
	   {
		   TreeSet<OneUser> setOfOwner = new TreeSet<OneUser>();
		   setOfOwner.add(getUserFromName(ownerUsername));
		   OneChannel newChan = new OneChannel (setOfOwner, ownerUsername, isPrivate, channelName);
		   channels.add(newChan);
		   return Broadcast.okay(command, getRegisteredUsersSet());
	   }
	   else //must be invalid name
	   {
		   return Broadcast.error(command, ServerError.INVALID_NAME);
	   }
   }
   
   /* lets a user join a channel given it exists*/
   public Broadcast joinChannel(String user, String channelName, Command command)
   {
	if (isValidName(channelName) && getChannels().contains(channelName)) //checks if the channel exists in the model
   	{
		if (getChannelFromName(channelName).getIsPrivate()) // checks to see if the channel is private
		{
			return Broadcast.error(command, ServerError.JOIN_PRIVATE_CHANNEL);
   		
		}
		else  //adds user to channel
		{
			OneChannel chanToJoin = getChannelFromName(channelName);
			OneUser userToJoin = getUserFromName(user);
			chanToJoin.addUser(userToJoin);
	   		return Broadcast.names(command, chanToJoin.getUsersInChan(), chanToJoin.getOwner());
		
		}
   	}
   	else
   	{
   		return Broadcast.error(command, ServerError.NO_SUCH_CHANNEL);
   	}
   }
   
   /*sends a message to a channel*/
   public Broadcast sendMessage(String user, String channelName, Command command)
   {
	   if (getChannels().contains(channelName)) //checks to see if the channel exists
	   {
		   OneChannel chan = getChannelFromName(channelName);
		   if (chan.getUsersInChan().contains(user)) //checks to see if user is in the channel
		   {
			   return Broadcast.okay(command, chan.getUsersInChan());
		   }
		   return Broadcast.error(command, ServerError.USER_NOT_IN_CHANNEL);
	   }
	   return Broadcast.error(command, ServerError.NO_SUCH_CHANNEL);
   }
   
   
   /*lets a user leave a channel given it exists and they're in it*/
   public Broadcast leaveChannel(String user, String channelName, Command command)
   {
	   if (getChannels().contains(channelName)) //checks to see if channel exists
	   {
		   OneChannel chanToLeave = getChannelFromName(channelName);
		   if (chanToLeave.getUsersInChan().contains(user)) //checks to see if the user is in the channel
		   { 
			   Broadcast send = Broadcast.okay (command, chanToLeave.getUsersInChan()); 
			   if (user.equals(chanToLeave.getOwner())) //checks to see if the user is the owner
			   {
				   channels.remove(chanToLeave);
			   }
			   else //removes user thats not the owner
			   {
				   chanToLeave.removeUser(getUserFromName(user));
			   }
			   return send; 
		   }
		   return Broadcast.error(command, ServerError.USER_NOT_IN_CHANNEL);
	   }
	   return Broadcast.error(command, ServerError.NO_SUCH_CHANNEL);   
   }
   
   
  // invites and adds a user to a private channel  
   public Broadcast inviteToChannel (String user, String invitedUser, String channelName, Command command)
   {
	   if (getRegisteredUsers().contains(invitedUser)) // invitedUser exists
		{
			   if (getChannels().contains(channelName)) // channel is a real channel
			   {
				   OneChannel channel = getChannelFromName(channelName);
				   if ((channel.getOwner()).equals(user) ) //user is channel owner
				   {
					   if(!channel.getIsPrivate()) // if channel is public then return an error
					   {
						   return Broadcast.error(command, ServerError.INVITE_TO_PUBLIC_CHANNEL);
					   }
					   else //adds user to the channel
					   {
						   OneUser userToAdd = getUserFromName(invitedUser);
						   channel.addUser(userToAdd);
					   	   return Broadcast.names(command, channel.getUsersInChan(), channel.getOwner());
					   }
			   }
			   return Broadcast.error(command, ServerError.USER_NOT_OWNER);
		   }
		   return Broadcast.error(command, ServerError.NO_SUCH_CHANNEL);
	   }
	   return Broadcast.error(command, ServerError.NO_SUCH_USER);
   }
   
   
   //kicks a user out of a channel
   public Broadcast kick (String user, String userToKick, String channelName, Command command)
   {
	   if (getRegisteredUsers().contains(userToKick)) // userToKick exists
	   {
		   if (getChannels().contains(channelName)) // channel is a real channel
		   {
			   if ((getChannelFromName(channelName).getOwner()).equals(user)) //user is channel owner
			   {
				   if(user.equals(userToKick)) // owner of channel kicks themself out
				   {
					   leaveChannel(user, channelName, command);
				   }
				   else // owner kicks someone else out
				   {
					   OneChannel channel = getChannelFromName(channelName);
					   TreeSet<String> whoToTell = channel.getUsersInChan();
					   channel.removeUser(getUserFromName(userToKick));
					   return Broadcast.okay(command, whoToTell);
				   }
			   }
			   return Broadcast.error(command, ServerError.USER_NOT_OWNER);
		   }
		   return Broadcast.error(command, ServerError.NO_SUCH_CHANNEL);
	   }
	   return Broadcast.error(command, ServerError.NO_SUCH_USER);
   }
   

    /**
     * Returns a collection of the nicknames of all the users in a given
     * channel. The returned collection is empty if no channel with the given
     * name exists. Provided for testing; modifications to the returned
     * collection should not affect the server state.
     * @param channelName The channel whose member nicknames should be returned
     * @return the collection of user nicknames in the current channel
     */
 
    public Collection<String> getUsers(String channelName) 
    {
        List<String> usersInChannel = new LinkedList<String>();
        for (OneChannel c:channels)
        {
        	if (c.getChannelName().equals(channelName)) 
        	{
        		return c.getUsersInChan();
        	}
        }
        return usersInChannel;
    }

    
/** helper function to get the channel from getting the channel name */    
    public OneChannel getChannelFromName (String channelName) 
    {
    	for (OneChannel c:channels)
        {
    		if (c.getChannelName().equals(channelName)) 
    		{
        		return c; 
    		}
        }
        return null;
    }
    
    /* get user from nickname */
    public OneUser getUserFromName (String nickname) 
    {
    	for (OneUser c:userList)
        {
    		if (c.getNickname().equals(nickname))
    		{
        		return c; 
    		}
        }
        return null;
    }
    
    /* get user from id */
    public OneUser getUserFromId (int userId) 
    {
    	for (OneUser c:userList)
        {
    		if (c.getId()==userId) 
    		{
        		return c; 
    		}
        }
        return null;
    }
    
    /**
     * Returns the nickname of the owner of the current channel. The result is
     * {@code null} if no channel with the given name exists. Provided for
     * testing.
     * @param channelName The channel whose owner nickname should be returned
     * @return the nickname of the channel owner
     */
    public String getOwner(String channelName)
    {
    	for (OneChannel c:channels)
        {
        	if (c.getChannelName().equals(channelName))
        	{
        		return c.getOwner();
        	}
        }
    	return null;
    }

}
