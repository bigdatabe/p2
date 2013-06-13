package be.bigdata.workshops.p2.storm;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.likethecolor.alchemy.api.Client;
import com.likethecolor.alchemy.api.call.AbstractCall;
import com.likethecolor.alchemy.api.call.SentimentCall;
import com.likethecolor.alchemy.api.call.type.CallTypeText;
import com.likethecolor.alchemy.api.entity.Response;
import com.likethecolor.alchemy.api.entity.SentimentAlchemyEntity;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: cvhuele
 * Date: 10/06/13
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */
public class SentimentBolt extends BaseBasicBolt {

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        final Client client;
        try {
            client = new Client("/alchemy_key.txt");
            String sentiment = tuple.getValue(1).toString();
            final AbstractCall call =  new SentimentCall(new CallTypeText(sentiment));
            final Response response = client.call(call);
            SentimentAlchemyEntity entity;
            final Iterator<SentimentAlchemyEntity> iter = response.iterator();
            while(iter.hasNext()) {
                entity = iter.next();
                System.out.println("Alchemy: " + entity.getType()+ ", Tweet: " + tuple.getValue(1).toString());
            }

        } catch (IOException e) {
            System.out.println("Error in call?" +tuple.getValue(1).toString() );
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NullPointerException e) {
            System.out.println("Error in call?" +tuple.getValue(1).toString() );
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

}
