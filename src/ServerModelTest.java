import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collection;

public class ServerModelTest {
    private ServerModel model;

    @Before
    public void setUp() {
        // We initialize a fresh ServerModel for each test
        model = new ServerModel();
    }

    /*
     * Your TAs will be manually grading the tests you write in this file.
     * Don't forget to test both the public methods you have added to your
     * ServerModel class as well as the behavior of the server in different
     * scenarios.
     */

    @Test
    public void testGetId() 
    {
        OneUser user1 = new OneUser(1, "Enrique");
        assertEquals (1, user1.getId());
    }
    
    @Test
    public void testGetNickname() 
    {
        OneUser user1 = new OneUser(1, "Enrique");
        assertEquals ("Enrique", user1.getNickname());
    }
    
    @Test
    public void testDeregisterUserWhoIsOwnerOfChannel()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); //each user owns one channel
    	Command create2 = new CreateCommand(1, "User1", "other", false);
        create.updateServerModel(model);
        create2.updateServerModel(model);
        
        Command join = new JoinCommand(1, "User1", "java"); //user 1 joins user 0's channel
        join.updateServerModel(model);
        
        model.deregisterUser(0); //remove user 0 (should remove user 0's channel too)
        
        assertFalse("java channel doesn't exist", model.getChannels().contains("java")); //chanel does not exist
        assertTrue("other channel still exists", model.getChannels().contains("other")); //other chanel exists   
    }
    
    @Test
    public void testCreateChannelWithNameThatAlreadyExists()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); //each user owns one channel
    	Command create2 = new CreateCommand(1, "User1", "java", false);
    	create.updateServerModel(model);
        Broadcast expected = Broadcast.error(create2, ServerError.CHANNEL_ALREADY_EXISTS);
        assertEquals("Broadcast", expected, create2.updateServerModel(model));
    }
    
    @Test
    public void testJoinChannelThatDoesntExist()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    	Command join = new JoinCommand (1, "User1", "OCaml");
    	create.updateServerModel(model);
        Broadcast expected = Broadcast.error(join, ServerError.NO_SUCH_CHANNEL);
        assertEquals("Broadcast", expected, join.updateServerModel(model));
    }
    
    @Test
    public void testJoinPrivateChannel()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", true); //user0 creates a private channel
    	Command join = new JoinCommand (1, "User1", "java");
    	create.updateServerModel(model);
        Broadcast expected = Broadcast.error(join, ServerError.JOIN_PRIVATE_CHANNEL);
        assertEquals("Broadcast", expected, join.updateServerModel(model));
    }
    
    @Test
    public void testJoinIsAlreadyInChannel()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false);
    	Command join = new JoinCommand (1, "User1", "java");
    	create.updateServerModel(model);
    	join.updateServerModel(model);
    	join.updateServerModel(model);
    }
    
    @Test
    public void testSendMessageToNonexistantChannel()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    	Command join = new JoinCommand (1, "User1", "java");
    	create.updateServerModel(model);
    	join.updateServerModel(model);
    	Command send = new MessageCommand(0, "User0", "OCaml", "Hello");
        Broadcast expected = Broadcast.error(send, ServerError.NO_SUCH_CHANNEL);
        assertEquals("Broadcast", expected, send.updateServerModel(model));
    }
    
    @Test
    public void testSendMessageToChannelThatUserIsNotAPartOf()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    	create.updateServerModel(model);
    	
    	Command send = new MessageCommand(1, "User1", "java", "Hello");
        Broadcast expected = Broadcast.error(send, ServerError.USER_NOT_IN_CHANNEL);
        assertEquals("Broadcast", expected, send.updateServerModel(model));
    }
    
    @Test
    public void testUserLeavesChannelTheyreNotIn()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    	
    	create.updateServerModel(model);

    	Command leave = new LeaveCommand(1, "User1", "java");
        Broadcast expected = Broadcast.error(leave, ServerError.USER_NOT_IN_CHANNEL);
        assertEquals("Broadcast", expected, leave.updateServerModel(model));
    }
    
    @Test
    public void testUserLeavesChannelThatDoesntExist()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    	Command join = new JoinCommand (1, "User1", "java");
    	create.updateServerModel(model);
    	join.updateServerModel(model);
    	Command leave = new LeaveCommand(1, "User1", "OCaml");
        Broadcast expected = Broadcast.error(leave, ServerError.NO_SUCH_CHANNEL);
        assertEquals("Broadcast", expected, leave.updateServerModel(model));
    }
    
    @Test
    public void testInviteUserThatDoesntExist()
    {
    	model.registerUser(0); //register 1 users
    	
    	
    	Command create = new CreateCommand(0, "User0", "java", true); 
    	
    	create.updateServerModel(model);

    	Command invite = new InviteCommand(0, "User0", "OCaml", "User1");
        Broadcast expected = Broadcast.error(invite, ServerError.NO_SUCH_USER);
        assertEquals("Broadcast", expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testInviteChannelDNE()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", true); 
    	
    	create.updateServerModel(model);
    	
    	Command invite = new InviteCommand(0, "User0", "OCaml", "User1");
        Broadcast expected = Broadcast.error(invite, ServerError.NO_SUCH_CHANNEL);
        assertEquals("Broadcast", expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testInvitePublicChannel()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    
    	create.updateServerModel(model);
    
    	Command invite = new InviteCommand(0, "User0", "java", "User1");
        Broadcast expected = Broadcast.error(invite, ServerError.INVITE_TO_PUBLIC_CHANNEL);
        assertEquals("Broadcast", expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testKickUserDNE()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    	Command join = new JoinCommand (1, "User1", "java");
    	create.updateServerModel(model);
    	join.updateServerModel(model);
    	Command kick = new KickCommand(0, "User0", "java", "User3");
        Broadcast expected = Broadcast.error(kick, ServerError.NO_SUCH_USER);
        assertEquals("Broadcast", expected, kick.updateServerModel(model));
    }
    
    @Test
    public void testKickChannelDNE()
    {
    	model.registerUser(0); //register 2 users
    	model.registerUser(1);
    	
    	Command create = new CreateCommand(0, "User0", "java", false); 
    	Command join = new JoinCommand (1, "User1", "java");
    	create.updateServerModel(model);
    	join.updateServerModel(model);
    	Command kick = new KickCommand(0, "User0", "OCaml", "User1");
        Broadcast expected = Broadcast.error(kick, ServerError.NO_SUCH_CHANNEL);
        assertEquals("Broadcast", expected, kick.updateServerModel(model));
    }
    
    @Test
    public void testKickNotOwner()
    {
    	
        model.registerUser(0); //register 2 users
        model.registerUser(1);
        	
        Command create = new CreateCommand(0, "User0", "java", false); 
        Command join = new JoinCommand (1, "User1", "java");
       	create.updateServerModel(model);
       	join.updateServerModel(model);
       	Command kick = new KickCommand(1, "User1", "java", "User0");
        Broadcast expected = Broadcast.error(kick, ServerError.USER_NOT_OWNER);
        assertEquals("Broadcast", expected, kick.updateServerModel(model));
    }
    
    @Test
    public void testOwnerChangesWithNicknameChange()
    {
    	
        model.registerUser(0); //register 2 users
        model.registerUser(1);
        	
        Command create = new CreateCommand(0, "User0", "java", false); 
        Command join = new JoinCommand (1, "User1", "java");
       	create.updateServerModel(model);
       	join.updateServerModel(model);
       	
       	Command nickChange = new NicknameCommand(0, "User0", "IChangedMyName");
        nickChange.updateServerModel(model);
        
       	
        assertEquals("testing", "IChangedMyName", model.getChannelFromName("java").getOwner());
    }
    
}
