package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;

import java.util.List;

/**
 * Created by ramesh on 17/11/17.
 */

public class CommentsListAdapter extends ArrayAdapter{

    Context context;
    int resource;
    List<CommentThread> commentThreadsList;

    public CommentsListAdapter(@NonNull Context context, @LayoutRes int resource, List<CommentThread> commentThreadsList) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.commentThreadsList = commentThreadsList;
    }

    @Override
    public int getCount() {
        return commentThreadsList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        CommentThread currentComment = commentThreadsList.get(position);

        TextView textViewComment = (TextView) convertView.findViewById(R.id.textViewComment);
        TextView textViewCommentPublishedAt = (TextView) convertView.findViewById(R.id.textViewCommentPublishedAt);
        TextView textViewAuthorName = (TextView) convertView.findViewById(R.id.textViewAuthorName);

        CommentSnippet commentSnippet = currentComment.getSnippet().getTopLevelComment().getSnippet();
        textViewComment.setText(commentSnippet.getTextOriginal());
        textViewCommentPublishedAt.setText(Helper.getReadableDateString(commentSnippet.getPublishedAt()));
        textViewAuthorName.setText(commentSnippet.getAuthorDisplayName());

        return convertView;
    }
}
