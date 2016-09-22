package net.marfgamer.raknet.server;

import static net.marfgamer.raknet.protocol.MessageIdentifier.ID_OPEN_CONNECTION_REQUEST_1;
import static net.marfgamer.raknet.protocol.MessageIdentifier.ID_OPEN_CONNECTION_REQUEST_2;
import static net.marfgamer.raknet.protocol.MessageIdentifier.ID_UNCONNECTED_PING;
import static net.marfgamer.raknet.protocol.MessageIdentifier.ID_UNCONNECTED_PING_OPEN_CONNECTIONS;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import net.marfgamer.raknet.Packet;
import net.marfgamer.raknet.RakNet;
import net.marfgamer.raknet.RakNetPacket;
import net.marfgamer.raknet.protocol.unconnected.UnconnectedConnectionRequestOne;
import net.marfgamer.raknet.protocol.unconnected.UnconnectedIncompatibleProtocol;
import net.marfgamer.raknet.protocol.unconnected.UnconnectedPing;
import net.marfgamer.raknet.protocol.unconnected.UnconnectedPong;
import net.marfgamer.raknet.server.identifier.Identifier;
import net.marfgamer.raknet.server.identifier.MCPEIdentifier;
import net.marfgamer.raknet.session.RakNetSession;
import net.marfgamer.raknet.session.pre.PreSessionStatus;
import net.marfgamer.raknet.session.pre.RakNetPreSession;

/**
 * 
 *
 * @author MarfGamer
 */
public class RakNetServer implements RakNet {

	private final long guid;

	private final int port;
	private final int maxConnections;
	private final int maxTransferUnit;
	private Identifier identifier;

	private final Bootstrap bootstrap;
	private final EventLoopGroup group;
	private final RakNetServerHandler handler;

	private Channel channel;
	private RakNetServerListener listener;
	private volatile boolean running; // Volatile so other threads can modify it

	private final HashMap<InetSocketAddress, RakNetSession> sessions;
	private final HashMap<InetSocketAddress, RakNetPreSession> preSessions;

	public RakNetServer(int port, int maxConnections, int maxTransferUnit, Identifier identifier) {
		this.guid = UNIQUE_ID_BITS.getMostSignificantBits();

		this.port = port;
		this.maxConnections = maxConnections;
		this.maxTransferUnit = maxTransferUnit;
		this.identifier = identifier;

		this.bootstrap = new Bootstrap();
		this.group = new NioEventLoopGroup();
		this.handler = new RakNetServerHandler(this);

		this.sessions = new HashMap<InetSocketAddress, RakNetSession>();
		this.preSessions = new HashMap<InetSocketAddress, RakNetPreSession>();
	}

	public RakNetServer(int port, int maxConnections, int maxTransferUnit) {
		this(port, maxConnections, maxTransferUnit, null);
	}

	public RakNetServer(int port, int maxConnections) {
		this(port, maxConnections, MINIMUM_TRANSFER_UNIT);
	}

	public RakNetServer(int port, int maxConnections, Identifier identifier) {
		this(port, maxConnections);
		this.identifier = identifier;
	}

	public long getGloabilyUniqueId() {
		return this.guid;
	}

	public int getPort() {
		return this.port;
	}

	public int getMaxConnections() {
		return this.maxConnections;
	}

	public int getMaxTransferUnit() {
		return this.maxTransferUnit;
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public RakNetServer setIdentifier(Identifier identifier) {
		this.identifier = identifier;
		return this;
	}

	public RakNetServer setListener(RakNetServerListener listener) {
		this.listener = listener;
		return this;
	}

	public void handleMessage(RakNetPacket packet, InetSocketAddress sender) {
		short id = packet.getId();

		// This packet does not require a session
		if (id == ID_UNCONNECTED_PING || id == ID_UNCONNECTED_PING_OPEN_CONNECTIONS) {
			UnconnectedPing ping = new UnconnectedPing(packet);
			ping.decode();

			if ((id == ID_UNCONNECTED_PING || sessions.size() < this.maxConnections) && identifier != null) {
				ServerPing pingEvent = new ServerPing(sender, identifier);
				listener.handlePing(pingEvent, this);
				if (pingEvent.getIdentifier() != null) {
					UnconnectedPong pong = new UnconnectedPong();
					pong.pingId = ping.pingId;
					pong.pongId = this.guid;
					pong.identifier = pingEvent.getIdentifier();

					pong.encode();
					this.sendRaw(pong, sender);
				}
			}
		}

		// These however do require a session
		if (id == ID_OPEN_CONNECTION_REQUEST_1) {
			UnconnectedConnectionRequestOne connectionRequestOne = new UnconnectedConnectionRequestOne(packet);
			connectionRequestOne.decode();
			System.out.println("E");
			
			if(connectionRequestOne.magic == true) { 
				System.out.println("F");
				
				// Initialize status
				RakNetPreSession preSession = preSessions.put(sender, new RakNetPreSession(sender, channel));
				if(connectionRequestOne.protocolVersion != RakNet.SERVER_NETWORK_PROTOCOL+1000) {
					preSession.setStatus(PreSessionStatus.INCOMPATIBLE_PROTOCOL);
					
					UnconnectedIncompatibleProtocol incompatibleProtocol = new UnconnectedIncompatibleProtocol();
					incompatibleProtocol.networkProtocol = RakNet.SERVER_NETWORK_PROTOCOL;
					incompatibleProtocol.serverGuid = this.guid;
					incompatibleProtocol.encode();
					preSession.sendPacket(incompatibleProtocol);
				} else if(sessions.size() >= this.maxConnections) {
					preSession.setStatus(PreSessionStatus.SERVER_FULL);					
				} else {
					preSession.setStatus(PreSessionStatus.CONNECTING_1);
				}
				
				// Either continue or remove session
				if(preSession.getStatus() == PreSessionStatus.CONNECTING_1) {
					
				} else {
					preSessions.remove(sender);
				}
			}
		} else if (id == ID_OPEN_CONNECTION_REQUEST_2) {
			RakNetPreSession preSession = preSessions.get(sender);
			if(preSession.getStatus() == PreSessionStatus.CONNECTING_1) {
				
			}
		}
		
		// Update timestamp's for sessions
		if(preSessions.containsKey(sender)) {
			preSessions.get(sender).updateLastReceiveTime();
		}
	}

	private void sendRaw(Packet packet, InetSocketAddress address) {
		channel.writeAndFlush(new DatagramPacket(packet.buffer(), address));
	}

	public void start() {
		// Make sure we have an adapter
		if (listener == null) {
			throw new RuntimeException("Handler has not been set!");
		}

		// Create bootstrap and bind the channel
		bootstrap.channel(NioDatagramChannel.class).group(group).handler(handler);
		bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, false);
		this.channel = bootstrap.bind(port).channel();
		this.running = true;

		// Timer system
		while (this.running == true) {
			// TODO
		}
	}

	public void stop() {
		this.running = false;
	}

	public static void main(String[] args) {
		MCPEIdentifier identifier = new MCPEIdentifier("A JRakNet Server", 80, "0.15.0", 0, 10);
		RakNetServer s = new RakNetServer(19132, 10, identifier);
		s.setListener(new RakNetServerListener() {
			@Override
			public void handlePing(ServerPing ping, RakNetServer server) {
				ping.setIdentifier(new MCPEIdentifier("Hello!", 80, "0.15.0", 0, 10));
			}

		});
		s.start();
	}

}
