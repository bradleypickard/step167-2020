package com.google.sps.meltingpot.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.sps.meltingpot.data.DBUtils;
import java.util.Date;

public class Comment {
  public static final String CONTENT_KEY = "content";
  public static final String TIMESTAMP_KEY = "timestamp";
  public static final String CREATOR_ID_KEY = "creatorId";
  public static final String VOTES_KEY = "votes";

  public String content;
  public final long timestamp;
  public String creatorId;
  public long votes;
  

  public Comment(String content, String creatorId) {
    this.content = content;
    this.timestamp = System.currentTimeMillis();
    this.creatorId = creatorId;
    this.votes = 0;
  }

  // TODO: get user display name (or username) from db collection.

  public static boolean createdbyUser(String recipeId, String commentId, String userId) {
    DocumentSnapshot comment = DBUtils.blockOnFuture(DBUtils.comment(recipeId, commentId).get());

    String commentCreatorId = comment.getString(CREATOR_ID_KEY);
    return commentCreatorId.equals(userId);
  }
}
