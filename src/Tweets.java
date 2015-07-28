
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.*;

import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.*;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import twitter4j.json.JSONObjectType;

import javax.json.*;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

public class Tweets
{
    private final Logger logger = Logger.getLogger(Tweets.class.getName());
    private static final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json";

    public Tweets()
    {

    }

    public List<String[]> Search(int type, int ressize, String searchp)
    {
        List<String[]> ret= new ArrayList<String[]>();

        try
        {
            Twitter twitter = new TwitterFactory().getInstance();
            DateFormat f = new SimpleDateFormat();

            Query q = new Query(searchp);
            q.setLang("en");
            QueryResult result = twitter.search(q);
            for(Status tweet : result.getTweets())
            {
                String[] tr = new String[ressize];
                tr[0]=String.valueOf(tweet.getId());
                tr[1]=f.format(tweet.getCreatedAt());
                tr[2]=tweet.getUser().getScreenName();
                tr[3]=tweet.getText();
                ret.add(tr);
            }

        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        return ret;
    }


    public List<String[]> Stream(int NumTweets, List<String> lus)
    {
        List<String[]> ret= new ArrayList<String[]>();

        String searchterms = "";

        for(String s : lus)
        {
            searchterms=searchterms + "," + s;
        }

        try
        {
            int TweetCount = 0;

            OAuthService service = new ServiceBuilder()
                    .provider(TwitterApi.class)
                    .apiKey("UfbSgWdvvtfO7Snqptezg")
                    .apiSecret("UYrHCfDzCvePLtT3A8BPLkUNtyh8vO7o41tjJjhavMY")
                    .build();
            Token accessToken = new Token("76365926-ZDMr3jQdyAzW6ow0GLr256H6CZcNddOp82Jt5wxXG", "lj0ePJsgPxVbGiaP6x5SATStlOwo96j4rJSJ0espMkY");

            DateFormat f = new SimpleDateFormat();
            OAuthRequest request = new OAuthRequest(Verb.POST, STREAM_URI);
            request.addHeader("version", "HTTP/1.1");
            request.addHeader("host", "stream.twitter.com");
            request.setConnectionKeepAlive(true);
            request.addHeader("user-agent", "Twitter Stream Reader");
            request.addBodyParameter("track", searchterms); // Set keywords you'd like to track here
            request.addBodyParameter("language", "en");
            request.addBodyParameter("filter_level", "none");
            service.signRequest(accessToken, request);
            Response response = request.send();

            // Create a reader to read Twitter's stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));
            String line;

            while (TweetCount < NumTweets & (line = reader.readLine()) != null)
            {

                //System.out.println("tweets scanned: " + TweetCount);
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(line);
                JSONObject json = (JSONObject) obj;

                String created = (String) json.get("created_at");
                String id = (String) json.get("id_str");
                String text = (String) json.get("text");
                //String tags = "";

                /*
                if(text != null)
                {
                    if(text.contains("#"))
                    {
                        ContainerFactory containerFactory = new ContainerFactory(){
                            public List creatArrayContainer() {
                                return new LinkedList();
                            }

                            public Map createObjectContainer() {
                                return new LinkedHashMap();
                            }

                        };

                        Map jsonm = (Map)parser.parse(line, containerFactory);
                        Iterator iter = jsonm.entrySet().iterator();
                        //System.out.println("==iterate result==");
                        while(iter.hasNext()){
                            Map.Entry entry = (Map.Entry)iter.next();
                            //System.out.println(entry.getKey() + ":" + entry.getValue());
                            //System.out.println(JSONValue.toJSONString(entry.getValue()));
                            if(entry.getKey().equals("entities"))
                            {
                                Map jsonm2 = (Map)parser.parse(JSONValue.toJSONString(entry.getValue()), containerFactory);
                                Iterator iter2 = jsonm2.entrySet().iterator();
                                //System.out.println("==iterate result==");
                                while(iter2.hasNext()){
                                    Map.Entry entry2 = (Map.Entry)iter2.next();
                                    //System.out.println(entry2.getKey() + ":" + entry2.getValue());
                                    //System.out.println(JSONValue.toJSONString(entry2.getValue()));
                                    if(entry2.getKey().equals("hashtags"))
                                    {
                                        String res=entry2.getValue().toString();
                                        //System.out.println(res);
                                        List<String> hashtags = new ArrayList<String>();
                                        while(res.contains("{text="))
                                        {
                                            hashtags.add(res.substring(res.indexOf("{text=")+6, res.indexOf(", indices",res.indexOf("{text=")+6)));
                                            res=res.substring(res.indexOf("]}")+1, res.length());
                                        }
                                        for(String lu : lus)
                                        {
                                            if(hashtags.contains(lu))
                                            {
                                                System.out.println(text);
                                                for(String s : hashtags)
                                                {
                                                    tags = tags + " " + s;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                */
                //if(tags.length()>0)
                //{
                    String[] output = new String[4];
                    output[0]=created;
                    output[1]=id;
                    output[2]=text;
                    //output[3]=tags;
                    ret.add(output);
                //}
                TweetCount++;
                System.out.println("tweets scanned: " + TweetCount);
            }

        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        return ret;
    }

}
