package com.google.sps.meltingpot.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.sps.meltingpot.data.DBUtils;

public class Auth {
  private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

  public static FirebaseToken verifyIdToken(String idToken) {
    try {
      FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken, true);
      return decodedToken;
    } catch (IllegalArgumentException | FirebaseAuthException e) {
      e.printStackTrace();
      return null;
    }
  }
} 