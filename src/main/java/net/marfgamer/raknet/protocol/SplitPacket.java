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
 * Copyright (c) 2016 MarfGamer
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
package net.marfgamer.raknet.protocol;

import net.marfgamer.raknet.Packet;
import net.marfgamer.raknet.RakNet;
import net.marfgamer.raknet.protocol.message.EncapsulatedPacket;
import net.marfgamer.raknet.util.map.IntMap;

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
			throw new IllegalArgumentException("Split count can be no greater than " + RakNet.MAX_SPLIT_COUNT + "!");
		}
	}

	/**
	 * Updates the data for the split packet while also verifying that the
	 * specified <code>EncapsulatedPacket</code> belongs to this split packet
	 * 
	 * @param encapsulated
	 *            - The <code>EncapsulatedPacket</code> being used to update the
	 *            data
	 * @return The packet if finished, null if data is still missing
	 */
	public Packet update(EncapsulatedPacket encapsulated) {
		// Update payload data
		if (encapsulated.split != true || encapsulated.splitId != this.splitId
				|| encapsulated.splitCount != this.splitCount || encapsulated.reliability != this.reliability) {
			throw new IllegalArgumentException("This split packet does not belong to this one!");
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

}
