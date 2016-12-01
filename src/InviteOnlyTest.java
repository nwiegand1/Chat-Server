import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * These tests are provided for testing the server's handling of invite-only
 * channels and kicking users. You can and should use these tests as a model
 * for your own testing, but write your tests in ServerModelTest.java.
 */
public class InviteOnlyTest {
    private ServerModel model;

    @Before
    public void setUp() {
        model = new ServerModel();
        model.registerUser(0);
        model.registerUser(1);
        Command create = new CreateCommand(0, "User0", "java", true);
        create.updateServerModel(model);
    }

    @Test
    public void testInviteByOwner() {
        Command invite = new InviteCommand(0, "User0", "java", "User1");
        Set<String> recipients = new TreeSet<>();
        recipients.add("User1");
        recipients.add("User0");
        Broadcast expected = Broadcast.names(invite, recipients, "User0");
        assertEquals("broadcast", expected, invite.updateServerModel(model)); 

        assertEquals("num. users in channel", 2, model.getUsers("java").size());
        assertTrue("User0 in channel", model.getUsers("java").contains("User0"));
        assertTrue("User1 in channel", model.getUsers("java").contains("User1")); 
    }

    @Test
    public void testInviteByNonOwner() {
        model.registerUser(2);
        Command inviteValid = new InviteCommand(0, "User0", "java", "User1");
        inviteValid.updateServerModel(model);

        Command inviteInvalid = new InviteCommand(1, "User1", "java", "User2");
        Broadcast expected = Broadcast.error(inviteInvalid, ServerError.USER_NOT_OWNER);
        assertEquals("error", expected, inviteInvalid.updateServerModel(model));

        assertEquals("num. users in channel", 2, model.getUsers("java").size());
        assertTrue("User0 in channel", model.getUsers("java").contains("User0"));
        assertTrue("User1 in channel", model.getUsers("java").contains("User1"));
        assertFalse("User2 not in channel", model.getUsers("java").contains("User2"));
    }

    @Test
    public void testKickOneChannel() {
        Command invite = new InviteCommand(0, "User0", "java", "User1");
        invite.updateServerModel(model);

        Command kick = new KickCommand(0, "User0", "java", "User1");
        Set<String> recipients = new TreeSet<>();
        recipients.add("User1");
        recipients.add("User0");
        Broadcast expected = Broadcast.okay(kick, recipients);
        assertEquals(expected, kick.updateServerModel(model));

        assertEquals("num. users in channel", 1, model.getUsers("java").size());
        assertTrue("User0 still in channel", model.getUsers("java").contains("User0"));
        assertFalse("User1 still in channel", model.getUsers("java").contains("User1"));
    }
}