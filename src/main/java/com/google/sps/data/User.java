package com.google.sps.meltingpot.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.sps.meltingpot.data.DBUtils;

public class User {
  public static final String CREATED_RECIPES_KEY = "created-recipe-ids";
  public static final String UID_KEY = "user-id";

  public String uid;

  public User(String uid) {
    this.uid = uid;
  }

  public static boolean createdRecipe(String userId, String recipeId) {
    DocumentReference userRef = DBUtils.user(userId);
    ApiFuture<DocumentSnapshot> userFuture = userRef.get();
    DocumentSnapshot user = DBUtils.blockOnFuture(userFuture);
    if (!user.exists()) {
      return false;
    }
    Boolean userCreatedRecipe =
        user.getBoolean(DBUtils.getNestedPropertyName(CREATED_RECIPES_KEY, recipeId));
    // note that userCreatedRecipe is a Boolean, not a boolean
    if (userCreatedRecipe == null || userCreatedRecipe == false) {
      return false;
    }
    return true;
  }
}