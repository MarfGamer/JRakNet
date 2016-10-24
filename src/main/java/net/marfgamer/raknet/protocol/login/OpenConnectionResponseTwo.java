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
package net.marfgamer.raknet.protocol.login;

import java.net.InetSocketAddress;

import net.marfgamer.raknet.Packet;
import net.marfgamer.raknet.RakNetPacket;
import net.marfgamer.raknet.protocol.MessageIdentifier;

public class OpenConnectionResponseTwo extends RakNetPacket {

	public boolean magic;
	public long serverGuid;
	public InetSocketAddress clientAddress;
	public int maximumTransferUnit;
	public boolean encryptionEnabled;

	public OpenConnectionResponseTwo(Packet packet) {
		super(packet);
	}

	public OpenConnectionResponseTwo() {
		super(MessageIdentifier.ID_OPEN_CONNECTION_REPLY_2);
	}

	@Override
	public void encode() {
		this.writeMagic();
		this.writeLong(serverGuid);
		this.writeAddress(clientAddress);
		this.writeUShort(maximumTransferUnit);
		this.writeBoolean(encryptionEnabled);
	}

	@Override
	public void decode() {
		this.magic = this.checkMagic();
		this.serverGuid = this.readLong();
		this.clientAddress = this.readAddress();
		this.maximumTransferUnit = this.readUShort();
		this.encryptionEnabled = this.readBoolean();
	}

}
