package com.ynov.dystraite.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.ynov.dystraite.entities.Tips;
import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.repositories.TipsRepository;
import com.ynov.dystraite.repositories.UsersRepository;

@Service
public class TipsService {
	
	@Autowired
	TipsRepository tipsRepo;
	
	@Autowired
	UsersRepository userRepo;
	
	public Tips getById(@PathVariable int id) {
		Optional<Tips> tip= tipsRepo.findById(id);
		if(!tip.isPresent()) {
			System.out.println("Tip not found");
		}
		return tip.get();
	}
	
	public List<Tips> getAll() {
		return tipsRepo.findAll();
	}
	public Tips create(Tips tip, Authentication authentication) {
		tip.setOwner(this.userRepo.findByEmail(authentication.getName()));
		tip.setCreatedAt(new Date());
		return tipsRepo.save(tip);
	}
	public Tips delete(@PathVariable int id, Authentication authentication) {
		Optional<Tips> tip= tipsRepo.findById(id);
		if(!tip.isPresent()) {
			System.out.println("Tip not found");
		}
		if(tip.get().getOwner().getEmail().equals(authentication.getName())){
			tipsRepo.deleteById(id);
		}
		return tip.get();
	}
	public Tips update(Tips newTip) {
		return tipsRepo.save(newTip);
	}
	
	public Tips like(Users user, Tips tip) {
		Optional<Users> opUser = userRepo.findById(user.getId());
		Optional<Tips> opTip = tipsRepo.findById(tip.getId());

		if(opUser.isPresent() && opTip.isPresent()) {
			if(opTip.get().getLikes().contains(opUser.get())) {
				opTip.get().getLikes().remove(opUser.get());
			}else {
				opTip.get().getLikes().add(opUser.get());
			}
			this.tipsRepo.save(opTip.get());
		}
		return tip;
	}
	
	public List<Tips> findLastCreated(int limit){
		return tipsRepo.findLastCreated(limit);
	}

}
