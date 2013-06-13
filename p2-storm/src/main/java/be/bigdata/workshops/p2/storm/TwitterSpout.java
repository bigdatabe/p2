/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.bigdata.workshops.p2.storm;

import java.util.List;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSpout extends BaseRichSpout {

    private SpoutOutputCollector spoutOutputCollector;
    private Twitter twitter;

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("screenName", "message"));
    }

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.spoutOutputCollector = spoutOutputCollector;
        //twitter = TwitterFactory.getSingleton();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("KTTdtBn4UoTPAP4sSaKBw")
                .setOAuthConsumerSecret("xK0qK1N1P080oxTSDJd1Ikt5L0t51TpWLnObyRgnPY")
                .setOAuthAccessToken("1470611821-r7R12S4A0688sufvsyDyOyKUWciZ1C26QOxQrm2")
                .setOAuthAccessTokenSecret("OCUdnum7UqaF0EbNJ2V5dUsQupHFUxQyIlxl70FR89k");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public void nextTuple() {
        Query query = new Query("redhatsummit");
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException e) {
            throw new IllegalStateException("Twitter is broken", e);
        }
        List<Status> tweets = result.getTweets();
        for (Status status : tweets) {
            spoutOutputCollector.emit(new Values(status.getUser().getScreenName(), status.getText()));
        }
        Utils.sleep(500);
    }

}
