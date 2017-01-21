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

import static net.marfgamer.jraknet.protocol.MessageIdentifier.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import net.marfgamer.jraknet.stream.PacketDataInput;
import net.marfgamer.jraknet.stream.PacketDataOutput;

/**
 * Used to read and write data with ease
 *
 * @author MarfGamer
 */
public class Packet {

	public static final int ADDRESS_VERSION_IPV4 = 0x04;
	public static final int ADDRESS_VERSION_IPV6 = 0x06;
	public static final int ADDRESS_VERSION_IPV4_LENGTH = 0x04;
	public static final int ADDRESS_VERSION_IPV6_LENGTH = 0x10;
	public static final int ADDRESS_VERSION_IPV6_MYSTERY_LENGTH = 0x0A;

	private final ByteBuf buffer;
	private final PacketDataInput input;
	private final PacketDataOutput output;

	public Packet(ByteBuf buffer) {
		this.buffer = buffer;
		this.input = new PacketDataInput(this);
		this.output = new PacketDataOutput(this);
	}

	public Packet(DatagramPacket datagram) {
		this(Unpooled.copiedBuffer(datagram.content()));
	}

	public Packet(byte[] data) {
		this(Unpooled.copiedBuffer(data));
	}

	public Packet(Packet packet) {
		this(Unpooled.copiedBuffer(packet.buffer));
	}

	public Packet() {
		this(Unpooled.buffer());
	}

	/**
	 * Reads data into the specified byte array
	 * 
	 * @param dest
	 *            The bytes to read the data into
	 */
	public void read(byte[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = buffer.readByte();
		}
	}

	/**
	 * Returns a byte array of the read data with the specified size
	 * 
	 * @param length
	 *            The amount of bytes to read
	 * @return A byte array of the read data with the specified size
	 */
	public byte[] read(int length) {
		byte[] data = new byte[length];
		for (int i = 0; i < data.length; i++) {
			data[i] = buffer.readByte();
		}
		return data;
	}

	/**
	 * Reads a byte
	 * 
	 * @return A single byte
	 */
	public byte readByte() {
		return buffer.readByte();
	}

	/**
	 * Reads a unsigned byte
	 * 
	 * @return A single unsigned byte
	 */
	public short readUByte() {
		return (short) (buffer.readByte() & 0xFF);
	}

	/**
	 * Reads a flipped unsigned byte casted back to a byte
	 * 
	 * @return A flipped unsigned byte casted back to a byte
	 */
	private byte readCFUByte() {
		return (byte) (~buffer.readByte() & 0xFF);
	}

	/**
	 * Returns a byte array of the read flipped unsigned byte's casted back to a
	 * byte
	 * 
	 * @param length
	 *            The amount of bytes to read
	 * @return A byte array of the read flipped unsigned byte's casted back to a
	 *         byte with the specified size
	 */
	private byte[] readCFU(int length) {
		byte[] data = new byte[length];
		for (int i = 0; i < data.length; i++) {
			data[0] = this.readCFUByte();
		}
		return data;
	}

	/**
	 * Reads a boolean (Anything larger than 0 is considered true)
	 * 
	 * @return A boolean
	 */
	public boolean readBoolean() {
		return (this.readUByte() > 0x00);
	}

	/**
	 * Reads a short
	 * 
	 * @return A short
	 */
	public short readShort() {
		return buffer.readShort();
	}

	/**
	 * Reads a little endian short
	 * 
	 * @return A little endian short
	 */
	public short readShortLE() {
		return buffer.readShortLE();
	}

	/**
	 * Reads an unsigned short
	 * 
	 * @return An unsigned short
	 */
	public int readUShort() {
		return (buffer.readShort() & 0xFFFF);
	}

	/**
	 * Reads an unsigned little endian short
	 * 
	 * @return An unsigned little endian short
	 */
	public int readUShortLE() {
		return (buffer.readShortLE() & 0xFFFF);
	}

	/**
	 * Reads a little endian triad
	 * 
	 * @return A little endian triad
	 */
	public int readTriadLE() {
		return (buffer.readByte() & 0xFF) | ((buffer.readByte() & 0xFF) << 8) | ((buffer.readByte() & 0x0F) << 16);
	}

