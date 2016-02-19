package com.expenses.volodymyr.notecase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.data.volodymyr.notecase.entity.User;
import com.expenses.volodymyr.notecase.R;

import java.util.List;

/**
 * Created by volodymyr on 18.02.16.
 */
public class UserAdapter extends ArrayAdapter<User>{

    public UserAdapter(Context context, List<User> userList) {
        super(context, 0, userList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        TextView userName = (TextView) convertView.findViewById(R.id.user_name_item);
        TextView userEmail = (TextView) convertView.findViewById(R.id.user_email_item);

        userName.setText(user.getName());

        //format double
        userEmail.setText(user.getEmail());

        return convertView;
    }

}
