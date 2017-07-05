package com.example.admin.publiclibraryfirst;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * An {@link LibraryAdapter} knows how to create a list item layout for each book research
 * in the data source (a list of {@link Library} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class LibraryAdapter extends ArrayAdapter<Library> {


    private static final String AUTHOR = "Author=>";

    /**
     * Constructs a new {@link LibraryAdapter}.
     *
     * @param context   of the app
     * @param libraries is the list of books, which is the data source of the adapter
     */
    public LibraryAdapter(Activity context, ArrayList<Library> libraries) {
        super(context, 0, libraries);
    }

    private static class MyStorage {
        ImageView thumbnail;
        TextView titleOfBook;
        TextView author;

    }

    /**
     * Returns a list item view that displays information about the library at the given time
     * in the list of books.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyStorage storage;
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View libraryView = convertView;
        if (libraryView == null) {
            libraryView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_presentation, parent, false);

            storage = new MyStorage();
            storage.thumbnail = (ImageView) libraryView.findViewById(R.id.thumbnail);
            storage.titleOfBook = (TextView) libraryView.findViewById(R.id.book_title);
            storage.author = (TextView) libraryView.findViewById(R.id.author);
            libraryView.setTag(storage);
        } else {
            storage = (MyStorage) libraryView.getTag();
        }
        // Find the book at the given position in the list of books in public library
        Library currentBook = getItem(position);

        // Picasso Library to convert the url from JSONObject imageLinks to a image(@thumbnail)
        Picasso.with(getContext()).load(currentBook.getThumbnail()).into(storage.thumbnail);
        // We set the new value to the book title that is returned from the HTTP request
        storage.titleOfBook.setText(currentBook.getTitle());
        //We set the returned value from the HTTP request
        storage.author.setText(AUTHOR + currentBook.getAuthor());


        // Return the list item view that is now showing the appropriate data
        return libraryView;
    }

}
