package net.devcats.stepit.Repositories;

import net.devcats.stepit.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostsRepository {

    private PostsRepositoryCallbacks callbacks;

    public void setListener(PostsRepositoryCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void getPosts() {
        List<Post> temp = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Post post = new Post();
            post.setTitle("TITLE " + i);

            temp.add(post);
        }

        callbacks.onPostsReceived(temp);
    }

    public interface PostsRepositoryCallbacks {
        void onPostsReceived(List<Post> posts);
    }

}
