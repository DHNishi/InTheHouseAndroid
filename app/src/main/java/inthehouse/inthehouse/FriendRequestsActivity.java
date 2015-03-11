package inthehouse.inthehouse;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import inthehouse.inthehouse.Persistence.PreferenceStorage;

public class FriendRequestsActivity extends ActionBarActivity {

    private ArrayList<Person> mSenders;

    private PersonListAdapter mSendersAdapter;

    private ListView mRequestsVw;
    private TextView mNoRequestsVw;

    private static final String TAG = "Friend Requests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        mRequestsVw = (ListView) findViewById(R.id.friendRequests);
        mNoRequestsVw = (TextView) findViewById(R.id.noRequestsText);
        mNoRequestsVw.setVisibility(View.GONE);

        mSenders = new ArrayList<Person>();
        loadFriendRequests();
    }

    private void loadFriendRequests() {
        Server.getFriendRequests(this, new Server.ResponseCallback() {
            @Override
            public void execute(InputStream response, int status) {
                try {
                    ArrayList<Map<String, String>> responseData = new ObjectMapper()
                            .readValue(response, ArrayList.class);

                    if (responseData != null) {
                        for (Map<String, String> friend : responseData) {
                            mSenders.add(new Person(
                                    friend.get("name"),
                                    friend.get("id"),
                                    null, null, null
                            ));
                        }

                        // Still keeping these for now for testing
                        mSenders.add(new Person("Bill", "asdfasfd", "http://cdn02.cdn.justjared.com/wp-content/uploads/2008/01/depp-paris/johnny-depp-paris-person-19.jpg", new Timestamp(System.currentTimeMillis()), null));
                        mSenders.add(new Person("Frank", "asdfasfdf", "http://bygghjalphemma.se/wp-content/uploads/2014/11/bill-gates-wealthiest-person.jpg", new Timestamp(System.currentTimeMillis() - 1800000), null));

                        mSendersAdapter = new PersonListAdapter(FriendRequestsActivity.this,
                                mSenders, RequestView.class);
                        mRequestsVw.setAdapter(mSendersAdapter);
                    }
                    else {
                        mNoRequestsVw.setVisibility(View.VISIBLE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    public void removeRequestBySender(Person sender) {
        mSenders.remove(sender);
        mSendersAdapter.notifyDataSetChanged();
    }
}
