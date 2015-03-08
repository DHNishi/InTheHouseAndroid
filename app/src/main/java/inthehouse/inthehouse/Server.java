package inthehouse.inthehouse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;

import inthehouse.inthehouse.Persistence.PreferenceStorage;

public class Server {

    public static final String SERVER_URL = "http://ec2-54-191-243-15.us-west-2.compute.amazonaws.com";
    public static final String SERVER_PORT = "5000";

    private static final String ROUTE_CHECKIN = "/checkin/";
    private static final String ROUTE_STATUSES = "/friends/status/";
    private static final String ROUTE_REQUESTS = "/friends/requests/";
    private static final String ROUTE_ACCEPT_REQUEST = "/friends/accept/";
    private static final String ROUTE_REJECT_REQUEST = "/friends/reject/";

    // Status codes
    public static final int SUCCESS = 200;

    private static final String TAG = "Server";

    public interface ResponseCallback {
        public void execute(InputStream response);
    }

    public static void checkin(Context ctx, ResponseCallback onSuccess) {
        sendRequest(ctx, ROUTE_CHECKIN, null, onSuccess);
    }

    public static void getFriendStatuses(Context ctx, ResponseCallback onSuccess) {
        sendRequest(ctx, ROUTE_STATUSES, null, onSuccess);
    }

    public static void getFriendRequests(Context ctx, ResponseCallback onSuccess) {
        sendRequest(ctx, ROUTE_REQUESTS, null, onSuccess);
    }

    public static void acceptFriendRequest(Context ctx, String friendId, ResponseCallback onSuccess) {
        sendRequest(ctx, ROUTE_ACCEPT_REQUEST, friendId, onSuccess);
    }

    public static void rejectFriendRequest(Context ctx, String friendId, ResponseCallback onSuccess) {
        sendRequest(ctx, ROUTE_REJECT_REQUEST, friendId, onSuccess);
    }

    private static void sendRequest(final Context ctx, final String route, final String genericArg,
                                    final ResponseCallback onSuccess) {
        new AsyncTask<Void, Void, InputStream>() {

            @Override
            protected InputStream doInBackground(Void... params) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = null;
                InputStream responseData = null;

                HttpGet request = new HttpGet(SERVER_URL + ":" + SERVER_PORT + route +
                        PreferenceStorage.getAuthToken(ctx) +
                        (genericArg != null ? "/" + genericArg : ""));

                try {
                    response = httpClient.execute(request);

                    if (response.getStatusLine().getStatusCode() == SUCCESS) {
                        responseData = response.getEntity().getContent();
                    }
                    else {
                        Log.d(TAG, "Error: " + response.getStatusLine().getStatusCode());
                    }
                    response.close();
                    httpClient.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return responseData;
            }

            @Override
            protected void onPostExecute(InputStream response) {
                if (response != null) {
                    onSuccess.execute(response);
                }
            }
        }.execute();
    }
}


