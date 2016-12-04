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
package net.marfgamer.jraknet;

/**
 * Contains info for RakNet
 *
 * @author MarfGamer
 */
public class RakNet {

	/**
	 * If this is set to true, the server and client will log as much as they
	 * can for debugging data
	 */
	/* TODO? public static boolean LOGGER_ENABLED = false; */

	// Version data
	public static final int JRAKNET_RELEASE_VERSION = 2;
	public static final int JRAKNET_MAJOR_VERSION = 3;
	public static final int JRAKNET_MINOR_VERSION = 1;

	// Network protocol data
	public static final int SERVER_NETWORK_PROTOCOL = 8;
	public static final int CLIENT_NETWORK_PROTOCOL = 8;

	// Maximum Transfer Unit data
	public static final int MINIMUM_TRANSFER_UNIT = 530;

	// Session limit data
	public static final int MAX_CHANNELS = 32;
	public static final byte DEFAULT_CHANNEL = 0x00;
	public static final int MAX_SPLIT_COUNT = 128;
	public static final int MAX_SPLITS_PER_QUEUE = 4;
	public static final int MAX_PACKETS_PER_SECOND = 500;

}
