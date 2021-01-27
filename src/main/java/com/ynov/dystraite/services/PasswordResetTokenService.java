package com.ynov.dystraite.services;

import com.ynov.dystraite.entities.PasswordResetTokens;
import com.ynov.dystraite.exceptions.PasswordResetTokenNotFoundException;
import com.ynov.dystraite.repositories.PasswordResetTokenRepository;
import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.Optional;

@Service
public class PasswordResetTokenService {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepo;

    @Autowired
    UsersRepository usersRepo;

    public PasswordResetTokens getByToken(@PathVariable String token) {
        Optional<PasswordResetTokens> PasswordResetToken = passwordResetTokenRepo.findByToken(token);
        if (!PasswordResetToken.isPresent()) {
            System.out.println("PasswordResetToken not found");
            throw new PasswordResetTokenNotFoundException("PasswordResetToken not found");
        }
        return PasswordResetToken.get();
    }

    public PasswordResetTokens createPasswordResetTokenForUser(Users user, String token) {
        PasswordResetTokens passwordResetToken = new PasswordResetTokens(token, user, new Date());
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(new Date());
        System.out.println(new Date());
        passwordResetTokenRepo.save(passwordResetToken);
        return passwordResetToken;
    }
}
