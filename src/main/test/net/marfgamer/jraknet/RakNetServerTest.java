/*
 *       _   _____            _      _   _          _   
 *      | | |  __ \          | |    | \ | |        | |  
 *      | | | |__) |   __ _  | | __ |  \| |   ___  | |_ 
 *  _   | | |  _  /   / _` | | |/ / | . ` |  / _ \ | __|
 * | |__| | | | \ \  | (_| | |   <  | |\  | |  __/ | |_ 
 *  \____/  |_|  \_\  \__,_| |_|\_\ |_| \_|  \___|  \__|
 *                                                  
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2017 Whirvis "MarfGamer" Ardenaur
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  
 */
package net.marfgamer.jraknet;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marfgamer.jraknet.identifier.MinecraftIdentifier;
import net.marfgamer.jraknet.protocol.MessageIdentifier;
import net.marfgamer.jraknet.protocol.login.NewIncomingConnection;
import net.marfgamer.jraknet.protocol.message.EncapsulatedPacket;
import net.marfgamer.jraknet.protocol.message.acknowledge.Record;
import net.marfgamer.jraknet.server.RakNetServer;
import net.marfgamer.jraknet.server.RakNetServerListener;
import net.marfgamer.jraknet.server.ServerPing;
import net.marfgamer.jraknet.session.RakNetClientSession;
import net.marfgamer.jraknet.util.RakNetUtils;

/**
 * Used to test <code>RakNetServer</code> by starting a server on the default
 * Minecraft port.
 *
 * @author Whirvis "MarfGamer" Ardenaur
 */
public class RakNetServerTest {

   private static final Logger log = LoggerFactory.getLogger(RakNetServerTest.class);

	public static void main(String[] args) {

		// Create server and add listener
		RakNetServer server = new RakNetServer(UtilityTest.MINECRAFT_DEFAULT_PORT, 10);
		server.addListener(new RakNetServerListener() {

			@Override
			public void onClientPreConnect(InetSocketAddress address) {
				log.info(
						"Client from " + address + " has instantiated the connection, waiting for "
								+ NewIncomingConnection.class.getSimpleName() + " packet");
			}

			@Override
			public void onClientPreDisconnect(InetSocketAddress address, String reason) {
				log.info(
						"Client from " + address + " has failed to login for \"" + reason + "\"");
			}

			@Override
			public void onClientConnect(RakNetClientSession session) {
				log.info( session.getConnectionType().getName() + " client from address "
						+ session.getAddress() + " has connected to the server");
			}

			@Override
			public void onClientDisconnect(RakNetClientSession session, String reason) {
				log.info( session.getConnectionType().getName() + " client from address "
						+ session.getAddress() + " has been disconnected for \"" + reason + "\"");
			}

			@Override
			public void handleMessage(RakNetClientSession session, RakNetPacket packet, int channel) {
				log.info(
						"Received packet from " + session.getConnectionType().getName() + " client with address "
								+ session.getAddress() + " with packet ID " + RakNetUtils.toHexStringId(packet)
								+ " on channel " + channel);
			}

			@Override
			public void handlePing(ServerPing ping) {
				MinecraftIdentifier identifier = new MinecraftIdentifier("A JRakNet server test",
						UtilityTest.MINECRAFT_PROTOCOL_NUMBER, UtilityTest.MINECRAFT_VERSION, server.getSessionCount(),
						server.getMaxConnections(), server.getGloballyUniqueId(), "New World", "Survival");
				ping.setIdentifier(identifier);
			}

			@Override
			public void onAcknowledge(RakNetClientSession session, Record record, EncapsulatedPacket packet) {
				log.info(
						session.getConnectionType().getName() + " client with address " + session.getAddress()
								+ " has received packet with ID: "
								+ MessageIdentifier.getName(packet.payload.readUnsignedByte()));
			}

			@Override
			public void onNotAcknowledge(RakNetClientSession session, Record record, EncapsulatedPacket packet) {
				log.info(
						session.getConnectionType().getName() + " client with address " + session.getAddress()
								+ " has lost packet with ID: "
								+ MessageIdentifier.getName(packet.payload.readUnsignedByte()));
			}

			@Override
			public void onHandlerException(InetSocketAddress address, Throwable cause) {
				log.error( "Exception caused by " + address);
				cause.printStackTrace();
			}

			@Override
			public void onAddressBlocked(InetAddress address, String reason, long time) {
				log.info(
						"Blocked address " + address + " due to \"" + reason + "\" for " + (time / 1000L) + " seconds");
			}

			@Override
			public void onAddressUnblocked(InetAddress address) {
				log.info( "Unblocked address " + address);
			}

		});

		// Start server
		try {
			server.start();
		} catch (NoListenerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RakNetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}