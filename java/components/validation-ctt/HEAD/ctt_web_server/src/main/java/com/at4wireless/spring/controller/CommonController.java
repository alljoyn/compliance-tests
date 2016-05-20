package com.at4wireless.spring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;
import com.at4wireless.spring.service.TcclService;

@Controller
@RequestMapping(value={"/common"})
public class CommonController
{
	@Autowired
	private TcclService tcclService;
	
	@RequestMapping(method=RequestMethod.GET)
	public String common(Model model)
	{
		return "common";
	}
	
	@RequestMapping(value="/tccl", method=RequestMethod.GET)
	public @ResponseBody List<Tccl> getTcclList(HttpServletRequest request)
	{
		return tcclService.list();
	}
	
	@RequestMapping(value = "/tccl/edit", method = RequestMethod.GET)
	public @ResponseBody List<TestCaseTccl> editTccl(HttpServletRequest request)
	{
		return tcclService.getTccl(Integer.parseInt(request.getParameter("idTccl")));
	}
	
	/*@RequestMapping(value="/user-manual", method = RequestMethod.GET)
	public void getTestReport(HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		File downloadFile = new File(File.separator + "Allseen" + File.separator + "CTT_user_manual.pdf");
		 
        // set content attributes for the response
        response.setContentType("application/pdf");
        response.setContentLength((int) downloadFile.length());
 
        // set headers for the response
        response.setHeader("Content-Disposition", String.format("inline"+"; filename=\"%s\"", downloadFile.getName()));
        
        FileInputStream is = new FileInputStream(downloadFile);
        OutputStream os = response.getOutputStream();
 
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
 
        // write bytes read from the input stream into the output stream
        while ((bytesRead = is.read(buffer)) != -1)
        {
            os.write(buffer, 0, bytesRead);
        }
 
        is.close();
        os.close();
	} */
}