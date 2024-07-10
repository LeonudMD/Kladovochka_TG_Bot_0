package org.example.Services.IMPL;

import org.example.DAO.AppUsersDAO;
import org.example.Services.UserActivationService;
import org.example.Utils.CryptoTool;
import org.springframework.stereotype.Service;

@Service
public class UserActivationServiceIMPL implements UserActivationService {
    private final AppUsersDAO appUsersDAO;
    private final CryptoTool cryptoTool;

    public UserActivationServiceIMPL(AppUsersDAO appUsersDAO, CryptoTool cryptoTool) {
        this.appUsersDAO = appUsersDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var optional = appUsersDAO.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUsersDAO.save(user);
            return true;
        }
        return false;
    }
}
