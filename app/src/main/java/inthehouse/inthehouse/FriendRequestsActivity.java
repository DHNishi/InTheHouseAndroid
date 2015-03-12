package inthehouse.inthehouse;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class FriendRequestsActivity extends ActionBarActivity {

    private ArrayList<Person> mSenders;

    private PersonListAdapter mSendersAdapter;

    private ListView mRequestsVw;
    private TextView mNoRequestsVw;
    private Button mRefreshVw;

    private static final String TAG = "Friend Requests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        mRequestsVw = (ListView) findViewById(R.id.friendRequests);
        mNoRequestsVw = (TextView) findViewById(R.id.noRequestsText);
        mRefreshVw = (Button) findViewById(R.id.refreshBtn);

        mNoRequestsVw.setVisibility(View.GONE);
        mRefreshVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSenders = new ArrayList<Person>();
                loadFriendRequests();
            }
        });

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
