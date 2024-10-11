package com.example.actionprice.user;

import com.example.actionprice.user.forms.UserRegisterForm;

public interface UserService {

  User createUser(UserRegisterForm userRegisterForm);

  boolean checkUserExistsWithUsername(String username);
  boolean checkUserExistsWithEmail(String email);

}