	/**
	 * Reads an integer
	 * 
	 * @return An integer
	 */
	public int readInt() {
		return buffer.readInt();
	}

	/**
	 * Reads a little endian integer
	 * 
	 * @return A little endian integer
	 */
	public int readIntLE() {
		return buffer.readIntLE();
	}

	/**
	 * Reads an unsigned integer
	 * 
	 * @return An unsigned integer
	 */
	public long readUInt() {
		return (buffer.readInt() & 0x00000000FFFFFFFFL);
	}

	/**
	 * Reads an unsigned little endian integer
	 * 
	 * @return An unsigned little endian integer
	 */
	public long readUIntLE() {
		return (buffer.readIntLE() & 0x00000000FFFFFFFFL);
	}

	/**
	 * Reads a long
	 * 
	 * @return A long
	 */
	public long readLong() {
		return buffer.readLong();
	}

	/**
	 * Reads a little endian long
	 * 
	 * @return A little endian long
	 */
	public long readLongLE() {
		return buffer.readLongLE();
	}

	/**
	 * Reads a float
	 * 
	 * @return A float
	 */
	public float readFloat() {
		return buffer.readFloat();
	}

	/**
	 * Reads a double
	 * 
	 * @return A double
	 */
	public double readDouble() {
		return buffer.readDouble();
	}

	/**
	 * Reads a magic array and returns whether or not it is valid
	 * 
	 * @return Whether or not the magic array was valid
	 */
	public boolean checkMagic() {
		byte[] magicCheck = this.read(MAGIC.length);
		return Arrays.equals(MAGIC, magicCheck);
	}

	/**
	 * Reads a UTF-8 String with it's length prefixed by a unsigned short
	 * 
	 * @return A String
	 */
	public String readString() {
		int len = this.readUShort();
		byte[] data = this.read(len);
		return new String(data);
	}

	/**
	 * Reads a UTF-8 String with ti's length prefixed by a unsigned little
	 * endian short
	 * 
	 * @return A String
	 */
	public String readStringLE() {
		int len = this.readUShortLE();
		byte[] data = this.read(len);
		return new String(data);
	}

	/**
	 * Reads an IPv4/IPv6 address
	 * 
	 * @return An IPv4/IPv6 address
	 * @throws UnknownHostException
	 *             Thrown if an error occurs when reading the address
	 */
	public InetSocketAddress readAddress() throws UnknownHostException {
		short version = this.readUByte();
		if (version == ADDRESS_VERSION_IPV4) {
			byte[] addressBytes = this.readCFU(ADDRESS_VERSION_IPV4_LENGTH);
			int port = this.readUShort();
			return new InetSocketAddress(InetAddress.getByAddress(addressBytes), port);
		} else if (version == ADDRESS_VERSION_IPV6) {
			// Read data
			byte[] addressBytes = this.readCFU(ADDRESS_VERSION_IPV6_LENGTH);
			this.read(ADDRESS_VERSION_IPV6_MYSTERY_LENGTH); // Mystery bytes
			int port = this.readUShort();
			return new InetSocketAddress(InetAddress.getByAddress(Arrays.copyOfRange(addressBytes, 0, 16)), port);
		} else {
			throw new UnknownHostException("Unknown protocol IPv" + version);
		}
	}

	/**
	 * Writes the specified byte array to the packet
	 * 
	 * @param data
	 *            The data to write
	 * @return The packet
	 */
	public Packet write(byte[] data) {
		for (int i = 0; i < data.length; i++) {
			buffer.writeByte(data[i]);
		}
		return this;
	}

	/**
	 * Writes the specified amount of null (0x00) bytes to the packet
	 * 
	 * @param length
	 *            The amount of bytes to write
	 * @return The packet
	 */
	public Packet pad(int length) {
		for (int i = 0; i < length; i++) {
			buffer.writeByte(0x00);
		}
		return this;
	}

	/**
	 * Writes a byte to the packet
	 * 
	 * @param b
	 *            The byte
	 * @return The packet
	 */
	public Packet writeByte(int b) {
		buffer.writeByte((byte) b);
		return this;
	}

