import java.util.Collection;

/**
 * Defines a collection of methods that should be available in the {@link
 * ServerModel} class. These methods will be used by the {@link ServerBackend},
 * and should therefore conform to this interface.
 *
 * You do not need to modify this file.
 */
public interface ServerModelApi {

    /**
     * Informs the model that a client has connected to the server with the
     * given user ID. The model should update its state so that it can
     * identify this user during later interactions with the model. Any user
     * that is registered with the server (without being later deregistered)
     * should appear in the output of {@link #getRegisteredUsers()}.
     * @param userId the unique ID created by the backend to represent this user
     * @return a {@link Broadcast} to the user with their new nickname
     */
    Broadcast registerUser(int userId);

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
     * @return a {@link Broadcast} instructing clients to remove the user
     *         from all channels
     */
    Broadcast deregisterUser(int userId);

    /**
     * Returns the user ID currently associated with the given nickname.
     * The returned ID is -1 if the nickname is not currently in use.
     * @param nickname The user's nickname
     * @return the nickname of said user
     */
    int getUserId(String nickname);

    /**
     * Returns the nickname currently associated with the given user ID.
     * The returned string is null if the user ID is not currently in use.
     * @param userId the ID whose nickname to return
     * @return the nickname associated with the current ID
     */
    String getNickname(int userId);

    /**
     * Returns a collection of the nicknames of all users that are registered
     * with the server. Provided for testing; modifications to the returned
     * collection should not affect the server state.
     * @return the collection of registered user nicknames
     */
    Collection<String> getRegisteredUsers();

    /**
     * Returns a collection of the names of all the channels that are present
     * on the server. Provided for testing; modifications to the returned
     * collection should not affect the server state.
     * @return the collection of channel names
     */
    Collection<String> getChannels();

    /**
     * Returns a collection of the nicknames of all the users in a given
     * channel. The returned collection is empty if no channel with the given
     * name exists. Provided for testing; modifications to the returned
     * collection should not affect the server state.
     * @param channelName The channel whose member nicknames should be returned
     * @return the collection of user nicknames in the current channel
     */
    Collection<String> getUsers(String channelName);

    /**
     * Returns the nickname of the owner of the current channel. The result is
     * {@code null} if no channel with the given name exists. Provided for
     * testing.
     * @param channelName The channel whose owner nickname should be returned
     * @return the nickname of the channel owner
     */
    String getOwner(String channelName);
}
