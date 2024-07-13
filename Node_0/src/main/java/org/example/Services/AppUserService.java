package org.example.Services;

import org.example.Entity.AppUsers;

public interface AppUserService {

    String registerUser(AppUsers appUser);

    String setEmail(AppUsers appUser, String email);
}
