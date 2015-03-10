package inthehouse.inthehouse;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class PersonListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Person> mFriends;

    private Class mViewType;

    public PersonListAdapter(Context context, List<Person> friends, Class viewType) {
        mContext = context;
        mFriends = friends;
        mViewType = viewType;
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
            convertView = mViewType.equals(PersonView.class) ?
                    new PersonView(mContext, mFriends.get(position)) :
                    new RequestView(mContext, mFriends.get(position));
        }
        else {
            if (mViewType.equals(PersonView.class)) {
                ((PersonView) convertView).setPerson(mFriends.get(position));
            }
            else {
                ((RequestView) convertView).setSender(mFriends.get(position));
            }
        }
        return convertView;
    }
}
