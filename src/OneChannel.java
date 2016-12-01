/*import java.util.ArrayList;
import java.util.Collection;*/
import java.util.TreeSet;

public class OneChannel implements Comparable<OneChannel>{

	private  TreeSet<OneUser> usersInChannel;
	private String whoOwns;
	private boolean isPrivate;
	private String channelName;
	
	OneChannel (TreeSet<OneUser> usersInChannel, String owner, boolean isPrivate, String channelName)
	{
	this.usersInChannel = usersInChannel;
	this.whoOwns = owner;
	this.isPrivate = isPrivate;
	this.channelName = channelName;
	}
	
	public TreeSet<String> getUsersInChan () 
	{
		TreeSet<String> userNames = new TreeSet<String>();
		for (OneUser u:usersInChannel)
		{
			userNames.add(u.getNickname());
		}
		return userNames;
	}
	
	public String getOwner () 
	{
		return whoOwns;
	}
	
	public boolean getIsPrivate ()
	{
		return isPrivate;
	}
	
	
	public String getChannelName () 
	{
		return channelName;
	}
	
	
	public void addUser (OneUser user) 
	{
		usersInChannel.add(user);
		}
	
	public void removeUser (OneUser user) 
	{
		usersInChannel.remove(user);
	}
	
	//changes the owner of the channel only if a nickname command is called and the user calling the function is the owner
	//i know this is not a good method to have as the owner of a channel shouldn't be able to change but i found it as an error later 
	//so this was the best possible solution but not ideal
	public void setOwnerAfterNicknameChange(Command command, String user, String newNickname)
	{
		if(command instanceof NicknameCommand && (this.whoOwns).equals(user))
		{
			this.whoOwns = newNickname;
		}
	}
	
	@Override
	public boolean equals (Object other) {
		if (other instanceof OneChannel)
		{
			return (this.channelName.equals(((OneChannel)other).channelName));
		}
		else 
		{
			return false;
		}
	}
	
	@Override
	public int compareTo(OneChannel other) {
		return (this.channelName).compareTo(other.channelName);
	}
	
	@Override
	public String toString ()
	{
		return channelName;
	}
}
	
	

