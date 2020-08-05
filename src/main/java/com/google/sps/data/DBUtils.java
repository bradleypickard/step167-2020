package com.google.sps.meltingpot.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.concurrent.ExecutionException;

public class DBUtils {

  private static final Firestore actualDatabase = FirestoreClient.getFirestore();
  private static final CollectionReference actualRecipesReference = actualDatabase.collection("recipes");
  private static final CollectionReference actualUsersReference = actualDatabase.collection("users");
  private static final String DB_COMMENTS = "comment-collection";

  private static Firestore database;
  private static CollectionReference recipesReference;
  private static CollectionReference usersReference;

  public static void testModeWithParams(Firestore db, CollectionReference recipesRef, CollectionReference usersRef) {
    // Use this method to inject mock db and recipe reference when testing
    // SHOULD ONLY BE USED IN TESTS
    database = db;
    recipesReference = recipesRef;
    usersReference = usersRef;
  }

  public static void productionMode() {
    database = actualDatabase;
    recipesReference = actualRecipesReference;
  }

  public static Firestore db() {
    return database;
  }

  public static CollectionReference users() {
    return usersReference;
  }

  public static DocumentReference user(String userID) {
    return usersReference.document(userID);
  }

  public static String getNestedPropertyName(String property, String nestedProperty) {
    return property + "." + nestedProperty;
  }

  public static CollectionReference recipes() {
    return recipesReference;
  }

  public static DocumentReference recipe(String recipeID) {
    return recipesReference.document(recipeID);
  }

  public static CollectionReference comments(String recipeID) {
    return recipesReference.document(recipeID).collection(DB_COMMENTS);
  }

  public static DocumentReference comment(String recipeId, String commentId) {
    return recipesReference.document(recipeId).collection(DB_COMMENTS).document(commentId);
  }

  public static String commentsCollectionName() {
    return DB_COMMENTS;
  }

  public static <T> T blockOnFuture(ApiFuture<T> future) {
    try {
      return future.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return null;
  }
}
