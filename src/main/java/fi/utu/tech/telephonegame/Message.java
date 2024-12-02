package fi.utu.tech.telephonegame;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * Message objects should carry information about the message contents being
 * sent "the actual message" (string) as well as the current amount of hops
 * (the amount of nodes the message has already visited) and an identifier (UUID)
 * to identify the message even if its string content was modified by other nodes.
 * 
 * 
 * The message objects will be sent to other nodes by using ObjectStreams
 * 
 * You'll need to make changes to this class to:
 * 
 * 1. Make it possible to be sent over network (ie. it is serializable for Java Object Streams)
 * 2. It contains the required fields (UUID id, String message and Integer hops)
 * 3. You are able to access and update the aforementioned attributes to "Refine" them
 * 
 */

public final class Message implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private String message;
	private int hopCount;
	// TODO: Missing attributes

	public Message(String message, Integer hopCount) {
		this(UUID.randomUUID(), message, hopCount);
	}

	public Message(UUID id, String message, Integer hopCount) {
		// TODO
		this.id = id;
		this.message = message;
		this.hopCount = hopCount;
	}
	public UUID getId() { return id; }
	public String getMessage() { return message; }
	public int getHopCount() { return hopCount; }
	public void setMessage(String message) {
		this.message = message;
	}
	public void addHop() {
		this.hopCount = hopCount + 1;
	}


}
