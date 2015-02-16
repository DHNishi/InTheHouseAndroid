package inthehouse.inthehouse;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * The model for an user.
 */
public class Person {

    private String mName;
    private String mGoogleId;
    private String mPictureUrl;
    private Timestamp mLastCheckin;
    private String mHomeMac;
    private ArrayList<Person> mFriends;
    private boolean mIsIncognito;

    // # of milliseconds since last checkin we use to decide if the user is still home or not.
    private static final int PERIOD_BEFORE_NOT_HOME = 3600000;

    // Kinda gross but it makes sharing/syncing between the checkin service and activities nice
    // TODO: This needs to be set upon successful login
    private static Person mCurrentUser = new Person("Bill", "fds", "dsf",
            new Timestamp(System.currentTimeMillis() - PERIOD_BEFORE_NOT_HOME),
            "aa:aa:aa:aa:aa:aa", null);

    public Person(String name, String googleId, String pictureUrl, Timestamp lastCheckin,
                  String homeMac, ArrayList<Person> friends) {
        this.mName = name;
        this.mGoogleId = googleId;
        this.mPictureUrl = pictureUrl;
        this.mLastCheckin = lastCheckin;
        this.mHomeMac = homeMac;

        if (friends != null) {
            this.mFriends = friends;
        }
        else {
            this.mFriends = new ArrayList<Person>();
        }
        this.mIsIncognito = false;
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

    public void setName(String name) {
        this.mName = name;
    }

    public String getGoogleId() {
        return mGoogleId;
    }

    public void setGoogleId(String googleId) {
        this.mGoogleId = googleId;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.mPictureUrl = pictureUrl;
    }

    public Timestamp getLastCheckin() {
        return mLastCheckin;
    }

    public String getHomeMac() {
        return mHomeMac;
    }

    public void setHomeMac(String homeMac) {
        this.mHomeMac = homeMac;
    }

    public ArrayList<Person> getFriends() {
        return mFriends;
    }

    public static Person getCurrentUser() {
        return mCurrentUser;
    }

    public static void setCurrentUser(Person user) {
        mCurrentUser = user;
    }

    public boolean isIncognito() {
        return mIsIncognito;
    }

    public void toggleIsIncognito() {
        mIsIncognito = !mIsIncognito;
    }
}
