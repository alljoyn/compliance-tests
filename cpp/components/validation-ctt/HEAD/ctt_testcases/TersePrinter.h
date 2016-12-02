/******************************************************************************
* * 
*    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
*    Source Project Contributors and others.
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0

******************************************************************************/
#pragma once

#include "gtest/gtest.h"

class TersePrinter : public ::testing::EmptyTestEventListener
{
private:
	virtual void OnTestProgramStart(const ::testing::UnitTest&);
	virtual void OnTestProgramEnd(const ::testing::UnitTest&);
	virtual void OnTestStart(const ::testing::TestInfo&);
	virtual void OnTestPartResult(const ::testing::TestPartResult&);
	virtual void OnTestEnd(const ::testing::TestInfo&);
	std::vector<std::string> split(const std::string&, char);
	std::vector<std::string> split(const std::string&, char, std::vector<std::string>&);
};