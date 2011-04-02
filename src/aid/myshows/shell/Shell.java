/*
Copyright (c) 2011, Ilya Arefiev <arefiev.id@gmail.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 * Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.
 * Neither the name of the author nor the names of its
   contributors may be used to endorse or promote products derived
   from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package aid.myshows.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.json.JSONArray;

import aid.lib.myshows.MyshowsClient;

/**
 * 
 * @author Ilya Arefiev (arefiev.id@gmail.com)
 *
 */
public class Shell {
	
	protected BufferedReader reader=null;
	protected PrintWriter writer=null;
	
	protected String cmd=null;
	
	protected MyshowsClient mshClient=null;
	
	/**
	 * shell constructor<br>
	 * using reader and writer (params) to read/write into console with correct
	 * encoding. if nothing passed as params - uses defalut InputStreamReader
	 * and OutputStreamWriter
	 * @param _reader InputStreamReader with correct encoding to use for stdin 
	 * @param _writer OutputStreamWriter with correct encoding to use as stdout
	 */
	public Shell(BufferedReader _reader, PrintWriter _writer) {
		System.out.println("+++ Shell()");
		
		if ( _reader.equals(null) ) {
			reader=new BufferedReader(
					new InputStreamReader(System.in)
					);
		} else {
			reader=_reader;
		}
		
		if ( _writer.equals(null) ) {
			writer=new PrintWriter(
					new OutputStreamWriter(System.out),
					true
					);
		} else {
			writer=_writer;
		}
		
		cmd=new String("");
		
		mshClient=new MyshowsClient();
	}
	
	//--------------------------------------------------------------------------
	
