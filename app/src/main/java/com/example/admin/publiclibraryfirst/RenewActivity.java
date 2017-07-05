package com.example.admin.publiclibraryfirst;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class RenewActivity extends AppCompatActivity {


    /**
     * URL for books data from the google example query dataset
     */
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=20&q=";


    /**
     * Adapter for the list of books in a public library
     */
    private LibraryAdapter mAdapter;

    /**
     * ProgressBar that is displayed when we renew the list
     */
    private ProgressBar progressBar;

    /**
     * TextView that is displayed when there isn't internet
     */
    private TextView noConnection;


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        setTitle("Result");


        //We get the user input text
        String bookTitle = getIntent().getStringExtra("TITLE");


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        noConnection = (TextView) findViewById(R.id.no_internet);

        //This method check if there is internet connection
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //We create the new requested url
        String newUrl = BOOK_REQUEST_URL + bookTitle.toLowerCase();

        listView = (ListView) findViewById(R.id.list);
        LibraryAsyncTask task = new LibraryAsyncTask();
        //If there is internet connection we execute the task
        if (isConnected) {
            task.execute(newUrl);
        } else {
            progressBar.setVisibility(View.GONE);
            noConnection.setText(R.string.no_internet_connection);
        }
        mAdapter = new LibraryAdapter(this, new ArrayList<Library>());
        // Find a reference to the {@link list_view} in the layout
        listView = (ListView) findViewById(R.id.list);
        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new LibraryAdapter(this, new ArrayList<Library>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        listView.setAdapter(mAdapter);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected books.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Library currentBook = mAdapter.getItem(position);

                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setData(Uri.parse(currentBook.getUrl()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Send the intent to launch a new activity
                    startActivity(intent);

                }
            }
        });


    }

    private class LibraryAsyncTask extends AsyncTask<String, Void, List<Library>> {
        @Override
        protected List<Library> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Library> renew = Utils.fetchLibraryList(urls[0]);
            return renew;
        }


        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         * <p>
         * It IS okay to modify the UI within this method. We take the {@link Library} object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */


        protected void onPostExecute(List<Library> data) {
            // Loader reset, so we can clear out our existing data.
            mAdapter.clear();
            progressBar.setVisibility(View.GONE);
            // If there is a valid list of {@link Library}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
                // If there is no information on a given book we set a text message to inform the user about it
            } else {
                noConnection.setText(R.string.no_results);
            }

        }


    }

}