/******************************************************************************
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
******************************************************************************/
#include "stdafx.h"
#include "AboutTestSuite.h"

#include <chrono>
#include <thread>

#include "TersePrinter.h"

// This macro initializes the Logger
INITIALIZE_EASYLOGGINGPP

void setLoggers()
{
	el::Configurations defaultConf;
	defaultConf.setToDefault();

	// Set main logger
	defaultConf.set(el::Level::Global,
		el::ConfigurationType::ToFile, "false");
	defaultConf.set(el::Level::Global,
		el::ConfigurationType::ToStandardOutput, "true");
	defaultConf.set(el::Level::Global, el::ConfigurationType::Format, "%datetime %level %fbase(%line) : %msg");
	el::Loggers::reconfigureLogger("default", defaultConf);

	// Set a raw logger for messages without header
	el::Logger* rawLogger = el::Loggers::getLogger("raw");
	defaultConf.set(el::Level::Global,
		el::ConfigurationType::Format, "%msg");
	el::Loggers::reconfigureLogger("raw", defaultConf);

	//Set a logger without file_name and line_number for GTest
	el::Logger* gTestLogger = el::Loggers::getLogger("gtest");
	defaultConf.set(el::Level::Global,
		el::ConfigurationType::Format, "%datetime %level %msg");
	el::Loggers::reconfigureLogger("gtest", defaultConf);
}

void configureGTest()
{
	::testing::UnitTest& unitTest = *::testing::UnitTest::GetInstance();
	::testing::TestEventListeners& listeners = unitTest.listeners();

	// Removes the default console output listener from the list so it will
	// not receive events from Google Test and won't print any output. Since
	// this operation transfers ownership of the listener to the caller we
	// have to delete it as well.
	delete listeners.Release(listeners.default_result_printer());

	// Adds the custom output listener to the list. It will now receive
	// events from Google Test and print the alternative output. We don't
	// have to worry about deleting it since Google Test assumes ownership
	// over it after adding it to the list.
	listeners.Append(new TersePrinter);
}

std::string getTestFilter(const std::string& t_TestCaseName)
{
	std::string testSuite, testFilter;

	if (t_TestCaseName.find("About") == 0)
	{
		testSuite = "AboutTestSuite";
	}
	else if (t_TestCaseName.find("EventsActions") == 0)
	{
		testSuite = "EventsActionsTestSuite";
	}
	else if (t_TestCaseName.find("Notification-Consumer") == 0)
	{
		testSuite = "NotifConsumerTestSuite";
	}
	else if (t_TestCaseName.find("Notification") == 0)
	{
		testSuite = "NotifProducerTestSuite";
	}
	else if (t_TestCaseName.find("Config") == 0)
	{
		testSuite = "ConfigTestSuite";
	}
	else if (t_TestCaseName.find("ControlPanel") == 0)
	{
		testSuite = "ControlPanelTestSuite";
	}
	else if (t_TestCaseName.find("Onboarding") == 0)
	{
		testSuite = "OnboardingTestSuite";
	}
	else if (t_TestCaseName.find("LSF_Lamp") == 0)
	{
		testSuite = "LSFLampTestSuite";
	}
	else if (t_TestCaseName.find("LSF_Controller") == 0)
	{
		testSuite = "LSFControllerTestSuite";
	}

	if (!testSuite.empty())
	{
		testFilter = testSuite;
		testFilter.append(".");
		testFilter.append(t_TestCaseName);

		std::replace(testFilter.begin(), testFilter.end(), '-', '_');
	}
	
	return testFilter;
}

int main(int argc, char* argv[])
{
	// Avoid execution if the number of input parameters
	// is not correct
	if (argc != 3)
	{
		return -1;
	}

	setLoggers();
	configureGTest();

	// Sets the test case that is going to be executed.
	// Several test cases could be executed in one execution,
	// but testing software structure limits it to a
	// test case per process
	::testing::FLAGS_gtest_filter = getTestFilter(argv[1]);
	::testing::InitGoogleTest(&argc, argv);

	// This function run all test cases specified in the
	// filter. It is not necessary to pass them as parameter
	// to the test suites.
	int retVal = RUN_ALL_TESTS();

	return retVal;
}
