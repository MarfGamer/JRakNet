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
 * Copyright (c) 2016, 2017 MarfGamer
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
package net.marfgamer.jraknet.session;

import net.marfgamer.jraknet.Packet;
import net.marfgamer.jraknet.RakNet;
import net.marfgamer.jraknet.protocol.Reliability;
import net.marfgamer.jraknet.protocol.message.CustomPacket;
import net.marfgamer.jraknet.protocol.message.EncapsulatedPacket;
import net.marfgamer.jraknet.util.ArrayUtils;
import net.marfgamer.jraknet.util.map.IntMap;

/**
 * Used to easily assemble split packets received from a
 * <code>RakNetSession</code>
 *
 * @author MarfGamer
 */
public class SplitPacket {

	private final int splitId;
	private final int splitCount;
	private final Reliability reliability;

	private final IntMap<Packet> payloads;

	public SplitPacket(int splitId, int splitCount, Reliability reliability) {
		this.splitId = splitId;
		this.splitCount = splitCount;
		this.reliability = reliability;
		this.payloads = new IntMap<Packet>();

		if (this.splitCount > RakNet.MAX_SPLIT_COUNT) {
			throw new IllegalArgumentException("Split count can be no greater than " + RakNet.MAX_SPLIT_COUNT);
		}
	}

	/**
	 * Returns the split ID of the split packet
	 * 
	 * @return The split ID of the split packet
	 */
	public int getSplitId() {
		return this.splitId;
	}

	/**
	 * Returns the amount of packets needed to complete the split packet
	 * 
	 * @return The amount of packets needed to complete the split packet
	 */
	public int getSplitCount() {
		return this.splitCount;
	}

	/**
	 * Returns the reliability of the split packet
	 * 
	 * @return The reliability of the split packet
	 */
	public Reliability getReliability() {
		return this.reliability;
	}

	/**
	 * Updates the data for the split packet while also verifying that the
	 * specified <code>EncapsulatedPacket</code> belongs to this split packet
	 * 
	 * @param encapsulated
	 *            The <code>EncapsulatedPacket</code> being used to update the
	 *            data
	 * @return The packet if finished, null if data is still missing
	 */
	public Packet update(EncapsulatedPacket encapsulated) {
		// Update payload data
		if (encapsulated.split != true || encapsulated.splitId != this.splitId
				|| encapsulated.splitCount != this.splitCount || encapsulated.reliability != this.reliability) {
			throw new IllegalArgumentException("This split packet does not belong to this one");
		}
		payloads.put(encapsulated.splitIndex, encapsulated.payload);

		// If the map is large enough then put the packet together and return it
		if (payloads.size() >= this.splitCount) {
			Packet finalPayload = new Packet();
			for (int i = 0; i < payloads.size(); i++) {
				finalPayload.write(payloads.get(i).array());
			}
			return finalPayload;
		}

		// The packet is not yet ready
		return null;
	}

	/**
	 * Returns whether or not the packet needs to be split
	 * 
	 * @param reliability
	 *            The reliability of the packet
	 * @param packet
	 *            The packet
	 * @param maximumTransferUnit
	 *            The maximum transfer unit of the session
	 * @return Whether or not the packet needs to be split
	 */
	public static boolean needsSplit(Reliability reliability, Packet packet, int maximumTransferUnit) {
		return (CustomPacket.calculateDummy()
				+ EncapsulatedPacket.calculateDummy(reliability, false, packet) > maximumTransferUnit);
	}

	/**
	 * Splits the specified <code>EncapsulatedPacket</code> using the specified
	 * maximumTransferUnit
	 * 
	 * @param session
	 *            The session
	 * @param encapsulated
	 *            The <code>EncapsulatedPacket</code> to split
	 * @return The split <code>EncapsulatedPacket</code>'s
	 */
	public static final EncapsulatedPacket[] splitPacket(RakNetSession session, EncapsulatedPacket encapsulated) {
		// Get split packet data
		byte[][] split = ArrayUtils.splitArray(encapsulated.payload.array(), session.getMaximumTransferUnit()
				- CustomPacket.calculateDummy() - EncapsulatedPacket.calculateDummy(encapsulated.reliability, true));
		EncapsulatedPacket[] splitPackets = new EncapsulatedPacket[split.length];

		// Encode encapsulated packets
		for (int i = 0; i < split.length; i++) {
			// Set the base parameters
			EncapsulatedPacket encapsulatedSplit = new EncapsulatedPacket();
			encapsulatedSplit.reliability = encapsulated.reliability;
			encapsulatedSplit.payload = new Packet(split[i]);

			// Set reliability specific parameters
			encapsulatedSplit.messageIndex = (encapsulated.reliability.isReliable() ? session.bumpMessageIndex()
					: encapsulated.messageIndex);
			if (encapsulated.reliability.isOrdered() || encapsulated.reliability.isSequenced()) {
				encapsulatedSplit.orderChannel = encapsulated.orderChannel;
				encapsulatedSplit.orderIndex = encapsulated.orderIndex;
			}

			// Set the split related parameters
			encapsulatedSplit.split = true;
			encapsulatedSplit.splitCount = split.length;
			encapsulatedSplit.splitId = encapsulated.splitId;
			encapsulatedSplit.splitIndex = i;
			splitPackets[i] = encapsulatedSplit;
		}

		return splitPackets;
	}

}
