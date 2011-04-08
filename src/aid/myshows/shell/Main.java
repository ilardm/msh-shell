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

/**
 * main class of shell<br>
 * determines correct codepage(currently based on running operating system:
 * using UTF-8 for unix-like systems and cp866 for Windows),
 * constructs and executes shell-object with properly configured(correct
 * codepage) input/output streams reader/writer
 * 
 * @author Ilya Arefiev (arefiev.id@gmail.com)
 */
public class Main {

	/**
	 * @param args currently no params supported
	 */
	public static void main(String[] args) {
		
		String os = System.getProperty("os.name", "");
		System.out.println("MyShows shell runs @ " + os);

		String cp = "UTF-8";
		if (os.contains("Windows")) {
			cp = "Cp866";
		}

		try {
			Shell shell=new Shell(
					new BufferedReader(
							new InputStreamReader(System.in, cp)
							),
					new PrintWriter(
							new OutputStreamWriter(System.out, cp),
							true
							)
					);
			
			shell.run();
			System.out.println("shell ended");
			
		} catch (Exception e) {
			System.err.println("--- oops: "+e.getMessage());
			e.printStackTrace();
		}
	}

}
