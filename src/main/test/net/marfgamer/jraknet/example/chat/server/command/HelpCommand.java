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
 * Copyright (c) 2016, 2017 Whirvis "MarfGamer" Ardenaur
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
package net.marfgamer.jraknet.example.chat.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marfgamer.jraknet.example.chat.server.ChatServer;

/**
 * Displays all of the commands that can be used on the server.
 * 
 * @author Whirvis "MarfGamer" Ardenaur
 */
public class HelpCommand extends Command {

   private static final Logger log = LoggerFactory.getLogger(HelpCommand.class);
   
	public HelpCommand() {
		super("help", "Displays all the commands that can be used");
	}

	@Override
	public boolean handleCommand(String[] args) {
		StringBuilder helpMessage = new StringBuilder();
		helpMessage.append("Showing all " + Command.getRegisteredCommands().length + " commands:\n");
		for (Command command : Command.getRegisteredCommands()) {
			helpMessage.append("\t\t");
			helpMessage.append(command.getUsage() + ": " + command.getDescription() + "\n");
		}
		log.info(helpMessage.toString());
		return true;
	}

}
