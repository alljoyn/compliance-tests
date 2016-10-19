package com.at4wireless.test;

import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Helper
{
	public static boolean evalExpression(List<String> variables, int[] values, String expression) throws ScriptException
	{		
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		for (int i = 0; i < values.length; i++)
		{
			engine.eval(variables.get(i) + " = " + values[i]);
		}

		return (Integer) engine.eval(expression) == 1.0;
	}
	
	@SuppressWarnings("static-access")
	public static List<String> getVariablesFromBooleanExpression(String expression)
	{
		StringCharacterIterator sci = new StringCharacterIterator(expression); // we use a string iterator for iterating over each character
		List<String> strings = new ArrayList<String>(); // this will be our array of strings in the end
		StringBuilder sb = new StringBuilder(); // a string builder for efficiency
		
		for (char c = sci.first(); c != sci.DONE; c = sci.next())
		{
			if ( c == ' ' ) {
				continue; // we ignore empty spaces
			}
			else if (
					c == '&' ||
					c == '(' ||
					c == ')' ||
					c == '|' ||
					c == '!')
			{
				// if we stumble upon one of 'tokens', we first add any previous strings that are variables to our string array (if does not exist yet)
				// and finally clean our StringBuilder
				if (sb.length() != 0)
				{
					if (!strings.contains(sb.toString()))
					{
						strings.add(sb.toString());
					}
				}
				sb = new StringBuilder();
			}
			else
			{
				sb.append(c);
			}
		}
		
		return strings;
	}
}
