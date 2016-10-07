/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
*
*    SPDX-License-Identifier: Apache-2.0
*
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*
*    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
*    Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for
*    any purpose with or without fee is hereby granted, provided that the
*    above copyright notice and this permission notice appear in all
*    copies.
*
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "TersePrinter.h"

using namespace ::testing;
using namespace std;

void TersePrinter::OnTestProgramStart(const UnitTest& t_UnitTest)
{

}

void TersePrinter::OnTestProgramEnd(const UnitTest& t_UnitTest)
{
	if ((t_UnitTest.failed_test_case_count() == 0) && (t_UnitTest.successful_test_case_count() == 0))
	{
		CLOG(INFO, "raw") << "NO SUCH TESTCASE";
	}
	else if (t_UnitTest.failed_test_case_count() == 1)
	{
		LOG(INFO) << "Final Verdict: FAIL";
	}
	else if (t_UnitTest.successful_test_case_count() == 1)
	{
		LOG(INFO) << "Final Verdict: PASS";
	}
}

void TersePrinter::OnTestStart(const TestInfo& t_TestInfo)
{

}

void TersePrinter::OnTestPartResult(const TestPartResult& t_TestPartResult)
{
	vector<string> vector = split(t_TestPartResult.message(), '\n');
	const char* message = vector.at(vector.size() - 1).c_str();
	CLOG(INFO, "gtest") << t_TestPartResult.file_name() << "(" << t_TestPartResult.line_number() << ") : " <<
		"Partial Verdict: " << (t_TestPartResult.failed() ? "FAIL: " : "PASS: ") << message;
}

void TersePrinter::OnTestEnd(const TestInfo& t_TestInfo)
{
	string test(t_TestInfo.name());
	std::replace(test.begin(), test.end(), '_', '-');

	LOG(INFO) << "finished: " << test;
}

vector<string> TersePrinter::split(const string &t_StringToSplit, char t_Delim, vector<string> &t_SplittedElements)
{
	std::stringstream ss(t_StringToSplit);
	std::string item;
	while (std::getline(ss, item, t_Delim))
	{
		t_SplittedElements.push_back(item);
	}
	return t_SplittedElements;
}

vector<string> TersePrinter::split(const string &t_StringToSplit, char t_Delim)
{
	std::vector<std::string> elems;
	split(t_StringToSplit, t_Delim, elems);
	return elems;
}