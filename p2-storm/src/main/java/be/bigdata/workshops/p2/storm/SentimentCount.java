/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.bigdata.workshops.p2.storm;


/**
 *
 * @author laurent
 */
public class SentimentCount {

    private int sum;
    private int count;
    
    public void addSentiment(final int sentiment){
        count++;
        sum+=sentiment;
    }
    
    public double getAverage(){
        return ((double) sum / (double) count);
    }
    
}
