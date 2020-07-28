// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.meltingpot.servlets;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.meltingpot.data.DBReferences;
import com.google.sps.meltingpot.data.Recipe;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.*;
import java.util.stream.Collectors;
import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handles recipe post requests. */
@WebServlet("/api/post")
public class RecipeServlet extends HttpServlet {
  private Gson gson = new Gson();
  private boolean documentNotFound = false;

  // For logs.
  private final Logger logger = Logger.getLogger(RecipeServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String recipeID = request.getParameter("recipeID");
    String json;

    if (recipeID == null)
      json = getRecipeList(response);
    else
      json = getDetailedRecipe(recipeID, response);

    if (documentNotFound || json == "Exception") {
      return;
    }

    response.setContentType("application/json;");
    response.getWriter().println(json);
    logger.log(Level.INFO, json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String data = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    Recipe newRecipe = gson.fromJson(data, Recipe.class);
    if (newRecipe.content == null || newRecipe.title == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    // Add the recipe to the DB recipes collection as a new document.
    DocumentReference recipeDoc = DBReferences.recipes().document();
    recipeDoc.set(newRecipe);

    // Return the recipe's ID in the HTTP response.
    response.setContentType("text/plain");
    response.getWriter().println(recipeDoc.getId());
    logger.log(Level.INFO, recipeDoc.getId().toString());

    response.setStatus(HttpServletResponse.SC_ACCEPTED);
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {}

  private String getRecipeList(HttpServletResponse response) {
    Query query = DBReferences.recipes();
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    ArrayList<Object> recipeList = new ArrayList<>();

    try {
      for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
        recipeList.add(document.getData());
      }
      return gson.toJson(recipeList);
    } catch (InterruptedException e) {
      System.out.println("Attempt to query recipes raised exception: " + e);
    } catch (ExecutionException e) {
      System.out.println("Attempt to query recipes raised exception: " + e);
    }

    return "Exception";
  }

  private String getDetailedRecipe(String recipeID, HttpServletResponse response)
      throws IOException {
    DocumentReference recipeRef = DBReferences.recipes().document(recipeID);
    ApiFuture<DocumentSnapshot> future = recipeRef.get();

    try {
      DocumentSnapshot document = future.get();
      if (document.exists())
        return gson.toJson(document.getData());
      else {
        documentNotFound = true;
        return "";
      }
    } catch (InterruptedException e) {
      System.out.println("Attempt to query single recipe raised exception: " + e);
    } catch (ExecutionException e) {
      System.out.println("Attempt to query single recipe raised exception: " + e);
    }

    return "Exception";
  }
}
