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
package net.marfgamer.jraknet;

import java.net.InetSocketAddress;

import net.marfgamer.jraknet.client.RakNetClient;
import net.marfgamer.jraknet.client.RakNetClientListener;
import net.marfgamer.jraknet.client.Warning;
import net.marfgamer.jraknet.session.RakNetServerSession;

/**
 * Used to test <code>RakNetClient</code> by connecting to the Lifeboat Survival
 * Games server and then disconnecting afterwards
 *
 * @author MarfGamer
 */
public class RakNetClientTest {

    public static void main(String[] args) throws RakNetException {
	RakNetClient client = new RakNetClient();
	client.setListener(new RakNetClientListener() {

	    @Override
	    public void onConnect(RakNetServerSession session) {
		System.out.println("Connected to server with address " + session.getAddress() + "!");
		client.disconnectAndShutdown();
	    }

	    @Override
	    public void onDisconnect(RakNetServerSession session, String reason) {
		System.out.println("Disconnected from server with address " + session.getAddress() + " for reason \""
			+ reason + "\"");
	    }

	    @Override
	    public void onWarning(Warning warning) {
		System.err.println("Warning: " + warning.getMessage());
	    }

	    @Override
	    public void onHandlerException(InetSocketAddress address, Throwable cause) {
		System.err.println("Exception caused by " + address);
		cause.printStackTrace();
	    }

	});
	System.out.println("Created client, connecting to " + UtilityTest.LIFEBOAT_SURVIVAL_GAMES_ADDRESS + "...");

	client.connect(UtilityTest.LIFEBOAT_SURVIVAL_GAMES_ADDRESS);
    }

}
