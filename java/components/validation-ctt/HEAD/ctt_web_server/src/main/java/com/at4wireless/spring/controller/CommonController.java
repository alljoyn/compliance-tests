package com.at4wireless.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value={"/common"})
public class CommonController
{
	@RequestMapping(method=RequestMethod.GET)
	public String common(Model model)
	{
		return "common";
	}
}
