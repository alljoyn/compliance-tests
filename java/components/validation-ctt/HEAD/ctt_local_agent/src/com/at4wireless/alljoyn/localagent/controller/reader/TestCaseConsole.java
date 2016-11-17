/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.localagent.controller.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

import com.at4wireless.alljoyn.localagent.view.ExecuteTestCaseWindow;

public class TestCaseConsole implements Runnable
{
    JTextArea displayPane;
    BufferedReader reader;
    ExecuteTestCaseWindow executeTestCaseWindow;
    boolean runningAll;
    
    private TestCaseConsole(JTextArea displayPane, PipedOutputStream pos, ExecuteTestCaseWindow executeTestCaseWindow, boolean runningAll)
    {
        this.displayPane = displayPane;
        this.executeTestCaseWindow = executeTestCaseWindow;
        this.runningAll = runningAll;

        try
        {
            PipedInputStream pis = new PipedInputStream( pos );
            reader = new BufferedReader( new InputStreamReader(pis) );
        }
        catch(IOException e) {}
    }

    public void run()
    {
        String line = null;

        try
        {
        	line = reader.readLine();
        	
        	if (line.contains("NO SUCH TESTCASE")) //[AT4] Change to a less trivial sentence
			{	    			
				executeTestCaseWindow.runTestCase("java");
			}
        	else
        	{
        		displayPane.append(line + "\n");
			    displayPane.setCaretPosition( displayPane.getDocument().getLength());
        	}
        	
			while ((line = reader.readLine()) != null)
			{		
			    displayPane.append(line + "\n");
			    displayPane.setCaretPosition( displayPane.getDocument().getLength());
			    
			    if (line.contains(": Final Verdict:") || line.contains(":Final Verdict:")) //[AT4] Second condition until v14.12 TCs update
			    {
			    	if (runningAll)
			    	{
			    		Thread.sleep(2000); //To allow the Test Cases Package to create the XML
			    		executeTestCaseWindow.saveButtonAction();
			    	}
			    	else
			    	{
			    		executeTestCaseWindow.drawSaveButton();
			    	}
			  
			    	this.finalize();
			    	return;
			    }
			}
		}
        catch (IOException e)
        {        	
        	if (runningAll)
        	{
	    		//executeTestCaseWindow.saveButtonAction();
        	}
        	else
        	{
        		executeTestCaseWindow.drawSaveButton();
        	}
		}
        catch (Throwable e)
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void redirectOutput(JTextArea displayPane, ExecuteTestCaseWindow executeTestCaseWindow, boolean runningAll)
    {
        TestCaseConsole.redirectOut(displayPane, executeTestCaseWindow, runningAll);
        TestCaseConsole.redirectErr(displayPane, executeTestCaseWindow, runningAll);
    }

    public static void redirectOut(JTextArea displayPane, ExecuteTestCaseWindow executeTestCaseWindow, boolean runningAll)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setOut( new PrintStream(pos, true) );

        TestCaseConsole console = new TestCaseConsole(displayPane, pos, executeTestCaseWindow, runningAll);
        new Thread(console).start();
    }

    public static void redirectErr(JTextArea displayPane, ExecuteTestCaseWindow executeTestCaseWindow, boolean runningAll)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setErr( new PrintStream(pos, true) );

        TestCaseConsole console = new TestCaseConsole(displayPane, pos, executeTestCaseWindow, runningAll);
        new Thread(console).start();
    }
}