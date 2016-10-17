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
package net.marfgamer.raknet;

import java.util.UUID;

/**
 * Contains info for RakNet
 *
 * @author MarfGamer
 */
public interface RakNet {

	/**
	 * Server takes higher bits, client takes lower bits
	 */
	public static final UUID UNIQUE_ID_BITS = UUID.randomUUID();

	public static final int SERVER_NETWORK_PROTOCOL = 8;
	public static final int CLIENT_NETWORK_PROTOCOL = 8;

	/*
	 * Most computers these days have up to around 1400-1500 for their MTU, if
	 * it's lower than that it is most likely outdated. It is suspected that the
	 * minimum MTU is 530, but I am not exactly sure. The client seems to go
	 * that low but I am also not sure what it's algorithm for selecting it's
	 * MTU is either.
	 */
	public static final int MINIMUM_TRANSFER_UNIT = 530;
	public static final int DEFAULT_MAXIMUM_TRANSFER_UNIT = 1280;

	public static final int MAX_CHANNELS = 32;
	public static final int MAX_SPLIT_COUNT = 128;
	public static final int MAX_SPLITS_PER_QUEUE = 4;
	public static final int MAX_PACKETS_PER_SECOND = 500;

}
