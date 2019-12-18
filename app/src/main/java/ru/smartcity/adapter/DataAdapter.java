package ru.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.smartcity.R;
import ru.smartcity.models.Comment;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.CommentHolder> {

    private LayoutInflater inflater;
    private ArrayList<Comment> comments;

    public DataAdapter(Context context, ArrayList<Comment> comments) {
        this.comments = comments;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_comments, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder commentHolder, int position) {
        Comment comment = comments.get(position);
        commentHolder.bindCrime(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentHolder extends  RecyclerView.ViewHolder {

        private  TextView email;
        private TextView dateTime;
        private TextView text;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            email = (TextView) itemView.findViewById(R.id.listCommets_textEmail);
            dateTime = (TextView) itemView.findViewById(R.id.listCommets_textDateTime);
            text = (TextView) itemView.findViewById(R.id.listCommets_textText);
        }

        public void bindCrime(final Comment comment) {
            email.setText(comment.getEmail());
            dateTime.setText(comment.getDateTime());
            text.setText(comment.getText());
        }
    }
}
