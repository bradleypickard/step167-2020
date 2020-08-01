package com.google.sps.meltingpot.data;

import java.util.Date;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.sps.meltingpot.data.DBUtils;

public class Comment {
  public static final String CONTENT_KEY = "content";
  public static final String DATE_KEY = "date";
  public static final String CREATOR_ID_KEY = "creatorId";

  public String content;
  public final Date date;
  public String creatorId;

  public Comment(String content, String creatorId) {
    this.content = content;
    this.date = new Date();
    this.creatorId = creatorId;
  }

  // TODO: get user display name (or username) from db collection.

  public static boolean createdbyUser(String recipeId, String commentId, String userId) {
    DocumentReference commentRef = DBUtils.comment(recipeId, commentId);
    ApiFuture<DocumentSnapshot> commentFuture = commentRef.get();
    DocumentSnapshot comment = DBUtils.blockOnFuture(commentFuture);

    String commentCreatorId = comment.getString(CREATOR_ID_KEY);
    return commentCreatorId.equals(userId);
  }
}
