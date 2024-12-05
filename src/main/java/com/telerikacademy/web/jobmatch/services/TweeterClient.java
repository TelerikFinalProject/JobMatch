package com.telerikacademy.web.jobmatch.services;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweeterClient {

    private final Twitter twitter;

    public TweeterClient() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("jJO3mQgBG6LVT5uV6aMF5Nlme")
                .setOAuthConsumerSecret("duXgHCAQVdoQ15IK6hLlgZhx0nXfQ3pe7pYx6ISEJpCOAPFoT3")
                .setOAuthAccessToken("1863998898911522816-ABHUXGZQ18IUrijSuAKGAGnlqrDFTP")
                .setOAuthAccessTokenSecret("P1x8xGgdMc6Zk4aCyeFkIJnVe0Vjp49cq03KGZYo5GaT5");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public void sendTweet(String tweetContent) {
        try {
            StatusUpdate status = new StatusUpdate(tweetContent);
            twitter.updateStatus(status);
            System.out.println("Tweet posted successfully!");
        } catch (TwitterException e) {
            System.err.println("An error occurred while sending the tweet: " + e.getMessage());
        }
    }
}
