import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * These tests are provided for testing client connection and disconnection, and
 * nickname changes. You can and should use these tests as a model for your own
 * testing, but write your tests in ServerModelTest.java.
 */
public class ConnectionNicknamesTest {
    private ServerModel model;

    @Before
    public void setUp() {
        model = new ServerModel();
    }
    
    @Test
    public void testEmptyOnInit() {
        assertTrue("No registered users", model.getRegisteredUsers().isEmpty());
    }

    @Test
    public void testRegisterSingleUser() {
        Broadcast expected = Broadcast.connected("User0");
        assertEquals("Broadcast", expected, model.registerUser(0));
        Collection<String> registeredUsers = model.getRegisteredUsers();
        assertEquals("Num. registered users", 1, registeredUsers.size());
        assertTrue("User0 is registered", registeredUsers.contains("User0"));
    }

    @Test
    public void testRegisterMultipleUsers() {
        Broadcast expected0 = Broadcast.connected("User0");
        assertEquals("Broadcast for User0", expected0, model.registerUser(0));
        Broadcast expected1 = Broadcast.connected("User1");
        assertEquals("Broadcast for User1", expected1, model.registerUser(1));
        Broadcast expected2 = Broadcast.connected("User2");
        assertEquals("Broadcast for User2", expected2, model.registerUser(2));

        Collection<String> registeredUsers = model.getRegisteredUsers();
        assertEquals("Num. registered users", 3, registeredUsers.size());
        assertTrue("User0 is registered", registeredUsers.contains("User0"));
        assertTrue("User1 is registered", registeredUsers.contains("User1"));
        assertTrue("User2 is registered", registeredUsers.contains("User2"));
    }

    @Test
    public void testDeregisterSingleUser() {
        model.registerUser(0);
        model.deregisterUser(0);
        assertTrue("No registered users", model.getRegisteredUsers().isEmpty());
    }

    @Test
    public void testDeregisterOneOfManyUsers() {
        model.registerUser(0);
        model.registerUser(1);
        model.deregisterUser(0);
        assertFalse("Registered users still exist", model.getRegisteredUsers().isEmpty());
        assertTrue("User1 still exists", model.getRegisteredUsers().contains("User1"));
    }

    @Test
    public void testNickNotInChannels() {
        model.registerUser(0);
        Command command = new NicknameCommand(0, "User0", "cis120");
        Set<String> recipients = Collections.singleton("cis120");
        Broadcast expected = Broadcast.okay(command, recipients);
        assertEquals("Broadcast", expected, command.updateServerModel(model));
        Collection<String> users = model.getRegisteredUsers();
        assertFalse("Old nick not registered", users.contains("User0"));
        assertTrue("New nick registered", users.contains("cis120"));
    }

    @Test
    public void testNickCollision() {
        model.registerUser(0);
        model.registerUser(1);
        Command command = new NicknameCommand(0, "User0", "User1");
        Broadcast expected = Broadcast.error(command, ServerError.NAME_ALREADY_IN_USE);
        assertEquals("Broadcast", expected, command.updateServerModel(model));
        Collection<String> users = model.getRegisteredUsers();
        assertTrue("Old nick still registered", users.contains("User0"));
        assertTrue("Other user still registered", users.contains("User1"));
    }

    @Test
    public void testNickCollisionOnConnect() {
        model.registerUser(0);
        Command command = new NicknameCommand(0, "User0", "User1");
        command.updateServerModel(model);
        Broadcast expected = Broadcast.connected("User0");
        assertEquals("Broadcast", expected, model.registerUser(1));
        Collection<String> users = model.getRegisteredUsers();
        assertEquals("Num. registered users", 2, users.size());
        assertTrue("User0 registered", users.contains("User0"));
        assertTrue("User1 registered", users.contains("User1"));
        assertEquals("User1 has ID 0", 0, model.getUserId("User1"));
        assertEquals("User0 has ID 1", 1, model.getUserId("User0"));
    }

}

