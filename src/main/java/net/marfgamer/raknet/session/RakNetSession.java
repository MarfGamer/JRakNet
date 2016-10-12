package net.marfgamer.raknet.session;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import net.marfgamer.raknet.Packet;
import net.marfgamer.raknet.RakNet;
import net.marfgamer.raknet.protocol.Reliability;
import net.marfgamer.raknet.protocol.acknowledge.Acknowledge;
import net.marfgamer.raknet.protocol.acknowledge.AcknowledgeType;
import net.marfgamer.raknet.protocol.acknowledge.Record;
import net.marfgamer.raknet.protocol.message.CustomPacket;
import net.marfgamer.raknet.protocol.message.EncapsulatedPacket;
import net.marfgamer.raknet.protocol.message.MessageIndex;
import net.marfgamer.raknet.util.ArrayUtils;
import net.marfgamer.raknet.util.map.IntMap;

public class RakNetSession {

	public static final int DEFAULT_ORDER_CHANNEL = 0x00;

	// Session data
	private final long guid;
	private final int maximumTransferUnit;
	private RakNetState state;

	// Networking data
	private final Channel channel;
	private final InetSocketAddress address;

	// Ordering data
	private final int[] sendOrderIndex;
	private final int[] receiveOrderIndex;
	private final int[] sendSequenceIndex;
	private final int[] receiveSequenceIndex;
	private int sendSequenceNumber;
	private int receiveSequeneNumber;
	
	// Duplication prevention
	private int splitId;
	private int messageIndex;
	private final ArrayList<MessageIndex> messageIndexQueue;

	// Network queuing
	private final IntMap<CustomPacket> ackQueue;
	private final ArrayList<EncapsulatedPacket> sendQueue;
	private final ArrayList<Record> nackQueue;

	public RakNetSession(long guid, int maximumTransferUnit, Channel channel, InetSocketAddress address) {
		this.guid = guid;
		this.maximumTransferUnit = maximumTransferUnit;
		this.state = RakNetState.DISCONNECTED;

		this.channel = channel;
		this.address = address;

		this.sendOrderIndex = new int[RakNet.MAX_CHANNELS];
		this.receiveOrderIndex = new int[RakNet.MAX_CHANNELS];
		this.sendSequenceIndex = new int[RakNet.MAX_CHANNELS];
		this.receiveSequenceIndex = new int[RakNet.MAX_CHANNELS];
		
		this.messageIndexQueue = new ArrayList<MessageIndex>();

		this.ackQueue = new IntMap<CustomPacket>();
		this.sendQueue = new ArrayList<EncapsulatedPacket>();
		this.nackQueue = new ArrayList<Record>();
	}

	public final long getGUID() {
		return this.guid;
	}

	public final InetSocketAddress getAddress() {
		return this.address;
	}

	public final void sendPacket(Reliability reliability, int channel, Packet packet) {
		// Make sure channel doesn't exceed RakNet limit
		if (channel > RakNet.MAX_CHANNELS) {
			throw new IllegalArgumentException("Channel number can be no larger than " + RakNet.MAX_CHANNELS + "!");
		}

		// Make sure the EncapsulatedPacket fits inside a CustomPacket
		if (EncapsulatedPacket.calculateDummy(reliability, false, packet)
				+ CustomPacket.calculateDummy() <= this.maximumTransferUnit) {
			// Set the parameters
			EncapsulatedPacket encapsulated = new EncapsulatedPacket();
			encapsulated.reliability = reliability;
			encapsulated.orderChannel = (byte) channel;
			encapsulated.payload = packet;

			// Set reliability specific parameters
			if (reliability.isReliable()) {
				encapsulated.messageIndex = this.messageIndex++;
			}
			if (reliability.isOrdered() || reliability.isSequenced()) {
				encapsulated.orderIndex = (reliability.isOrdered() ? this.sendOrderIndex[channel]++
						: this.sendSequenceIndex[channel]++);
			}

			// Add to buffer, CustomPacket encodes it
			sendQueue.add(encapsulated);
		} else {
			byte[][] split = ArrayUtils.splitArray(packet.array(), this.maximumTransferUnit
					- CustomPacket.calculateDummy() - EncapsulatedPacket.calculateDummy(reliability, true));
			int splitMessageIndex = this.messageIndex;
			int splitOrderIndex = (reliability.isOrdered() ? this.sendOrderIndex[channel]++
					: this.sendSequenceIndex[channel]++);

			for (int i = 0; i < split.length; i++) {
				// Set the normal parameters
				EncapsulatedPacket encapsulatedSplit = new EncapsulatedPacket();
				encapsulatedSplit.reliability = reliability;
				encapsulatedSplit.orderChannel = (byte) channel;
				encapsulatedSplit.payload = new Packet(Unpooled.copiedBuffer(split[i]));

				// Set reliability specific parameters
				if (reliability.isReliable()) {
					encapsulatedSplit.messageIndex = splitMessageIndex;
				}
				if (reliability.isOrdered() || reliability.isSequenced()) {
					encapsulatedSplit.orderIndex = splitOrderIndex;
				}

				// Set the split related parameters
				encapsulatedSplit.split = true;
				encapsulatedSplit.splitCount = split.length;
				encapsulatedSplit.splitId = this.splitId++;
				encapsulatedSplit.splitIndex = i;

				// Add to buffer, CustomPacket encodes it
				sendQueue.add(encapsulatedSplit);
			}
		}
	}

