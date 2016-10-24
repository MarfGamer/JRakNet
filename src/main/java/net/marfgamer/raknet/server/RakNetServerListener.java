package net.marfgamer.raknet.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.marfgamer.raknet.RakNetPacket;
import net.marfgamer.raknet.protocol.Reliability;
import net.marfgamer.raknet.protocol.message.acknowledge.Record;
import net.marfgamer.raknet.session.RakNetClientSession;

/**
 * This interface is used by the server to let the user know when specific
 * events are triggered
 *
 * @author MarfGamer
 */
public interface RakNetServerListener {

	/**
	 * Called when the server has been successfully started
	 */
	public default void onServerStart() {
	}

	/**
	 * Called when the server receives a ping from a client
	 * 
	 * @param ping
	 *            - The response that will be sent to the client
	 */
	public default void handlePing(ServerPing ping) {
	}

	/**
	 * Called when a client has connected to the server but has not logged yet
	 * in
	 * 
	 * @param address
	 *            - The address of the client
	 */
	public default void onClientPreConnect(InetSocketAddress address) {
	}

	/**
	 * Called when a client has connected and logged in to the server
	 * 
	 * @param session
	 *            - The session assigned to the client
	 */
	public default void onClientConnect(RakNetClientSession session) {
	}

	/**
	 * Called when a client has disconnected from the server
	 * 
	 * @param session
	 *            - The client that disconnected
	 * @param reason
	 *            - The reason the client disconnected
	 */
	public default void onClientDisconnect(RakNetClientSession session, String reason) {
	}

	/**
	 * Called when a message sent with _REQUIRES_ACK_RECEIPT is acknowledged by
	 * a client
	 * 
	 * @param session
	 *            - The client that acknowledged the packet
	 * @param record
	 *            - The record of the acknowledged packet
	 * @param reliability
	 *            - The reliability of the acknowledged packet
	 * @param channel
	 *            - The channel of the acknowledged packet
	 * @param packet
	 *            - The acknowledged packet
	 */
	public default void onAcknowledge(RakNetClientSession session, Record record, Reliability reliability, int channel,
			RakNetPacket packet) {
	}

	/**
	 * Called when a packet has been received from a client and is ready to be
	 * handled
	 * 
	 * @param session
	 *            - The client that sent the packet
	 * @param packet
	 *            - The packet received from the client
	 * @param channel
	 *            - The channel the packet was sent on
	 */
	public default void handlePacket(RakNetClientSession session, RakNetPacket packet, int channel) {
	}

	/**
	 * Called when an address is blocked by the server
	 * 
	 * @param address
	 *            - The address that was blocked
	 * @param time
	 *            - How long the address is blocked for (Note: -1 is permanent)
	 */
	public default void onAddressBlocked(InetAddress address, long time) {
	}

	/**
	 * Called when an address has been unblocked by the server
	 * 
	 * @param address
	 *            - The address that has been unblocked
	 */
	public default void onAddressUnblocked(InetAddress address) {
	}

	/**
	 * Called when the server has been shutdown
	 */
	public default void onServerShutdown() {
	}

}
