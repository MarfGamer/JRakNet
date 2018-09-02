/*
 *       _   _____            _      _   _          _
 *      | | |  __ \          | |    | \ | |        | |
 *      | | | |__) |   __ _  | | __ |  \| |   ___  | |_
 *  _   | | |  _  /   / _` | | |/ / | . ` |  / _ \ | __|
 * | |__| | | | \ \  | (_| | |   <  | |\  | |  __/ | |_
 *  \____/  |_|  \_\  \__,_| |_|\_\ |_| \_|  \___|  \__|
 *
 * the MIT License (MIT)
 *
 * Copyright (c) 2016-2018 Whirvis T. Wheatley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * the above copyright notice and this permission notice shall be included in all
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
package com.whirvis.jraknet;

/**
 * Contains info for RakNet
 *
 * @author Whirvis T. Wheatley
 */
public class RakNet {

	/**
	 * Quick note about RakNet protocol versions: If the RakNet protocol version
	 * for Minecraft is ever bumped, please let me know immediately. I would add
	 * a function to change the network protocol values, however I would rather
	 * have myself change it after being notified by someone through an issue on
	 * GitHub so I know the protocol is possibly out of date and that I need to
	 * bump the versions.
	 */

	// Network protocol data
	public static final int SERVER_NETWORK_PROTOCOL = 9;
	public static final int CLIENT_NETWORK_PROTOCOL = 9;
	public static final int MAXIMUM_MTU_SIZE = 1492;
	public static final int MINIMUM_MTU_SIZE = 400;

	// Session data
	public static final int MAX_CHANNELS = 32;
	public static final byte DEFAULT_CHANNEL = 0;
	public static final int MAX_SPLIT_COUNT = 128;
	public static final int MAX_SPLITS_PER_QUEUE = 4;

	// Configurable options
	private static long MAX_PACKETS_PER_SECOND = 500;

	/**
	 * Returns how many packets can be received in the span of a single second
	 * (1000 milliseconds) before a session is blocked.
	 * 
	 * @return how many packets can be received in the span of a single second
	 *         before a session is blocked.
	 */
	public static long getMaxPacketsPerSecond() {
		return MAX_PACKETS_PER_SECOND;
	}

	/**
	 * Sets how many packets can be received in the span of a single second
	 * (1000 milliseconds) before a session is blocked.
	 * 
	 * @param maxPacketsPerSecond
	 *            how many packets can be received in the span of a single
	 *            second before a session is blocked.
	 */
	public static void setMaxPacketsPerSecond(long maxPacketsPerSecond) {
		MAX_PACKETS_PER_SECOND = maxPacketsPerSecond;
	}

	/**
	 * Removes the max packets per second limit so that no matter how many
	 * packets a session sends it will never be blocked. This is unrecommended,
	 * as it can open your server to DOS/DDOS attacks.
	 */
	public static void setMaxPacketsPerSecondUnlimited() {
		MAX_PACKETS_PER_SECOND = Long.MAX_VALUE;
	}

	// Session timing
	public static final long SEND_INTERVAL = 50L;
	public static final long RECOVERY_SEND_INTERVAL = SEND_INTERVAL;
	public static final long PING_SEND_INTERVAL = 2500L;
	public static final long DETECTION_SEND_INTERVAL = PING_SEND_INTERVAL * 2;
	public static final long SESSION_TIMEOUT = DETECTION_SEND_INTERVAL * 5;
	public static final long MAX_PACKETS_PER_SECOND_BLOCK = (1000L * 300);

}
