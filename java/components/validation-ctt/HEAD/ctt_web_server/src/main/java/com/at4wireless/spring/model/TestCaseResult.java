/*******************************************************************************
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright 2016 Open Connectivity Foundation and Contributors to
 *      AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/

package com.at4wireless.spring.model;

public class TestCaseResult
{
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	private String execTimestamp;
	public String getExecTimestamp() { return execTimestamp; }
	public void setExecTimestamp(String execTimestamp) { this.execTimestamp = execTimestamp; }
	
	private String version;
	public String getVersion() { return version; }
	public void setVersion(String version) { this.version = version; }
	
	private String verdict;
	public String getVerdict() { return verdict; }
	public void setVerdict(String verdict) { this.verdict = verdict; }
	
	private String log;
	public String getLog() { return log; }
	public void setLog(String log) { this.log = log; }
}