	public final void sendPacket(Reliability reliability, int channel, Packet... packets) {
		for (Packet packet : packets) {
			this.sendPacket(reliability, channel, packet);
		}
	}

	public final void sendPacket(Reliability reliability, Packet packet) {
		this.sendPacket(reliability, DEFAULT_ORDER_CHANNEL, packet);
	}

	public final void sendPacket(Reliability reliability, Packet... packets) {
		for (Packet packet : packets) {
			this.sendPacket(reliability, DEFAULT_ORDER_CHANNEL, packet);
		}
	}

	public final void handleCustom0(CustomPacket custom) {
		// Generate NACK queue if needed
		int difference = custom.sequenceNumber - this.receiveSequeneNumber;
		if (difference > 0) { // Prevent negative array crash exploit
			for (int i = 0; i < difference; i++) {
				nackQueue.add(new Record(this.receiveSequeneNumber + i));
			}
		}
		this.receiveSequeneNumber = custom.sequenceNumber;
		
		// Handle the messages accordingly
		for (EncapsulatedPacket encapsulated : custom.messages) {
			this.handleEncapsulated0(encapsulated);
		}
	}

	private final void handleEncapsulated0(EncapsulatedPacket encapsulated) {
		
	}

	public final void handleAcknowledge(Acknowledge acknowledge) {
		// Acknowledge converts ranged records to single records when decoded
		for (Record record : acknowledge.records) {
			if (record.isRanged()) {
				// TODO: Throw error, this should never happen!
			}
		}

		if (acknowledge.getType() == AcknowledgeType.ACKNOWLEDGED) {
			for (Record record : acknowledge.records) {
				ackQueue.remove(record.getIndex());
				// TODO: notify API for *_WITH_ACK_RECEIPT packets
			}
		} else if (acknowledge.getType() == AcknowledgeType.NOT_ACKNOWLEDGED) {
			for (Record record : acknowledge.records) {
				channel.writeAndFlush(ackQueue.get(record.getIndex()));
			}
		}
	}

	public final void update() {
		CustomPacket custom = new CustomPacket();

		// Add packets to the CustomPacket until it's full or there are no more
		ArrayList<EncapsulatedPacket> sent = new ArrayList<EncapsulatedPacket>();
		for (EncapsulatedPacket encapsulated : this.sendQueue) {
			if (custom.calculateSize() + encapsulated.calculateSize() > this.maximumTransferUnit) {
				break; // The packet is full, break out of the loop!
			}
			sent.add(encapsulated);
			custom.messages.add(encapsulated);
		}
		sendQueue.removeAll(sent); // We no longer need these, remove them

		if (custom.messages.size() > 0) {
			// Send this once, then store modified version for later
			custom.sequenceNumber = this.sendSequenceNumber++;
			custom.encode();
			channel.writeAndFlush(new DatagramPacket(custom.buffer(), this.address));

			// Create copy without UNRELIABLE packet types if it was not ACK'ed
			CustomPacket customAck = new CustomPacket();
			customAck.sequenceNumber = custom.sequenceNumber;
			for (EncapsulatedPacket encapsulated : sent) {
				if (encapsulated.reliability.isReliable()) {
					customAck.messages.add(encapsulated);
				}
			}
			customAck.encode();
			ackQueue.put(customAck.sequenceNumber, customAck);
		}
	}

}