	/**
	 * Writes an unsigned by to the packet
	 * 
	 * @param b
	 *            The unsigned byte
	 * @return The packet
	 */
	public Packet writeUByte(int b) {
		buffer.writeByte(((byte) b) & 0xFF);
		return this;
	}

	/**
	 * Writes a flipped unsigned byte casted back into a byte to the packet
	 * 
	 * @param b
	 *            The byte
	 * @return The packet
	 */
	private Packet writeCFUByte(byte b) {
		buffer.writeByte(~b & 0xFF);
		return this;
	}

	/**
	 * Writes a byte array of the specified flipped unsigned byte's casted back
	 * to a byte to the packet
	 * 
	 * @param data
	 *            The data to write
	 * @return The packet
	 */
	private Packet writeCFU(byte[] data) {
		for (int i = 0; i < data.length; i++) {
			this.writeCFUByte(data[i]);
		}
		return this;
	}

	/**
	 * Writes a boolean value to the packet
	 * 
	 * @param b
	 *            The boolean
	 * @return The packet
	 */
	public Packet writeBoolean(boolean b) {
		buffer.writeByte(b ? 0x01 : 0x00);
		return this;
	}

	/**
	 * Writes a short to the packet
	 * 
	 * @param s
	 *            The short
	 * @return The packet
	 */
	public Packet writeShort(int s) {
		buffer.writeShort(s);
		return this;
	}

	/**
	 * Writes a little endian short to the packet
	 * 
	 * @param s
	 *            The short
	 * @return The packet
	 */
	public Packet writeShortLE(int s) {
		buffer.writeShortLE(s);
		return this;
	}

	/**
	 * Writes a unsigned short to the packet
	 * 
	 * @param s
	 *            The short
	 * @return The packet
	 */
	public Packet writeUShort(int s) {
		buffer.writeShort(((short) s) & 0xFFFF);
		return this;
	}

	/**
	 * Writes an unsigned little endian short to the packet
	 * 
	 * @param s
	 *            The short
	 * @return The packet
	 */
	public Packet writeUShortLE(int s) {
		buffer.writeShortLE(((short) s) & 0xFFFF);
		return this;
	}

	/**
	 * Writes a little endian triad to the packet
	 * 
	 * @param t
	 *            The triad
	 * @return The packet
	 */
	public Packet writeTriadLE(int t) {
		buffer.writeByte((byte) (t & 0xFF));
		buffer.writeByte((byte) ((t >> 8) & 0xFF));
		buffer.writeByte((byte) ((t >> 16) & 0xFF));
		return this;
	}

	/**
	 * Writes an integer to the packet
	 * 
	 * @param i
	 *            The integer
	 * @return The packet
	 */
	public Packet writeInt(int i) {
		buffer.writeInt(i);
		return this;
	}

	/**
	 * Writes an unsigned integer to the packet
	 * 
	 * @param i
	 *            The integer
	 * @return The packet
	 */
	public Packet writeUInt(long i) {
		buffer.writeInt(((int) i) & 0xFFFFFFFF);
		return this;
	}

	/**
	 * Writes a little endian integer to the packet
	 * 
	 * @param i
	 *            The integer
	 * @return The packet
	 */
	public Packet writeIntLE(int i) {
		buffer.writeIntLE(i);
		return this;
	}

	/**
	 * Writes an unsigned little endian integer to the packet
	 * 
	 * @param i
	 *            The integer
	 * @return The packet
	 */
	public Packet writeUIntLE(long i) {
		buffer.writeIntLE(((int) i) & 0xFFFFFFFF);
		return this;
	}

	/**
	 * Writes a long to the packet
	 * 
	 * @param l
	 *            The long
	 * @return The packet
	 */
	public Packet writeLong(long l) {
		buffer.writeLong(l);
		return this;
	}

	/**
	 * Writes a little endian long to the packet
	 * 
	 * @param l
	 *            The long
	 * @return The packet
	 */
	public Packet writeLongLE(long l) {
		buffer.writeLongLE(l);
		return this;
	}

	/**
	 * Writes a float to the packet
	 * 
	 * @param f
	 *            The float
	 * @return The packet
	 */
	public Packet writeFloat(double f) {
		buffer.writeFloat((float) f);
		return this;
	}

