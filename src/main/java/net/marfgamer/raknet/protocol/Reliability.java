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

/**
 * Contains all the reliability types for RakNet
 * 
 * @author MarfGamer
 */
public enum Reliability {

	/* For the love of god, do not CTRL+SHIFT+F this */
	UNRELIABLE(0, false, false, false, false),
	UNRELIABLE_SEQUENCED(1, false, false, true, false),
	RELIABLE(2, true, false, false, false),
	RELIABLE_ORDERED(3, true, true, false, false),
	RELIABLE_SEQUENCED(4, true, false, true, false),
	UNRELIABLE_WITH_ACK_RECEIPT(5, false, false, false, true),
	UNRELIABLE_SEQUENCED_WITH_ACK_RECEIPT(6, false, false, true, true),
	RELIABLE_WITH_ACK_RECEIPT(7, true, false, false, true),
	RELIABLE_ORDERED_WITH_ACK_RECEIPT(8, true, true, false, true),
	RELIABLE_SEQUENCED_WITH_ACK_RECEIPT(9, true, false, true, true);
	/* You can CTRL+SHIFT+F, but make sure these each stay on their own line */

	private final byte reliability;
	private final boolean reliable;
	private final boolean ordered;
	private final boolean sequenced;
	private final boolean requiresAck;

	private Reliability(int reliability, boolean reliable, boolean ordered, boolean sequenced, boolean requiresAck) {
		this.reliability = (byte) reliability;
		this.reliable = reliable;
		this.ordered = ordered;
		this.sequenced = sequenced;
		this.requiresAck = requiresAck;
	}

	/**
	 * Returns the reliability as a byte
	 * 
	 * @return The reliability as a byte
	 */
	public byte asByte() {
		return this.reliability;
	}

	/**
	 * Returns whether or not the reliability is reliable
	 * 
	 * @return Whether or not the reliability is reliable
	 */
	public boolean isReliable() {
		return this.reliable;
	}

	/**
	 * Returns whether or not the reliability is ordered
	 * 
	 * @return Whether or not the reliability is ordered
	 */
	public boolean isOrdered() {
		return this.ordered;
	}

	/**
	 * Returns whether or not the reliability is sequenced
	 * 
	 * @return Whether or not the reliability is sequenced
	 */
	public boolean isSequenced() {
		return this.sequenced;
	}

	/**
	 * Returns whether or not the reliability requires Acknowledgement
	 * 
	 * @return Whether or not the reliability requires Acknowledgement
	 */
	public boolean requiresAck() {
		return this.requiresAck;
	}

	/**
	 * Returns the reliability based on it's ID
	 * 
	 * @param reliability
	 *            The ID of the reliability to lookup
	 * @return The reliability based on it's ID
	 */
	public static Reliability lookup(int reliability) {
		Reliability[] reliabilities = Reliability.values();
		for (Reliability sReliability : reliabilities) {
			if (sReliability.asByte() == reliability) {
				return sReliability;
			}
		}
		return null;
	}

}
