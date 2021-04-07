package com.ynov.dystraite.services;

import com.ynov.dystraite.entities.Tips;
import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.exceptions.UserExistsException;
import com.ynov.dystraite.exceptions.UserNotFoundException;
import com.ynov.dystraite.models.CustomUserDetails;
import com.ynov.dystraite.models.UserAuth;
import com.ynov.dystraite.repositories.UsersRepository;
import com.ynov.dystraite.security.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {
	@Autowired
	UsersRepository usersRepo;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Users applicationUser = usersRepo.findByEmail(email);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(email);
		}
		return new CustomUserDetails(applicationUser, new ArrayList<>());
	}
	
	public Users getById(@PathVariable String email) throws UserNotFoundException {
		Users user = usersRepo.findByEmail(email);
		if(user == null) {
			throw new UserNotFoundException("User not found");
		}
		return user;
	}
	
	public List<Users> getAll() {
		return usersRepo.findAll();
	}
	
	
	public UserAuth create(HttpServletResponse response, Users user) throws UserExistsException {
		if (
			user != null &&
			user.getEmail() != null &&
			user.getEmail().trim().length() > 0 &&
			user.getPassword() != null &&
			user.getPassword().trim().length() > 0){

			if (usersRepo.findByEmail(user.getEmail()) != null) {
				throw new UserExistsException("This email address is already used: " + user.getEmail());
			}
			String user_pwd_non_encoded = user.getPassword();
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword().trim()));
			Users u = usersRepo.save(user);

			//permet de connecter l'utilisateur après l'enregistrement
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(u.getEmail(), user_pwd_non_encoded);

			Authentication authentication = authenticationManager.authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			if (authentication.isAuthenticated()){
				String token = JWTAuthenticationFilter.generateAndSetToken(u.getEmail());
				return new UserAuth(user, token);
			}
		}
		return null;
	}
	public Users changePassword(String email, String oldPassword, String newPassword) {
		Users user = usersRepo.findByEmail(email);
		if (!user.getPassword().equals(bCryptPasswordEncoder.encode(oldPassword))) {
			throw new UserExistsException(
			          "Incorrect password");
		}
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		return usersRepo.save(user);
	}
	public Users delete(@PathVariable long id) {
		Optional<Users> user = usersRepo.findById(id);
		if(!user.isPresent()) {
			System.out.println("User not found");
		}
		usersRepo.deleteById(id);
		return user.get();
	}
	public Users update(Users newUser) {
		String oldPassword = this.usersRepo.findById(newUser.getId()).get().getPassword();
		newUser.setPassword(oldPassword);
		return usersRepo.save(newUser);
	}

	public List<Users> getNearSpeechTherapist(Users util){
		return usersRepo.findNearest(util.getLatitude(), util.getLongitude());
	}
}
