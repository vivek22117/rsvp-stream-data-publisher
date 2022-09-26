package com.dd.rsvp.stream.handler;

import com.twitter.clientlib.model.StreamingTweetResponse;

public interface TweetsStreamListener {

    void actionOnTweetsStream(StreamingTweetResponse streamingTweet);
}
