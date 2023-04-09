package com.nikhilosatwal.diceassessment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

public class RepositoryAdapter extends ArrayAdapter<Repository> {
    public RepositoryAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.repository_items, parent, false);
        }
        TextView repoName = view.findViewById(R.id.repo_name);
        TextView repoLang = view.findViewById(R.id.repo_language);
        TextView repoOwner = view.findViewById(R.id.repo_owner);
        TextView repoStar = view.findViewById(R.id.repo_star);
        TextView repoDesc = view.findViewById(R.id.repo_desc);
        ImageView repoAvatar = view.findViewById(R.id.repo_avatar);

        Repository repository = getItem(position);
        repoName.setText(repository.getName());
        repoLang.setText(repository.getLanguage());
        repoOwner.setText(repository.getOwner());
        repoStar.setText(repository.getStarCount());
        repoDesc.setText(repository.getDesc());
        String avatarUrl = repository.getAvatar();
        Glide.with(view)
                .load(avatarUrl)
                .circleCrop()
                .into(repoAvatar);
        /*new LoadAvatarUrl(repoAvatar, getContext()).execute(repository.getAvatar());*/
        return view;
    }
}
