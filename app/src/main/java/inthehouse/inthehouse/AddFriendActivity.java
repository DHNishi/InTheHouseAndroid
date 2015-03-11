package inthehouse.inthehouse;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;


public class AddFriendActivity extends ActionBarActivity implements
        View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                Log.d("ADD FRIEND", "Submit button hit.");

                Server.addFriend(this, ((EditText) findViewById(R.id.txtEmail)).getText().toString(),
                        new Server.ResponseCallback() {
                    @Override
                    public void execute(InputStream response, int status) {
                        if (response != null)
                            Log.d("ADD FRIEND", "Friend added.");
                        else {
                            Log.d("ADD FRIEND", "There was an error.");
                        }
                        finishActivity(0);
                    }
                }, null);
                break;
        }
    }
}