	/**
	 * Writes a double to the packet
	 * 
	 * @param d
	 *            The double
	 * @return The packet
	 */
	public Packet writeDouble(double d) {
		buffer.writeDouble(d);
		return this;
	}

	/**
	 * Writes the magic sequence to the packet
	 * 
	 * @return The packet
	 */
	public Packet writeMagic() {
		this.write(MAGIC);
		return this;
	}

	/**
	 * Writes a UTF-8 String prefixed by an unsigned short to the packet
	 * 
	 * @param s
	 *            The String
	 * @return The packet
	 */
	public Packet writeString(String s) {
		byte[] data = s.getBytes();
		this.writeUShort(data.length);
		this.write(data);
		return this;
	}

	/**
	 * Writes a UTF-8 String prefixed by a little endian unsigned short to the
	 * packet
	 * 
	 * @param s
	 *            The String
	 * @return The packet
	 */
	public Packet writeStringLE(String s) {
		byte[] data = s.getBytes();
		this.writeUShortLE(data.length);
		this.write(data);
		return this;
	}

	/**
	 * Writes an IPv4 address to the packet (Writing IPv6 is not yet supported)
	 * 
	 * @param address
	 *            The address
	 * @return The packet
	 * @throws UnknownHostException
	 *             Thrown if an error occurs when reading the address
	 */
	public Packet writeAddress(InetSocketAddress address) throws UnknownHostException {
		byte[] addressBytes = address.getAddress().getAddress();
		if (addressBytes.length == ADDRESS_VERSION_IPV4_LENGTH) {
			this.writeUByte(ADDRESS_VERSION_IPV4);
			this.writeCFU(addressBytes);
			this.writeUShort(address.getPort());
		} else if (addressBytes.length == ADDRESS_VERSION_IPV6_LENGTH) {
			this.writeUByte(ADDRESS_VERSION_IPV6);
			this.writeCFU(addressBytes);
			this.pad(ADDRESS_VERSION_IPV6_MYSTERY_LENGTH); // Mystery bytes
			this.writeUShort(address.getPort());
		} else {
			throw new UnknownHostException("Unknown protocol IPv" + addressBytes.length);
		}
		return this;
	}

	/**
	 * Writes an IPv4 address to the packet (IPv6 is not yet supported)
	 * 
	 * @param address
	 *            The address
	 * @param port
	 *            The port
	 * @throws UnknownHostException
	 *             Thrown if an error occurs when reading the address
	 */
	public void writeAddress(InetAddress address, int port) throws UnknownHostException {
		this.writeAddress(new InetSocketAddress(address, port));
	}

	/**
	 * Writes an IPv4 address to the packet (IPv6 is not yet supported)
	 * 
	 * @param address
	 *            The address
	 * @param port
	 *            The port
	 * @throws UnknownHostException
	 *             Thrown if an error occurs when reading the address
	 */
	public void writeAddress(String address, int port) throws UnknownHostException {
		this.writeAddress(new InetSocketAddress(address, port));
	}

	/**
	 * Converts the packet to a byte array
	 * 
	 * @return The packet as a byte array
	 */
	public byte[] array() {
		return Arrays.copyOfRange(buffer.array(), 0, buffer.writerIndex());
	}

	/**
	 * Returns the size of the packet in bytes
	 * 
	 * @return The size of the packet in bytes
	 */
	public int size() {
		return array().length;
	}

	/**
	 * Returns the packet's buffer
	 * 
	 * @return The packet's buffer
	 */
	public ByteBuf buffer() {
		return this.buffer.retain();
	}

	/**
	 * Returns the packet's input
	 * 
	 * @return The packet's input
	 */
	public PacketDataInput getDataInput() {
		return this.input;
	}

	/**
	 * Returns the packet's output
	 * 
	 * @return The packet's output
	 */
	public PacketDataOutput getDataOutput() {
		return this.output;
	}

	/**
	 * Returns how many bytes are left in the packet's buffer
	 * 
	 * @return How many bytes are left in the packet's buffer
	 */
	public int remaining() {
		return buffer.readableBytes();
	}

	/**
	 * Clears the packets buffer
	 */
	public void clear() {
		buffer.clear();
	}

}
