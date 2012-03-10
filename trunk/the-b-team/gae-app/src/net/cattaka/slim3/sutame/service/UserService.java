package net.cattaka.slim3.sutame.service;

import java.util.List;

import net.cattaka.slim3.sutame.meta.UserModelMeta;
import net.cattaka.slim3.sutame.StameConstants;
import net.cattaka.slim3.sutame.model.UserModel;

import org.slim3.datastore.Datastore;


public class UserService {
    private UserModelMeta userModelMeta = new UserModelMeta();
    
    public UserModel getGuestUser() {
        return getUser(StameConstants.USERNAME_GUEST);
    }
    
    public UserModel getUser(String username) {
        List<UserModel> userModels = Datastore.query(UserModel.class).filter(userModelMeta.username.equal(username)).asList();
        if (userModels.size() > 0) {
            return userModels.get(0);
        } else {
            return registerUser(username);
        }
    }
    
    public UserModel registerUser(String username) {
        if (Datastore.putUniqueValue(StameConstants.UQ_USERNAME, username)) {
            UserModel userModel = new UserModel();
            userModel.setUsername(username);
            Datastore.put(userModel);
            return userModel;
        } else {
            // ありえない
            return null;
        }
    }
}
