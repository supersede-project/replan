package eu.supersede.rp.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestRest {
	
	@RequestMapping("")
	public String test()
	{
		return "test";
	}
	
}
