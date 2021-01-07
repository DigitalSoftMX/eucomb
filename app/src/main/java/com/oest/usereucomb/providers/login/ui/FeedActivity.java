package com.oest.usereucomb.providers.login.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.oest.usereucomb.R;
import com.oest.usereucomb.providers.rss.RSSFeed;
import com.oest.usereucomb.providers.rss.RSSHandler;
import com.oest.usereucomb.providers.rss.RSSItem;
import com.oest.usereucomb.providers.rss.RssAdapter;
import com.oest.usereucomb.util.Helper;
import com.oest.usereucomb.util.InfiniteRecyclerViewAdapter;
import com.oest.usereucomb.util.Log;
import com.oest.usereucomb.util.ThemeUtils;
import com.oest.usereucomb.util.ViewModeUtils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FeedActivity extends AppCompatActivity {

    private RSSFeed rssFeed = null;
    private ArrayList<RSSItem> postsList;
    private RssAdapter listAdapter;
    private ViewModeUtils viewModeUtils;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Activity mAct;
    private RelativeLayout ll;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.fragment_list_refresh);

        RecyclerView listView = findViewById(R.id.list);
        postsList = new ArrayList<>();
        listAdapter = new RssAdapter(this, postsList);
        listAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_PROGRESS);
        listView.setAdapter(listAdapter);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }


    private class RssTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL rssUrl = new URL(url);
                SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                XMLReader myXMLReader = mySAXParser.getXMLReader();
                RSSHandler myRSSHandler = new RSSHandler();
                myXMLReader.setContentHandler(myRSSHandler);
                InputSource myInputSource = new InputSource(rssUrl.openStream());
                myXMLReader.parse(myInputSource);

                rssFeed = myRSSHandler.getFeed();

            } catch (ParserConfigurationException | IOException | SAXException e) {
                Log.printStackTrace(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (rssFeed != null) {
                if (rssFeed.getList().size() > 0) {
                    postsList.addAll(rssFeed.getList());
                }

                listAdapter.setHasMore(false);
                listAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_LIST);
                swipeRefreshLayout.setRefreshing(false);

            } else {
                String message = null;
                if (!url.startsWith("http"))
                    message = "Debug info: '" + url + "' is most likely not a valid RSS url. Make sure the url entered in your configuration starts with 'http' and verify if it's valid XML using https://validator.w3.org/feed/";
                Helper.noConnection(mAct, message);

                listAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_EMPTY);
                swipeRefreshLayout.setRefreshing(false);

            }

            super.onPostExecute(result);
        }

    }



    private void refreshItems() {
        postsList.clear();
        listAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_PROGRESS);
        new RssTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModeUtils.handleSelection(item, new ViewModeUtils.ChangeListener() {
            @Override
            public void modeChanged() {
                listAdapter.notifyDataSetChanged();
            }
        });
        switch (item.getItemId()) {
            case R.id.info:
                //show information about the feed in general in a dialog
                if (rssFeed != null) {
                    String FeedTitle = (rssFeed.getTitle());
                    String FeedDescription = (rssFeed.getDescription());
                    //String FeedPubdate = (myRssFeed.getPubdate()); most times not present
                    String FeedLink = (rssFeed.getLink());

                    AlertDialog.Builder builder = new AlertDialog.Builder(mAct);

                    String titlevalue = getResources().getString(R.string.feed_title_value);
                    String descriptionvalue = getResources().getString(R.string.feed_description_value);
                    String linkvalue = getResources().getString(R.string.feed_link_value);

                    if (FeedLink.equals("")) {
                        builder.setMessage(titlevalue + ": \n" + FeedTitle +
                                "\n\n" + descriptionvalue + ": \n" + FeedDescription);
                    } else {
                        builder.setMessage(titlevalue + ": \n" + FeedTitle +
                                "\n\n" + descriptionvalue + ": \n" + FeedDescription +
                                "\n\n" + linkvalue + ": \n" + FeedLink);
                    }

                    builder.setNegativeButton(getResources().getString(R.string.ok), null)
                            .setCancelable(true);
                    builder.create();
                    builder.show();

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
