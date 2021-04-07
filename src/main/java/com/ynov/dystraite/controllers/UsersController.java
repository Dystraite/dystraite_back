package com.ynov.dystraite.controllers;

import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.models.UserAuth;
import com.ynov.dystraite.services.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ynov.dystraite.entities.Tips;
import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.exceptions.UserNotFoundException;
import com.ynov.dystraite.services.UsersService;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("user")
public class UsersController {
	@Autowired
	UsersService service;
	
	@RequestMapping(value = "/loggedUser", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Users get(Authentication authentication) {
		return service.getById(authentication.getName());
	}
	@RequestMapping(value = "", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Users> getAll() {
		return service.getAll();
	}
	@RequestMapping(value = "", method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Users update(@RequestBody Users user) {
		return service.update(user);
	}
	/*@RequestMapping(value = "/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Users create(@RequestBody Users user) {
		user.setPassword(service.encode(user.getPassword()));
		return service.create(user);
	}
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public Users delete(@PathVariable int id) {
		return service.delete(id);
	}
	
	@RequestMapping(value = "/speechtherapists/near/{email}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Users> findNearSpeechTherapist(@PathVariable String email) {
		return service.getNearSpeechTherapist(service.getById(email));
	}*/
	@RequestMapping(value = "/sign-up", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserAuth signUp(HttpServletResponse response, @RequestBody Users user) {
		return service.create(response, user);
	}
	@RequestMapping(value = "/likedTips", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tips> getLikedTips() {
		Users user = new Users();
		return user.getLikedTips();
	}
	@RequestMapping(value = "/tips", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tips> getTips() {
		Users user = new Users();
		return user.getTips();
	}
	
	
}
