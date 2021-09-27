package com.zerobracket.codeassignment_cg.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zerobracket.codeassignment_cg.databinding.UserItemLayoutBinding;
import com.zerobracket.codeassignment_cg.interfaces.UserInfoClicked;
import com.zerobracket.codeassignment_cg.model.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<UserInfo> userInfoList;
    private final UserInfoClicked userInfoClicked;

    public UserAdapter(List<UserInfo> userInfoList, UserInfoClicked userInfoClicked) {
        this.userInfoList = userInfoList;
        this.userInfoClicked = userInfoClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        UserItemLayoutBinding itemBinding = UserItemLayoutBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInfo userInfo = userInfoList.get(position);
        holder.onBindView(userInfo,position);
        holder.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInfoClicked.userInfo(userInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        UserItemLayoutBinding binding;

        public ViewHolder(@NonNull UserItemLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(UserInfo userInfo, int position) {
            binding.tvName.setText(userInfo.getUserName());
            //Format the dates in ur desired display mode
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleFormat = new SimpleDateFormat("dd MMM yyyy");
            //Display it by setText
            binding.tvAge.setText(simpleFormat.format(new Date(userInfo.getDob())));
            Picasso.get().load(userInfo.getImageUrl()).into(binding.ivUser);
        }
    }

    public void setItems(List<UserInfo> userInfoItems){
        userInfoList.addAll(userInfoItems);
    }
}
