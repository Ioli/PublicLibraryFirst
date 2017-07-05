package com.example.admin.publiclibraryfirst;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving books data from public library.
 */
public final class Utils {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utils (and an object instance of Utils is not needed).
     */
    private Utils() {
    }

    /**
     * * Query the public library dataset and return a list of {@link Library} objects.
     */
    public static List<Library> fetchLibraryList(String requestUrl) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);


        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Library}s
        List<Library> libraries = extractFeatureFromJson(jsonResponse);
        // Return the list of {@link Library}s
        return libraries;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Library} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Library> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding books to
        List<Library> libraries = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            // Extract the JSONArray associated with the key called "items",
            // which represents a list of features (or books).
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
            // For each book in the bookArray, create an {@link Library} object
            for (int i = 0; i < itemsArray.length(); i++) {
                // Get a single book at position i within the list of books
                JSONObject currentBook = itemsArray.getJSONObject(i);
                // For a given book, extract the JSONObject associated with the
                // key called "Info", which represents a list of all properties
                // for that book.
                JSONObject Info = currentBook.getJSONObject("volumeInfo");
                //Extract the imageLinks JSONObject
                JSONObject imageLinks = Info.getJSONObject("imageLinks");
                String thumbnail;
                if (imageLinks.has("thumbnail")) {
                    //Extract the url of the smallThumbnail
                    thumbnail = imageLinks.optString("thumbnail");
                } else {
                    thumbnail = "Missing Image";
                }
                // Extract the value for the key called "title"
                String title = Info.optString("title");
                // We create a new ArrayList
                // Extract the value for the key called "authors"
                JSONArray authorsArray;
                StringBuilder authors = new StringBuilder();
                if (Info.has("authors")) {
                    authorsArray = Info.getJSONArray("authors");
                    for (int n = 0; n < authorsArray.length(); n++) {
                        authors.append(System.getProperty("line.separator"));
                        authors.append(authorsArray.optString(n));
                    }
                } else {
                    authors.append("Unknown Author");
                }


                String url = null;
                if (Info.has("infoLink")) {
                    url = Info.optString("infoLink");
                }
                // Create a new {@link Library} object with the thumbnail, title, author,
                // and url from the JSON response.
                // (like..."http://books.google.gr/books?id=6tLAyQLSzG0C&dq=android&hl=&source=gbs_api")
                Library book = new Library(thumbnail, title, authors, url);
                // Add the new {@link book} to the list of books.
                libraries.add(book);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return libraries;
    }
}
