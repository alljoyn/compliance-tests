/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package org.alljoyn.smarthome.centralizedmanagement.client;

import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 *	This class used to describe the arguments of method.
 */

public class ArgsDesc {
	
	// Method Name
	/** The method name. */
	@Position(0)
	public String methodName;
	
	// The data type of input parameter
	/** The input value type. */
	@Position(1)
	public String inputValueType;
	
	// The data type of output parameter
	/** The return value type. */
	@Position(2)
	public String returnValueType;
	
	// The name of input parameter and output parameter
	/** The args name. */
	@Position(3)
	public String argsName;

}
