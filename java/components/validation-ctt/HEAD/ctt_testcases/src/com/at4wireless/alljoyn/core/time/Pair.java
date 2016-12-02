/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.core.time;

public class Pair<A, B>
{
	private A first;
	private B second;
	
	public Pair(A first, B second)
	{
		super();
		this.first = first;
		this.second = second;
	}
	
	public int hashCode()
	{
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;
		
		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other)
	{
		if (other instanceof Pair) {
			Pair otherPair = (Pair) other;
			return
			(( this.first == otherPair.first ||
					( this.first != null && otherPair.first != null &&
					  this.first.equals(otherPair.first))) &&
			 (		this.second == otherPair.second ||
					( this.second != null && otherPair.second != null &&
					  this.second.equals(otherPair.second))) );
		}
		
		return false;
	}
	
	public String toString()
	{
		return "(" + first + ", " + second +")";
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}
}