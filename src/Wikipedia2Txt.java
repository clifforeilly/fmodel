
import java.io.*;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.WikiModel;

import org.xml.sax.SAXException;

public class Wikipedia2Txt {

    static String datafolder;
    static BufferedWriter br;
    static CSVWriter csvout;
    static int pageCount;
    static int lineCount;

    /**
     //* @param args
     * @throws SAXException
     * @throws IOException
     * @throws SAXException
     //* @throws ParserConfigurationException
     */
    public Wikipedia2Txt(String filename, String dsfolder, CSVWriter csvouti) throws IOException, SAXException
    {
        csvout=csvouti;
        datafolder=dsfolder;
        pageCount=0;
        lineCount=0;
        IArticleFilter handler = new ArticleFilter();
        WikiXMLParser wxp = new WikiXMLParser(filename, handler);
        wxp.parse();

    }

    /**
     * Print title an content of all the wiki pages in the dump.
     *
     */
    static class ArticleFilter implements IArticleFilter {

        //final static Pattern regex = Pattern.compile("[A-Z][\\p{L}\\w\\p{Blank},\\\"\\';\\[\\]\\(\\)-]+[\\.!]",Pattern.CANON_EQ);

        // Convert to plain text
        WikiModel wikiModel = new WikiModel("${image}", "${title}");

        public void process(WikiArticle page, Siteinfo siteinfo) throws SAXException {

            if (page != null && page.getText() != null && !page.getText().startsWith("#REDIRECT ")){

                /*
                PrintStream out = null;

                try {
                    out = new PrintStream(System.out, true, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                  */

                // Zap headings ==some text== or ===some text===

                // <ref>{{Cite web|url=http://tmh.floonet.net/articles/falseprinciple.html |title="The False Principle of our Education" by Max Stirner |publisher=Tmh.floonet.net |date= |accessdate=2010-09-20}}</ref>
                // <ref>Christopher Gray, ''Leaving the Twentieth Century'', p. 88.</ref>
                // <ref>Sochen, June. 1972. ''The New Woman: Feminism in Greenwich Village 1910Ð1920.'' New York: Quadrangle.</ref>

                // String refexp = "[A-Za-z0-9+\\s\\{\\}:_=''|\\.\\w#\"\\(\\)\\[\\]/,?&%Ð-]+";

                String wikiText = page.getText().
                        replaceAll("[=]+[A-Za-z+\\s-]+[=]+", " ").
                        replaceAll("\\{\\{[A-Za-z0-9+\\s-]+\\}\\}"," ").
                        replaceAll("(?m)<ref>.+</ref>"," ").
                        replaceAll("(?m)<ref name=\"[A-Za-z0-9\\s-]+\">.+</ref>"," ").
                        replaceAll("<ref>"," <ref>");

                // Remove text inside {{ }}
                String plainStr = wikiModel.render(new PlainTextConverter(), wikiText).
                        replaceAll("\\{\\{[A-Za-z+\\s-]+\\}\\}"," ");

                try
                {
                    br = new BufferedWriter(new FileWriter(datafolder + "\\" + page.getTitle() + ".txt"));
                    br.write(page.getTitle() + "\r\n");
                }
                catch(Exception ex)
                {
                    System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }

                System.out.println(page.getTitle());
                //Matcher regexMatcher = regex.matcher(plainStr);

                try
                {
                    pageCount++;
                    StanCoreNLP parser = new StanCoreNLP(plainStr, " ", datafolder, page.getTitle(), "\\", csvout, pageCount, lineCount);
                    lineCount = parser.run();
                    br.write(plainStr + "\r\n");
                    br.close();
                }
                catch(Exception ex)
                {
                    System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
                /*
                while (regexMatcher.find())
                {
                    // Get sentences with 6 or more words
                    String sentence = regexMatcher.group();

                    if (matchSpaces(sentence, 5)) {
                        //out.println(sentence);
                        try
                        {
                            br.write(plainStr + "\r\n");
                        }
                        catch(Exception ex)
                        {
                            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                        out.println(plainStr);
                    }
                } */

            }
        }

        private boolean matchSpaces(String sentence, int matches) {

            int c =0;
            for (int i=0; i< sentence.length(); i++) {
                if (sentence.charAt(i) == ' ') c++;
                if (c == matches) return true;
            }
            return false;
        }

    }

}
