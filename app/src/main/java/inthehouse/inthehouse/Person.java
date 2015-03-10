package inthehouse.inthehouse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * The model for a user.
 */
public class Person {

    private String mName;
    private String mGoogleId;
    private String mPictureUrl;
    private Timestamp mLastCheckin;
    private ArrayList<Person> mFriends;

    // # of milliseconds since last checkin we use to decide if the user is still home or not.
    private static final long PERIOD_BEFORE_NOT_HOME = TimeUnit.HOURS.toMillis(1);

    public Person(String name, String googleId, String pictureUrl, Timestamp lastCheckin,
                  ArrayList<Person> friends) {
        this.mName = name;
        this.mGoogleId = googleId;
        this.mPictureUrl = pictureUrl;
        this.mLastCheckin = lastCheckin;

        if (friends != null) {
            this.mFriends = friends;
        }
        else {
            this.mFriends = new ArrayList<Person>();
        }
    }

    public void addFriend(Person friend) {
        if (friend != null && mFriends.contains(friend) == false) {
            mFriends.add(friend);
        }
    }

    public void deleteFriend(Person friend) {
        if (friend != null) {
            mFriends.remove(friend);
        }
    }

    public void checkin() {
        this.mLastCheckin = new Timestamp(System.currentTimeMillis());
    }

    public boolean isHome() {
        return System.currentTimeMillis() - mLastCheckin.getTime() < PERIOD_BEFORE_NOT_HOME;
    }

    public String getName() {
        return mName;
    }

    public String getGoogleId() {
        return mGoogleId;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public Timestamp getLastCheckin() {
        return mLastCheckin;
    }

    public ArrayList<Person> getFriends() {
        return mFriends;
    }

}
