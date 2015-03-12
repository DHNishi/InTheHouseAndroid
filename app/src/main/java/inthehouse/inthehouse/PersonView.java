package inthehouse.inthehouse;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PersonView extends LinearLayout {

    private Person mPerson;
    private Context mContext;

    private ImageView mPhotoVw;
    private TextView mNameVw;
    private TextView mLastCheckinVw;
    private ImageView mHomeIndicatorVw;

    public PersonView(Context context, Person person) {
        super(context);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.person_view, this, true);
        mContext = context;

        mPhotoVw = (ImageView) this.findViewById(R.id.userPhoto);
        mNameVw = (TextView) this.findViewById(R.id.userName);
        mLastCheckinVw = (TextView) this.findViewById(R.id.lastCheckin);
        mHomeIndicatorVw = (ImageView) this.findViewById(R.id.homeIndicator);
        setPerson(person);
    }

    public PersonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPerson(Person person) {
        mPerson = person;
        Picasso.with(mContext)
                .load(mPerson.getPictureUrl())
                .placeholder(R.drawable.icon_user_default)
                .into(mPhotoVw);
        mNameVw.setText(mPerson.getName());
        CharSequence checkinFormat = DateUtils.getRelativeDateTimeString(getContext(), mPerson.getLastCheckin().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        mLastCheckinVw.setText(checkinFormat.toString());
        mHomeIndicatorVw.setImageResource(mPerson.isHome() ? R.drawable.house_icon_green
                : R.drawable.house_icon_red);

        this.requestLayout();
    }

    public Person getPerson() {
        return mPerson;
    }
}