package net.marfgamer.raknet.util;

import java.net.InetAddress;

import net.marfgamer.raknet.server.identifier.Identifier;

public class RakNetUtils {
	
	public static Identifier getIdentifier(InetAddress address, int port) {
		return null;
	}
	
	public static long parseLongPassive(String longStr) {
		try {
			return Long.parseLong(longStr);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static int parseIntPassive(String intStr) {
		return (int) RakNetUtils.parseLongPassive(intStr);
	}

}
