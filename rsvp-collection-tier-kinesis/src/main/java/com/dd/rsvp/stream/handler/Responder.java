package com.dd.rsvp.stream.handler;

import com.twitter.clientlib.model.StreamingTweetResponse;

public class Responder implements TweetsStreamListener {

    @Override
    public void actionOnTweetsStream(StreamingTweetResponse streamingTweet) {
        if(streamingTweet == null) {
            System.err.println("Error: actionOnTweetsStream - streamingTweet is null ");
            return;
        }

        if(streamingTweet.getErrors() != null) {
            streamingTweet.getErrors().forEach(System.out::println);
        } else if (streamingTweet.getData() != null) {
            System.out.println("New streaming tweet: " + streamingTweet.getData());
            System.out.println("New streaming tweet: " + streamingTweet.getData().getText());
        }
    }
}
