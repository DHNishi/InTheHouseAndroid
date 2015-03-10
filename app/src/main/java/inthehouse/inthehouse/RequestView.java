package inthehouse.inthehouse;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;

public class RequestView extends LinearLayout {

    private Person mSender;
    private Context mContext;

    private TextView mNameVw;
    private Button mAcceptVw;
    private Button mRejectVw;

    public RequestView(Context context, Person sender) {
        super(context);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.request_view, this, true);
        mContext = context;

        mNameVw = (TextView) this.findViewById(R.id.senderName);
        mAcceptVw = (Button) this.findViewById(R.id.acceptRequest);
        mRejectVw = (Button) this.findViewById(R.id.rejectRequest);
        setSender(sender);

        initListeners();
    }

    public RequestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initListeners() {
        final Server.ResponseCallback removeRequest = new Server.ResponseCallback() {
            @Override
            public void execute(InputStream response) {
                ((FriendRequestsActivity) mContext).removeRequestBySender(mSender);
            }
        };

        mAcceptVw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Server.acceptFriendRequest(mContext, mSender.getGoogleId(), removeRequest);
                removeRequest.execute(null);
            }
        });

        mRejectVw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Server.rejectFriendRequest(mContext, mSender.getGoogleId(), removeRequest);
                removeRequest.execute(null);
            }
        });
    }

    public void setSender(Person person) {
        mSender = person;
        mNameVw.setText(mSender.getName());

        this.requestLayout();
    }
}
