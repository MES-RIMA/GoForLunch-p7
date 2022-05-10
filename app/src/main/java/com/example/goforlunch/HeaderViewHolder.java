package com.example.goforlunch;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.controller.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HeaderViewHolder extends BaseActivity {

    @BindView(R.id.header_user_image)
    ImageView userImage;
    @BindView(R.id.header_user_name)
    TextView userName;
    @BindView(R.id.header_user_email)
    TextView userMail;

    private Context context;
    private View view;

    /**
     * empty constructor
     */
    public HeaderViewHolder() {
    }

    /**
     * Constructor of the header of menu drawer.
     */
    public HeaderViewHolder(Context context, View view) {
        this.context = context;
        this.view = view;
        ButterKnife.bind(this, view);
    }

    public void updateMainMenuWithUserInfo() {

        if (this.getCurrentUser() != null) {

            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(context)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }

            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String name = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_name_found) : this.getCurrentUser().getDisplayName();

            userMail.setText(email);
            userName.setText(name);
        }
    }
}