	/**
	 * main loop<br>
	 * reads cmds, parses them and executes api's calls<br>
	 * cmd string checking placed in frequency order
	 */
	public void run() {
		try {
			do {
				writer.print("> ");
				writer.flush();
				
				cmd=reader.readLine();
				if ( cmd==null || cmd.trim().equals("exit") ) {
					break;
				}
				
				// high frequency
				 
				if ( cmd.trim().startsWith("ls") ) {
					cmd=cmd.replaceFirst("ls", "").trim();
					
					ls(cmd);
					continue;
				}
				
				if ( cmd.trim().startsWith("check") ) {
					cmd=cmd.replaceFirst("check", "").trim();
					
					check(cmd);
					continue;
				}
				
				if ( cmd.trim().startsWith("uncheck") ) {
					cmd=cmd.replaceFirst("uncheck", "").trim();
					
					uncheck(cmd);
					continue;
				}
				
				// low frequency
				
				if ( cmd.trim().startsWith("help") || cmd.trim().equals("?") ) {
					help();
					continue;
				}
				
				if ( cmd.trim().startsWith("login") ) {
					cmd=cmd.replaceFirst("login", "").trim();
					
					login(cmd);
					continue;
				}
				
				if ( cmd.trim().startsWith("logout") ) {
					logout();
					continue;
				}
				
				writer.println("Unknown cmd: '"+cmd+"'");
				writer.flush();
				
			} while ( true );
			
			writer.println("\nbue!");
			writer.flush();
			
		} catch (Exception e) {
			writer.println("--- oops: "+e.getMessage());
			writer.flush();
			e.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
	
	/**
	 * prints known commands and help for them
	 */
	protected void help() {
		writer.println(
				"commands:\n"+
				"\t"+"help || ? - this help message"+"\n"+
				"\t"+"login <username> <password> - login into MyShows"+"\n"+
				"\t"+"logout - logout from MyShows"+"\n"+
				"\t"+"exit - exit from shell"+"\n"+
				"\t"+"ls [$showId] [seen] [next] [unwatched] - list shows/episodes"+"\n"+
				"\t"+"check [$episodeId] [$raio] - mark show/episode as seen [with ratio]"+"\n"+
				"\t"+"uncheck [$episodeId] - mark show/eisode as unseen"+"\n"
				);
		writer.flush();
	}
	
	/**
	 * calls <code>login()</code> @ API
	 * @param _args string, contains user account info (username and password),
	 * 		separated by space ' '<br>
	 * 		Notifies about insufficient arguments
	 */
	protected void login(String _args) {
		if ( _args==null ) {
			// TODO: rewrite: don't call .split method - exit immediately
			_args="";	// workaround to show help message
		}
		
		String[] args=_args.split(" ");
		boolean es=false;
		
		if ( args.length>=2 ) {
			es=mshClient.login(args[0], args[1]);
		} else {
			writer.println("--- not enought params");
			writer.println("use: login <username> <password>");
			writer.flush();
		}
		
		writer.println("+++ login: "+ (es ? "done" : "failed") );
		writer.flush();
	}
	
	/**
	 * calls <code>logout()</code> @ API
	 */
	protected void logout() {
		boolean es=mshClient.logout();
		
		writer.println("+++ logout: "+ (es ? "done" : "failed") );
		writer.flush();
	}
	
	protected void ls(String _args) {
		JSONArray result=null;
		
		// TODO: parse shows and episodes case
		/*
		 * params: seen, next, unwatched
		 */
		
		if ( _args==null || _args.equals("") ) {
			result=mshClient.getShows();
		} else {
			String args[]=_args.split(" ");
			
			int show=-1;
			
			if ( args.length>0 ) {
				try {
					show=Integer.parseInt(args[0]);
				} catch (Exception e) {
					show=-1;
				}
			}
			
			if ( _args.contains("seen") ) {
				result=mshClient.getSeenEpisodes(show);
			} else if ( _args.contains("next") ) {
				result=mshClient.getNextEpisodes(show);
			} else if ( _args.contains("unwatched") ) {
				result=mshClient.getUnwatchedEpisodes(show);
			} else {
				// TODO: implement listing all show episodes
			}
		}
		
		if ( result!=null ) {
			try {
				writer.println( result.toString(2) );
			} catch (Exception e) {
				writer.println("--- oops: "+e.getMessage());
				writer.flush();
				e.printStackTrace();
			}
		} else {
			writer.println("--- result=null");
			writer.flush();
		}
		
	}
	
	protected void check(String _args) {
		if ( _args==null || _args.equals("") ) {
			writer.println("--- not enought params");
			writer.flush();
			
			return;
		}
		
		String[] args=_args.split(" ");
		int episode=-1;
		int ratio=-1;
		
		// TODO: parse multiply episodes
		
		if ( args.length>0 ) {
			try {
				episode=Integer.parseInt( args[0] );
			} catch (Exception e) {
				writer.println("--- wrong episode number: "+args[0]);
				writer.flush();
				
				return;
			}
		} else {
			writer.println("--- not enought params");
			writer.flush();
		}
		
		if ( args.length>1 ) {
			try {
				ratio=Integer.parseInt( args[1] );
			} catch (Exception e) {
				ratio=-1;
			}
		}
		
		boolean es=mshClient.checkEpisode(episode, ratio);
		writer.println("+++ check: "+ (es ? "done" : "failed") );
		writer.flush();
	}
	
	protected void uncheck(String _args) {
		if ( _args==null || _args.equals("") ) {
			writer.println("--- not enought params");
			writer.flush();
			
			return;
		}
		
		// TODO: parse multiply episodes
		
		String[] args=_args.split(" ");
		int episode=-1;
		
		if ( args.length>0 ) {
			try {
				episode=Integer.parseInt( args[0] );
			} catch (Exception e) {
				writer.println("--- wrong episode number: "+args[0]);
				writer.flush();
				
				return;
			}
		} else {
			writer.println("--- not enought params");
			writer.flush();
		}
		
		boolean es=mshClient.unCheckEpisode(episode);
		writer.println("+++ uncheck: "+ (es ? "done" : "failed") );
		writer.flush();
	}
}
