package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.Config;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPage {
    private ZonedDateTime startedAt;
    private BigDecimal startAmount;
    private List<Post> posts = new ArrayList<>();

    public AbstractPage(ZonedDateTime startedAt, BigDecimal startAmount) {
        this.startedAt = startedAt;
        this.startAmount = startAmount;
    }

    /**
     * Checks if the date belongs to the current operation block
     *
     * @param at
     * @return
     */
    public boolean contains(ZonedDateTime at) {
        return this.startedAt.compareTo(at) <= 0;
    }

    public List<Post> addPost(final Post post) {
        posts.add(post);
        return this.posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public BigDecimal getAmountAt(ZonedDateTime at) {
        BigDecimal curAmount = this.startAmount;
        if (!posts.isEmpty()) {
            int res = 0;
            for (int i=0; i<posts.size(); i++) {
                Post curPost = posts.get(i);
                res = at.compareTo(curPost.getPostedAt());
                if (res >= 0) {
                    curAmount = curAmount.add(curPost.getAmount());
                } else {
                    break;
                }
            }
        }
        return curAmount;
    }

    /**
     *
     * @param number
     * @return              - true if the block overflowed
     */
    private boolean checkOverflow(int number) {
        return number > Config.getInstance().getMaxNumPostsInBlock();
    }

    /**
     *
     * @return              - true if the block is full
     */
    private boolean isFull() {
        return !checkOverflow(this.posts.size());
    }

    private boolean hasNext() {
        return !checkOverflow(this.posts.size()+1);
    }

    public BigDecimal getStartAmount() {
        return this.startAmount;
    }

    public ZonedDateTime getStartedAt() {
        return this.startedAt;
    }

}
