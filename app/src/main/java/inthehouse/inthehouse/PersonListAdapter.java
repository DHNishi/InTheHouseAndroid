package inthehouse.inthehouse;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class PersonListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Person> mFriends;

    public PersonListAdapter(Context context, List<Person> friends) {
        mContext = context;
        mFriends = friends;
    }

    @Override
    public int getCount() {
        return mFriends.size();
    }

    @Override
    public Object getItem(int position) {
        return mFriends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new PersonView(mContext, mFriends.get(position));
        }
        else {
            ((PersonView) convertView).setPerson((Person) getItem(position));
        }
        return convertView;
    }
}
