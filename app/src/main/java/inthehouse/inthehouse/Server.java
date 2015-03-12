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
import java.util.HashMap;
import java.util.Map;

import inthehouse.inthehouse.Persistence.PreferenceStorage;

public class Server {

    public static final String SERVER_URL = "http://ec2-54-191-243-15.us-west-2.compute.amazonaws.com";
    public static final String SERVER_PORT = "5000";

    private static final String ROUTE_CHECKIN = "/checkin/";
    private static final String ROUTE_STATUSES = "/friends/status/";
    private static final String ROUTE_REQUESTS = "/friends/requests/";
    private static final String ROUTE_ACCEPT_REQUEST = "/friends/accept/";
    private static final String ROUTE_REJECT_REQUEST = "/friends/reject/";
    private static final String ROUTE_ADD_FRIEND = "/friends/add/";
    private static final String ROUTE_DELETE_FRIEND = "/friends/delete/";

    // Status codes
    public static final int SUCCESS = 200;

    private static final String TAG = "Server";

    public interface ResponseCallback {
        public void execute(InputStream response, int statusCode);
    }

    public static void checkin(Context ctx, ResponseCallback onSuccess, ResponseCallback onError) {
        sendRequest(ctx, ROUTE_CHECKIN, null, onSuccess, onError);
    }

    public static void getFriendStatuses(Context ctx, ResponseCallback onSuccess,
                                         ResponseCallback onError) {
        sendRequest(ctx, ROUTE_STATUSES, null, onSuccess, onError);
    }

    public static void getFriendRequests(Context ctx, ResponseCallback onSuccess,
                                         ResponseCallback onError) {
        sendRequest(ctx, ROUTE_REQUESTS, null, onSuccess, onError);
    }

    public static void acceptFriendRequest(Context ctx, String friendId, ResponseCallback onSuccess,
                                           ResponseCallback onError) {
        sendRequest(ctx, ROUTE_ACCEPT_REQUEST, friendId, onSuccess, onError);
    }

    public static void rejectFriendRequest(Context ctx, String friendId, ResponseCallback onSuccess,
                                           ResponseCallback onError) {
        sendRequest(ctx, ROUTE_REJECT_REQUEST, friendId, onSuccess, onError);
    }

    public static void deleteFriend(Context ctx, String friendId, ResponseCallback onSuccess,
                                           ResponseCallback onError) {
        sendRequest(ctx, ROUTE_DELETE_FRIEND, friendId, onSuccess, onError);
    }

    public static void addFriend(Context ctx, String friendEmail, ResponseCallback onSuccess,
                                 ResponseCallback onError) {
        sendRequest(ctx, ROUTE_ADD_FRIEND, friendEmail, onSuccess, onError);
    }

    private static void sendRequest(final Context ctx, final String route, final String genericArg,
                                    final ResponseCallback onSuccess, final ResponseCallback onError) {
        new AsyncTask<Void, Void, Map>() {

            @Override
            protected Map doInBackground(Void... params) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = null;
                InputStream responseData = null;
                Map<String, Object> data = new HashMap<String, Object>();

                HttpGet request = new HttpGet(SERVER_URL + ":" + SERVER_PORT + route +
                        PreferenceStorage.getAuthToken(ctx) +
                        (genericArg != null ? "/" + genericArg : ""));

                try {
                    response = httpClient.execute(request);

                    data.put("status", response.getStatusLine().getStatusCode());
                    if (response.getStatusLine().getStatusCode() == SUCCESS) {
                        responseData = response.getEntity().getContent();
                    }
                    else {
                        Log.d(TAG, "Error in " + route + ": " + response.getStatusLine().getStatusCode());
                    }
                    response.close();
                    httpClient.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                data.put("data", responseData);
                return data;
            }

            @Override
            protected void onPostExecute(Map response) {
                if (response.get("data") != null && onSuccess != null) {
                    onSuccess.execute((InputStream) response.get("data"), ((Integer) response.get("status")).intValue());
                }
                else if (response.get("data") == null && onError != null) {
                    onError.execute(null, ((Integer) response.get("status")).intValue());
                }
            }
        }.execute();
    }
}


