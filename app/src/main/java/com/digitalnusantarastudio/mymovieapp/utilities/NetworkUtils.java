/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digitalnusantarastudio.mymovieapp.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    final static private String GITHUB_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    final static private String MOVIE_POSTER_BASE_URL =
            "https://image.tmdb.org/t/p/";

    final static private String PARAM_API_KEY = "api_key";
    final static private String API_KEY = "YOUR API KEY HERE";
    final static private String IMAGE_SIZE = "w185";


    /**
     * Builds the URL used to query GitHub.
     *
     * @param sort_by The keyword that will be queried for.
     * @return The URL to use to query the GitHub.
     */
    public static URL buildUrl(String sort_by) {
        Uri builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
                .appendPath(sort_by)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * Builds the URL used to query GitHub.
     *
     * @param poster_image_name The keyword that will be queried for.
     * @return The URL to use to query the GitHub.
     */
    public static URL buildMoviePosterUrl(String poster_image_name) {

        poster_image_name = poster_image_name.startsWith("/") ? poster_image_name.substring(1) : poster_image_name;//remove "/"

        Uri builtUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE)
                .appendPath(poster_image_name.substring(1))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}