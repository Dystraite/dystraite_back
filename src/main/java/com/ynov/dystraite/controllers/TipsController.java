package com.ynov.dystraite.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ynov.dystraite.entities.Tips;
import com.ynov.dystraite.services.TipsService;
import com.ynov.dystraite.services.UsersService;

@RestController
@RequestMapping("tips")
public class TipsController {
	
	@Autowired
	TipsService service;
	
	@Autowired
	UsersService userService;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tips getById(@PathVariable int id) {
		return service.getById(id);
	}
	@RequestMapping(value = "", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tips> getAll() {
		return service.getAll();
	}
	@RequestMapping(value = "", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tips create(Authentication authentication, @RequestBody Tips tip) {
		return service.create(tip, authentication);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tips delete(@PathVariable int id, Authentication authentication) {
		return service.delete(id, authentication);
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tips update(Tips tip) {
		return service.update(tip);
	}
	@RequestMapping(value = "/getLast/{limit}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tips> getLast(@PathVariable int limit) {
		return service.findLastCreated(limit);
	}
	@RequestMapping(value = "/like", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Tips like(@RequestBody Tips tip, Authentication authentication) {
		return this.service.like(userService.getById(authentication.getName()), tip);
	}
}
