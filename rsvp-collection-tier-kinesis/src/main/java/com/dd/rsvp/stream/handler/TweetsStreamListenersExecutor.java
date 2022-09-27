package com.dd.rsvp.stream.handler;

import com.twitter.clientlib.model.StreamingTweetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class TweetsStreamListenersExecutor {

    private final ITweetsQueue tweetsQueue;
    private final List<TweetsStreamListener> listeners = new ArrayList<>();
    private final InputStream stream;
    private volatile boolean isRunning = true;

    @Autowired
    public TweetsStreamListenersExecutor(InputStream stream) {
        this.tweetsQueue = new LinkedListTweetsQueue();
        this.stream = stream;
    }

    public TweetsStreamListenersExecutor(ITweetsQueue tweetsQueue, InputStream stream) {
        this.tweetsQueue = tweetsQueue;
        this.stream = stream;
    }

    public void addListener(TweetsStreamListener toAdd) {
        listeners.add(toAdd);
    }

    public void executeListeners() {
        if (stream == null) {
            System.out.println("Error: stream is null.");
            return;
        } else if (this.tweetsQueue == null) {
            System.out.println("Error: tweetsQueue is null.");
            return;
        }

        TweetsQueuer tweetsQueuer = new TweetsQueuer();
        TweetsListenersExecutor tweetsListenersExecutor = new TweetsListenersExecutor();
        tweetsListenersExecutor.start();
        tweetsQueuer.start();
    }

    public synchronized void shutdown() {
        isRunning = false;
        System.out.println("TweetsStreamListenersExecutor is shutting down.");
    }

    private class TweetsListenersExecutor extends Thread {
        @Override
        public void run() {
            processTweets();
        }

        private void processTweets() {
            StreamingTweetResponse streamingTweet;
            try {
                while (isRunning) {
                    streamingTweet = tweetsQueue.poll();
                    if (streamingTweet == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    for (TweetsStreamListener listener : listeners) {
                        listener.actionOnTweetsStream(streamingTweet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TweetsQueuer extends Thread {
        @Override
        public void run() {
            queueTweets();
        }

        public void queueTweets() {
            String line = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                while (isRunning) {
                    line = reader.readLine();
                    if (line == null || line.isEmpty()) {
                        Thread.sleep(100);
                        continue;
                    }
                    try {
                        tweetsQueue.add(StreamingTweetResponse.fromJson(line));
                    } catch (Exception interException) {
                        interException.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                shutdown();
            }
        }
    }
}

interface ITweetsQueue {
    StreamingTweetResponse poll();

    void add(StreamingTweetResponse streamingTweet);
}


class LinkedListTweetsQueue implements ITweetsQueue {
    private final Queue<StreamingTweetResponse> tweetsQueue = new LinkedList<>();

    @Override
    public StreamingTweetResponse poll() {
        return tweetsQueue.poll();
    }

    @Override
    public void add(StreamingTweetResponse streamingTweet) {
        tweetsQueue.add(streamingTweet);
    }
}
