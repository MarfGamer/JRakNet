/*
 *    __     ______     ______     __  __     __   __     ______     ______  
 *   /\ \   /\  == \   /\  __ \   /\ \/ /    /\ "-.\ \   /\  ___\   /\__  _\
 *  _\_\ \  \ \  __<   \ \  __ \  \ \  _"-.  \ \ \-.  \  \ \  __\   \/_/\ \/  
 * /\_____\  \ \_\ \_\  \ \_\ \_\  \ \_\ \_\  \ \_\\"\_\  \ \_____\    \ \_\ 
 * \/_____/   \/_/ /_/   \/_/\/_/   \/_/\/_/   \/_/ \/_/   \/_____/     \/_/                                                                          
 *
 * the MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Whirvis T. Wheatley
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
package com.whirvis.jraknet.client.peer;

import java.net.InetSocketAddress;

import com.whirvis.jraknet.client.RakNetClient;

/**
 * Signals that a {@link RakNetClient} attempted to to connect to an offline
 * server.
 *
 * @author Whirvis T. Wheatley
 * @since JRakNet v2.0.0
 */
public final class ServerOfflineException extends PeerFactoryException {

	private static final long serialVersionUID = -3916155995964791602L;

	private final InetSocketAddress address;

	/**
	 * Constructs a <code>ServerOfflineException</code>.
	 * 
	 * @param client
	 *            the client that attempted to the offline server.
	 * @param address
	 *            the address of the offline server.
	 */
	public ServerOfflineException(RakNetClient client, InetSocketAddress address) {
		super(client, "Server at address " + address.toString() + " is offline");
		this.address = address;
	}

	/**
	 * Returns the address of the offline server.
	 * 
	 * @return the address of the offline server.
	 */
	public InetSocketAddress getAddress() {
		return this.address;
	}

}
