package be.bigdata.workshops.p2.storm.playground;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class is a
 */
public class StockStreamPlayground {
    private static Logger logger = Logger.getLogger(StockStreamPlayground.class);

    public static void main(String[] args) throws MalformedURLException {
        ArrayList<String> stocks = Lists.newArrayList();
        stocks.add("FB");
        stocks.add("DATA");
        stocks.add("AAPL");

        // http://cliffngan.net/a/13
        String responseFormat = "sl1d1t1cv";

        // "http://finance.yahoo.com/d/quotes.csv?s=DATA&f=snl1d1t1cv&e=.csv"
        String yahooService = "http://finance.yahoo.com/d/quotes.csv?s=%s&f=%s&e=.csv";


        for (int i = 0; i < 10; i++) {

            int idx = new Double(Math.random() * stocks.size()).intValue(); // generate random index
            String stock = stocks.get(idx);

            String query = String.format(yahooService, stock, responseFormat);
            URL url = new URL(query);
            try {
                String response = IOUtils.toString(url.openStream()).replaceAll("\"", "");
                String[] lSplit = response.split(",");
                String stockName = lSplit[0];
                String date = lSplit[2];
                String time = lSplit[3];
                double stockPrice = Double.parseDouble(lSplit[1]);
                System.out.println(String.format("%s - %f - %s %s", stockName, stockPrice, date, time));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }
}
