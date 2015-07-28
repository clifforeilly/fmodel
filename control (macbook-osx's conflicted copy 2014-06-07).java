import java.io.*;
import java.util.*;

import Jama.Matrix;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import org.dom4j.*;

/**
 * Created with IntelliJ IDEA.
 * User: cliff
 * Date: 05/07/13
 * Time: 10:24
 * To change this template use File | Settings | File Templates.
 */


/*

variations:

1. corpus
2. contents of label set
3. topic probability limit - initially 0.1
4. Number of labels returned per topic - initially 15
5. Minimum word length (lda - initially >=2)
6. Filter out terms in infrequent documents (lda - initially <4)
7. Filter out most common terms (lda - initially set to 30)
8. Filter out documents with few words (lda - initially set to >=5 terms)
9. Filter labels in infrequent documents (lda - initially set to <10)
10. Number of terms in topics - set to 30 initially
11. Term smoothing parameter (lda - initially 0.01)
12. Topic smoothing parameter (lda - initially 0.01)

*/

public class control
{

static String v="1";
static String ix="7";
static String slash="/";    // for mac
//static String slash="\\"; // for pc
static String homeamd = "G:" + slash + "SharedOne" + slash + "Cliff" + slash + "Dropbox";
static String college = "C:" + slash + "Users" + slash + "coreila" + slash + "Dropbox";
static String macosx = slash + "Users" + slash + "cliff" + slash + "Desktop" + slash + "Dropbox";
static String macwin8 = "C:" + slash + "Users" + slash + "cliff" + slash + "Desktop" + slash + "Dropbox";
static String myLoc = macosx;
static String FileFolder = myLoc + slash + "MSD" + slash + "Tech" + slash + "Iceni" + slash + "Pipes" + slash + "test7";     // for mac
static String InputFileFolder = FileFolder + slash + "in";
static String OutputFileFolder = FileFolder + slash + "out";
static String DatasetsFileFolder = FileFolder + slash + "datasets";
static int LabelsColumn=2;
static int DocColumn=3;
static int numsections=10;
static List trainlabeldistribution;
static List testtopicdistribution;
static List testinputdata;
static List csvinputdata;
static String[][] results;
static String[][] its = new String[3][numsections];
static List itsimport;
static double TopicProbLimit = 0.1;
static int TopLabels = 15;
static int[][] doctopics;
static String[][][] labtopics;
//static String[][][] doclabels;
static List docLabels3;
static int numDocs;
static int numTopics;
static int numLabels;
static List<String> categories;
static Matrix mGoldStandard;
static Matrix mPredictedCategory;
static Matrix pcMatrix;
static Matrix gsMatrix;
static double precision;
static double recall;
static double microF1;
static double macroF1;
static Double[] Results;
static List<Double[]> Stats;
static CSVWriter csvout;
static double TermSmoothing=0.01;
static double TopicSmoothing=0.01;
static int filecount=0;



public static void main(String[] args)
{
    try
    {
        expt1();
        //expt2();
        //expt3(); //TwitterStream
        //ScanFNFullText();

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }

}

public static void ScanFNFullText()
{
    try
    {
        String fnPath = "G:\\Shared_2\\Cliff\\Dropbox\\Dropbox\\MSD\\Tech\\fndata-1.5\\fndata-1.5\\fulltext";
        File f = new File(fnPath);
        File[] matchingFiles = f.listFiles();
        System.out.println("Scanning ... " + InputFileFolder);
        List<String[]> outp = new ArrayList<String[]>();
        int filecount=0;
        int mscount=0;

        CSVWriter csvres = new CSVWriter(new FileWriter(OutputFileFolder + "\\data.csv"));

        for(File tf : matchingFiles)
        {
            filecount++;


            System.out.println("Scanning ... " + tf.getName());
            SAXReader reader = new SAXReader();
            Document d = reader.read(tf.getAbsoluteFile());

            Element root = d.getRootElement();
            for(Iterator i = root.elementIterator(); i.hasNext();)
            {
                Element e2 = (Element) i.next();
                String[] res = new String[5];
                if(e2.getName().equals("sentence"))
                {


                    String frames = "";
                    String sentence="";
                    for(Iterator i2 = e2.elementIterator(); i2.hasNext();)
                    {
                        Element e3 = (Element) i2.next();
                        if(e3.getName().equals("text"))
                        {
                            System.out.println(e3.getStringValue());
                            sentence=e3.getStringValue();
                        }

                        if(e3.getName().equals("annotationSet"))
                        {
                            System.out.println(e3.attributeValue("frameName"));
                            if(e3.attributeValue("frameName")!=null)
                            {
                                frames=frames+ " " + e3.attributeValue("frameName");
                            }
                        }
                    }

                    if(frames.length()>0)
                    {
                        mscount++;
                        res[0]=String.valueOf(mscount);
                        res[1]=String.valueOf(filecount);
                        res[2]=tf.getName();
                        res[3]=sentence;
                        res[4]=frames.trim();
                        outp.add(res);
                        csvres.writeNext(res);
                    }
                }
            }
        }
        csvres.close();

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}

public static void TwitterStream(List<String> lus, int it)
{
    try
    {
        int NumTweets=1000;
        Tweets tweets = new Tweets();

        List<String[]> data = tweets.Stream(NumTweets, lus);
        CSVWriter csvres = new CSVWriter(new FileWriter(OutputFileFolder + "\\expt3-data-" + it + ".csv"));

        for(int i=0 ; i<data.size() ; i++)
        {
            csvres.writeNext(data.get(i));
        }
        csvres.close();

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}

public static void TwitterSearch()
{
    try
    {
        CSVWriter csvres = new CSVWriter(new FileWriter(OutputFileFolder + "\\expt3-data.csv"));
        Tweets tweets = new Tweets();
        int ressize=4;
        int maxres=10;
        int rescount=0;
        int tweetcount=0;
        FNFrame fn = new FNFrame(myLoc, "/");
        List<String[]> data = new ArrayList<String[]>();
        StanCoreNLP parser = new StanCoreNLP("", myLoc, OutputFileFolder, InputFileFolder, "", null, 0, 0);

        for(String lu : fn.getAllLUs())
        {
            if(rescount<maxres)
            {
                System.out.println("lu:" + lu);
                String qs = "#" + lu;
                List<String[]> results = tweets.Search(1, ressize, qs);
                if(results != null)
                {
                    for(int i=0 ; i<results.size() ; i++)
                    {
                        tweetcount++;
                        String[] toutput = new String[7];
                        System.out.println(i + ":" + results.get(i)[0] + " - " + results.get(i)[1] + " - " + results.get(i)[2] + " - " + results.get(i)[3]);
                        toutput[0]=String.valueOf(tweetcount);
                        toutput[1]=lu;
                        toutput[2]=results.get(i)[0];
                        toutput[3]=results.get(i)[1];
                        toutput[4]=results.get(i)[2];
                        toutput[5]=results.get(i)[3];
                        toutput[6]=" ";

                        List<String[]> frames = new ArrayList<String[]>();
                        frames = parser.augment(1,results.get(i)[3].replace(qs,""),4);


                        for(int j=0 ; j<frames.size() ; j++)
                        {
                            String t = frames.get(j)[3].trim();
                            String[] lus = t.split(" ");
                            for(String lux : lus)
                            {
                                String thislu="";
                                if(lux.contains("."))
                                {
                                    thislu =lux.substring(0,lux.indexOf("."));
                                }
                                else
                                {
                                    thislu=lux;
                                }
                                if(thislu.equals(lu))
                                {
                                    toutput[6]=toutput[6] + frames.get(j)[0] + " " + frames.get(j)[1];
                                }
                            }
                            System.out.println(i+":"+j);
                        }

                        data.add(toutput);
                    }
                }
                System.out.println(" ");
            }
            rescount++;
        }

        for(String[] s : data)
        {
            csvres.writeNext(s);
        }
        csvres.close();

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}

public static void expt3()
{
    try
    {
        FNFrame fn = new FNFrame(myLoc, "");
        List<String> lus = fn.getAllLUs();

        int its = 100;
        int c = 0;
        int luc = 120;

        for(int k=0 ; k<its ; k++)
        {
            List<String> luss = new ArrayList<String>();
            for(int j=110 ; j<luc ; j++)
            {
                luss.add(lus.get(c));
                c++;
            }
            TwitterStream(luss, c);
        }
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}

public static void expt2()
{
    try
    {
        List<String[]> data = new ArrayList();
        String[] results = new String[2];

        File f = new File(InputFileFolder);
        File[] matchingFiles = f.listFiles();
        System.out.println("Scanning ... " + InputFileFolder);

        StanCoreNLP parser = new StanCoreNLP("", myLoc, OutputFileFolder, InputFileFolder, "", null, 0, 0);
        CSVWriter csvres = new CSVWriter(new FileWriter(OutputFileFolder + "\\data.csv"));

        for(File tf : matchingFiles)
        {

            CSVReader csvinput = new CSVReader(new FileReader(tf));
            csvinputdata = csvinput.readAll();
            csvinput.close();
            String[] row;
            int count=1;
            List<List> ShardsA;
            List<List> ShardsB;
            List<String[]> ts1;
            List<String[]> ts2;


            for(Object obj : csvinputdata)
            {
                //if(count<3)        // for testing
                //{
                    row=(String[]) obj;

                    ts1=parser.augment(0,row[0],1);
                    ts2=parser.augment(0,row[1],1);

                    if(ts1.size()>0 & ts2.size()>0)
                    {
                        int numSpaces=ts1.size();
                        int numShards=0;
                        if(numSpaces!=0)
                        {
                            numShards=ts1.get(0).length;
                        }

                        String[] temp = new String[6+((numShards+1)*6)];
                        temp[0]=tf.getName();
                        temp[1]=String.valueOf(count);
                        temp[2]=row[0];
                        temp[3]=row[1];
                        temp[4]=row[2];
                        temp[5]=row[3];

                        ShardsA = new ArrayList<List>();

                        for(int s=0 ; s<numShards ; s++)
                        {
                            List<String> tshard = new ArrayList<String>();
                            for(int w=0 ; w<numSpaces ; w++)
                            {
                                String[] tmpSpace=ts1.get(w);

                                String[] t = tmpSpace[s].trim().split(" ");
                                for(String st : t)
                                {
                                    if(!st.equals(" ") & ! st.equals(""))
                                    {
                                        if(!tshard.contains(st.trim()))
                                        {
                                            tshard.add(st.trim());
                                        }
                                    }
                                }

                            }
                            Collections.sort(tshard);
                            ShardsA.add(tshard);
                        }

                        ShardsB = new ArrayList<List>();
                        numSpaces=ts2.size();
                        if(numSpaces!=0)
                        {
                            numShards=ts2.get(0).length;
                        }
                        else
                        {
                            numShards=0;
                        }

                        for(int s=0 ; s<numShards ; s++)
                        {
                            List<String> tshard = new ArrayList<String>();
                            for(int w=0 ; w<numSpaces ; w++)
                            {
                                String[] tmpSpace=ts2.get(w);

                                String[] t = tmpSpace[s].trim().split(" ");
                                for(String st : t)
                                {
                                    if(!st.equals(" ") & ! st.equals(""))
                                    {
                                        if(!tshard.contains(st.trim()))
                                        {
                                            tshard.add(st.trim());
                                        }
                                    }
                                }
                            }
                            Collections.sort(tshard);
                            ShardsB.add(tshard);
                        }


                        //nouns
                        ts1=parser.augment(0,row[0],2);
                        ts2=parser.augment(0,row[1],2);
                        List<String> tnouns1 = new ArrayList<String>();
                        List<String> tnouns2 = new ArrayList<String>();

                        for(int y=0 ; y<ts1.size() ; y++)
                        {
                            if(!tnouns1.contains(ts1.get(y)))
                            {
                                tnouns1.add(ts1.get(y)[0]);
                            }
                        }
                        Collections.sort(tnouns1);
                        ShardsA.add(tnouns1);

                        for(int y=0 ; y<ts2.size() ; y++)
                        {
                            if(!tnouns2.contains(ts2.get(y)))
                            {
                                tnouns2.add(ts2.get(y)[0]);
                            }
                        }

                        Collections.sort(tnouns2);
                        ShardsB.add(tnouns2);


                        //named entities
                        ts1=parser.augment(0,row[0],3);
                        ts2=parser.augment(0,row[1],3);
                        List<String> tnes1 = new ArrayList<String>();
                        List<String> tnes2 = new ArrayList<String>();

                        for(int y=0 ; y<ts1.size() ; y++)
                        {
                            if(!tnes1.contains(ts1.get(y)))
                            {
                                tnes1.add(ts1.get(y)[0]);
                            }
                        }
                        Collections.sort(tnes1);
                        ShardsA.add(tnes1);

                        for(int y=0 ; y<ts2.size() ; y++)
                        {
                            if(!tnes2.contains(ts2.get(y)))
                            {
                                tnes2.add(ts2.get(y)[0]);
                            }
                        }

                        Collections.sort(tnes2);
                        ShardsB.add(tnes2);


                        int latestentry=0;

                        for(int s=0 ; s<numShards+2 ; s++)
                        {
                            temp[6+s*3]=String.valueOf(calcSimilarity(ShardsA.get(s), ShardsB.get(s))[0]);
                            temp[7+s*3]=String.valueOf(calcSimilarity(ShardsA.get(s), ShardsB.get(s))[1]);
                            temp[8+s*3]=String.valueOf(calcSimilarity(ShardsA.get(s), ShardsB.get(s))[2]);
                            latestentry=8+s*3;
                        }

                        int latestentry2=0;

                        for(int s=0 ; s<numShards+2 ; s++)
                        {
                            String ttA="";
                            String ttB="";
                            for(int t=0 ; t<ShardsA.get(s).size() ; t++)
                            {
                                ttA = ttA + " " + ShardsA.get(s).get(t);
                            }
                            for(int t=0 ; t<ShardsB.get(s).size() ; t++)
                            {
                                ttB = ttB + " " + ShardsB.get(s).get(t);
                            }
                            temp[latestentry+1+(s*2)]=ttA;
                            temp[latestentry+2+(s*2)]=ttB;
                            latestentry2=latestentry+2+s;
                        }

                        data.add(temp);
                        csvres.writeNext(temp);
                    }
                    count++;
                    System.out.println("p:" + count);
                }
            }
        //}
        csvres.close();
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}

public static void expt1()
{
    try
    {
        //csvout = new CSVWriter(new FileWriter(InputFileFolder + "\\fn-fulltext-aug.csv"));
        //parseInputFiles(4);
        //csvout.close();

        //createDatasetsFromInputFiles(1);

        //popViterbi();


        String itsfile="";
        if(myLoc.contains("/Users"))
        {
            itsfile=OutputFileFolder + slash + "itsmac.csv";
        }
        else
        {
            itsfile=OutputFileFolder + slash + "its.csv";
        }

        String dataFolder = FileFolder + slash + "ladout" + slash + "c2";

        CSVReader itsdata = new CSVReader(new FileReader("/Users/cliff/Desktop/Dropbox/MSD/Tech/Iceni/Pipes/test7/ldaprep/its.csv"));
        itsimport = itsdata.readAll();
        itsdata.close();
        String[] rowits;
        String[] row;

        Stats = new ArrayList<Double[]>();

        int c=0;
        boolean gotStats = false;

        for(Object ob : itsimport)
        {
            rowits=(String[]) ob;

            if(c==0)
            {
                popCategories(rowits[2], c+1, dataFolder);
                for(int p=5 ; p<15 ; p++)
                {
                    double p2 = p*0.01;
                    popDocTopics(rowits[1], c+1, p2, dataFolder);
                    popLabTopics(rowits[0], c+1, dataFolder);
                    popDocLabels(c+1, false, dataFolder);
                    popGoldStandard(rowits[2], c+1, dataFolder);
                    int NumberOfLabelsPerTopic = 10;

                    for(int n=10 ; n<30 ; n++)
                    {
                        popPredictedCategory(c+1, n, dataFolder);
                        gotStats = getStats(c+1, n, p2);
                    }
                }
            }
            c++;
        }

        CSVWriter csvres = new CSVWriter(new FileWriter(OutputFileFolder + slash + "Results.csv"));
        for(Double[] d : Stats)
        {
            row=new String[7];
            row[0]=String.valueOf(d[0]);
            row[1]=String.valueOf(d[1]);
            row[2]=String.valueOf(d[2]);
            row[3]=String.valueOf(d[3]);
            row[4]=String.valueOf(d[4]);
            row[5]=String.valueOf(d[5]);
            row[6]=String.valueOf(d[6]);
            csvres.writeNext(row);
        }
        csvres.close();




        /*
        CSVWriter csvout = new CSVWriter(new FileWriter("G:\\Shared_2\\Cliff\\MSD\\archive\\v6\\i16\\out\\main-output.csv"));
        filecount=0;
        ReadAndAnalyseBlendingDistros("G:\\Shared_2\\Cliff\\MSD\\archive\\v6\\i16\\out", csvout);
        csvout.close();
        System.out.println("Done.");
        */

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}

public static void ReadAndAnalyseBlendingDistros(String folder, CSVWriter csvout)
{
    try
    {
        File f = new File(folder);
        File[] matchingFiles = f.listFiles();
        //System.out.println("Scanning ... " + folder);
        String[] row;
        String[] row2;
        double[][] bigres;

        for(File tf : matchingFiles)
        {
            if(tf.isDirectory())
            {
                ReadAndAnalyseBlendingDistros(tf.getAbsolutePath(), csvout);
            }
            if(tf.getName().contains("test") & tf.getName().contains("-document-topic-distributions.csv"))
            {
                filecount++;
                System.out.println(filecount + ": " + tf.getAbsolutePath());

                CSVReader csvinput = new CSVReader(new FileReader(tf.getAbsolutePath()));
                csvinputdata = csvinput.readAll();
                csvinput.close();
                bigres=new double[csvinputdata.size()][csvinputdata.size()];
                double tr;
                int rc=0;
                int cc=0;

                for(Object obj : csvinputdata)
                {
                    row=(String[]) obj;

                    for(Object obj2 : csvinputdata)
                    {
                        tr=0.0;
                        row2=(String[]) obj2;
                        for(int i=1 ; i<row2.length ; i++)
                        {
                            tr=tr+Double.parseDouble(row[i])*Double.parseDouble(row2[i]);

                        }
                        bigres[rc][cc]=tr;
                        cc++;
                    }
                    rc++;
                    cc=0;
                }

                int numValues=(csvinputdata.size()*csvinputdata.size())/2;
                String[] output = new String[numValues];
                output[0]=tf.getName();
                int x=1;
                int y=1;

                for(int j=1 ; j<csvinputdata.size() ; j++)
                {
                    for(int k=0 ; k<csvinputdata.size() ; k++)
                    {
                        if(k<x)
                        {
                            output[y]=String.valueOf(bigres[j][k]);
                            y++;
                        }
                    }
                    x++;
                }
                csvout.writeNext(output);
            }
        }

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }

}


public static Double[] calcSimilarity(List<String> a1, List<String> a2)
{
    Double[] ret = new Double[3];
    ret[0]=0.0;

    try
    {
        System.out.println("Sentence one has " + a1.size() + " augmentations.");
        ret[1]=(double) a1.size();
        System.out.println("Sentence two has " + a2.size() + " augmentations.");
        ret[2]=(double) a2.size();

        if(a1.size()>a2.size())
        {
            for(String st : a1)
            {
                if(a2.contains(st))
                {
                    ret[0]++;
                }
            }
        }
        else
        {
            for(String st : a2)
            {
                if(a1.contains(st))
                {
                    ret[0]++;
                }
            }
        }

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
    return ret;
}


public static void parseInputFiles(int type)
{
     try
     {
         String filename=InputFileFolder + "\\plot_summaries.csv";

         if(type==1) // text from wikipedia
         {
             //enwiki-20130708-pages-articles1.xml
             //enwiki-20090902-pages-articles.txt


             String dsfolder=InputFileFolder;
             String input="";

             Wikipedia2Txt wt = new Wikipedia2Txt(filename, dsfolder, csvout);
         }

         if(type==2) //augment csv
         {
             CSVWriter csvaugment = new CSVWriter(new FileWriter(filename +"-augmented.csv"));

             CSVReader csvinput = new CSVReader(new FileReader(filename));
             csvinputdata = csvinput.readAll();
             csvinput.close();
             int c=0;
             String[] row;

             int docCount=0;
             FNFrame fn = new FNFrame(myLoc, "");
             for(Object obj : csvinputdata)
             {
                 row=(String[]) obj;
                 List<String> LUs = new ArrayList();
                 List<String> FEs = new ArrayList();
                 List<String> LU2s = new ArrayList();
                 List<String> FE2s = new ArrayList();
                 List<String> added = new ArrayList();
                 String[] frames = row[5].split(" ");
                 String outLUs="";
                 String outFEs="";
                 String outLU2s="";
                 String outFE2s="";

                 for(String f : frames)
                 {
                     if(f.length()>1)
                     {
                         LUs=fn.getFrameLUs(f);

                         for(String lu : LUs)
                         {
                             if(!added.contains(lu))
                             {
                                outLUs = outLUs + " " + lu;
                                added.add(lu);
                             }
                         }

                         //FEs=fn.getFrameElements(f);

                         //for(String fe : FEs)
                         {
                             //outFEs = outFEs + " " + fe;
                             /*
                             if(fe.length()>1)
                             {
                                 LU2s=fn.getFrameLUs(fn.getFrameFromFrameElement(fe));
                                 for(String lu2 : LU2s)
                                 {
                                     outLU2s = outLU2s + " " + lu2;
                                 }
                                 FE2s=fn.getFrameElements(fn.getFrameFromFrameElement(fe));
                                 for(String fe2 : FE2s)
                                 {
                                     outFE2s = outFE2s + " " + fe2;
                                 }
                             }
                             */
                         }
                     }
                 }

                 String[] outtocsv = new String[10];

                 outtocsv[0]=row[0];
                 outtocsv[1]=row[1];
                 outtocsv[2]=row[2];
                 outtocsv[3]=row[3];
                 outtocsv[4]=row[4];
                 outtocsv[5]=row[5];
                 outtocsv[6]=outLUs;
                 outtocsv[7]=outFEs;
                 outtocsv[8]=outLU2s;
                 outtocsv[9]=outFE2s;

                 csvaugment.writeNext(outtocsv);
                 c++;
                 System.out.println("Wrote a row, count:" + c);
             }

             csvaugment.close();

         }

         if(type==3) //augment csv
         {
             CSVWriter csvaugment = new CSVWriter(new FileWriter(filename +"-augmented.csv"));

             CSVReader csvinput = new CSVReader(new FileReader(filename));
             csvinputdata = csvinput.readAll();
             csvinput.close();
             int c=0;
             String[] row;

             StanCoreNLP parser = new StanCoreNLP("", myLoc, OutputFileFolder, InputFileFolder, "", null, 0, 0);


             for(Object obj : csvinputdata)
             {
                 row=(String[]) obj;
                 String s = row[1];
                 String nes = "";

                 List<String[]> res=parser.augment(999,s,3);

                 String[] outtocsv = new String[3];
                 for(String[] s2 : res)
                 {
                     nes = nes + " " + s2[0];
                 }
                 outtocsv[0]=row[0];
                 outtocsv[1]=row[1] + nes;
                 outtocsv[2]=row[2];

                 csvaugment.writeNext(outtocsv);
                 c++;
                 System.out.println("Wrote a row, count:" + c);
             }
             csvaugment.close();

         }

         if(type==4) //return frames
         {


             File f = new File(InputFileFolder);
             File[] matchingFiles = f.listFiles();
             System.out.println("Scanning ... " + InputFileFolder);

             for(File tf : matchingFiles)
             {
                 System.out.println("Processing file ... " + tf.getName());
                 filename = InputFileFolder + "\\" + tf.getName();
                 CSVWriter csvaugment = new CSVWriter(new FileWriter(filename +"-withframes.csv"));
                 CSVReader csvinput = new CSVReader(new FileReader(filename));
                 csvinputdata = csvinput.readAll();
                 csvinput.close();
                 int c=0;
                 int c2=0;
                 String[] row;

                 StanCoreNLP parser = new StanCoreNLP("", myLoc, OutputFileFolder, InputFileFolder, "", null, 0, 0);

                 for(Object obj : csvinputdata)
                 {
                     row=(String[]) obj;
                     String s = row[2];
                     String nes = "";

                     List<String[]> res=parser.parseAndAddFN(0,s,0);
                     //sentence | frames | nouns | named entities

                     String[] t = new String[8];
                     t[0]=row[0];
                     t[1]=row[1].trim();
                     t[2]=row[2];
                     for(String[] l : res)
                     {
                         c2++;

                         String fner = l[1].trim() + " " + l[3].trim();
                         t[3]=fner;
                         t[4]=l[2].trim();


                         int similarity = 0;

                         String[] origframes = t[1].trim().split(" ");
                         String[] newframes = fner.trim().split(" ");

                         for(String fr : origframes)
                         {
                            for(String fr2 : newframes)
                            {
                                if(fr.equals(fr2))
                                {
                                    similarity++;
                                }
                            }
                         }

                         t[5]=String.valueOf(origframes.length);
                         t[6]=String.valueOf(newframes.length);
                         t[7]=String.valueOf(similarity);

                         csvaugment.writeNext(t);
                         System.out.println("Wrote a row, count:" + c2);
                     }

                     c++;
                     System.out.println(c + " rows processed");
                 }
                 csvaugment.close();
             }
         }


     }
     catch (Exception ex)
     {
         System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
         ex.printStackTrace();
     }
}


public static boolean getStats(int it, int NumberOfLabelsPerTopic, double problim)
{
    try
    {
        Matrix pcCatMatrix;
        Matrix gsCatMatrix;
        double TP; // true positives (correctly predicted)
        double TP_FP; // true postives + false positives (all predicted)
        double TP_FN; // true positives + false negatives (all gold standard)
        double totalTP=0;
        double totalTP_FP=0;
        double totalTP_FN=0;
        double catF1;
        double totalF1=0;
        int catCount=0;
        for (int catIndex=0; catIndex<categories.size(); catIndex++){
            System.out.println(catIndex);
            pcCatMatrix = mPredictedCategory.getMatrix(catIndex, catIndex, 0, mPredictedCategory.getColumnDimension()-1);
            gsCatMatrix = mGoldStandard.getMatrix(catIndex, catIndex, 0, mGoldStandard.getColumnDimension()-1);

            TP = (gsCatMatrix.arrayTimes(pcCatMatrix)).normInf(); // normInf = row total
            TP_FP = (pcCatMatrix.arrayTimes(pcCatMatrix)).normInf();
            TP_FN = (gsCatMatrix.arrayTimes(gsCatMatrix)).normInf();
            //System.out.println(catIndex + " : " + categories.get(catIndex) + " TP: " + TP + " TP_FP: " + TP_FP + " TP_FN: " + TP_FN);

            // maintain counts for micro-averaging
            totalTP+=TP;
            totalTP_FP+=TP_FP;
            totalTP_FN+=TP_FN;

            // calculate F1 for cat
            // F1 = 2TP/(2TP+FP+FN) = 2TP/(TP_FP + TP_FN)
            if (TP_FP!=0 || TP_FN!=0){
                catF1=(2*TP)/(TP_FP + TP_FN);
                // maintain F1 totals
                totalF1+=catF1;
                catCount++;
            }
        }

        // first calculate precision and recall
        precision = totalTP/totalTP_FP;
        recall = totalTP/totalTP_FN;

        // calculate microaverage F1
        microF1=(2*totalTP)/(totalTP_FP + totalTP_FN);

        // calculate macroaverage F1
        macroF1=totalF1/catCount;

        Results = new Double[7];
        Results[0]=Double.parseDouble(String.valueOf(it));
        Results[1]=(double) NumberOfLabelsPerTopic;
        Results[2]=problim;
        Results[3]=precision;
        Results[4]=recall;
        Results[5]=microF1;
        Results[6]=macroF1;

        Stats.add(Results);
        System.out.println("it:" + it + ", Precision:" + precision + ", Recall:" + recall + ", Micro F1:" + microF1 + ", Macro F1:" + macroF1);

        return true;

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
        return false;
    }
}


public static void popViterbi()
{
    try
    {
        String filename=InputFileFolder + "//data.csv";
        CSVReader csvinput = new CSVReader(new FileReader(filename));
        csvinputdata = csvinput.readAll();
        csvinput.close();

        String[] row;
        int rowcount=0;
        int resultcount=0;
        String[] prevrow=null;
        List<String[]> outp = new ArrayList<String[]>();
        CSVWriter csvout = new CSVWriter(new FileWriter(OutputFileFolder + "//vit1.csv"));


        for(Object obj : csvinputdata)
        {
            if(rowcount!=0)
            {
                row=(String[]) obj;

                if(row[2].equals(prevrow[2]))
                {
                    String docname=row[3];
                    String[] frames = row[4].split(" ");
                    String[] prevframes = prevrow[4].split(" ");

                    for(String pframe : prevframes)
                    {
                        for(String frame : frames)
                        {
                            resultcount++;
                            String[] res = new String[6];
                            res[0]=String.valueOf(resultcount);
                            res[1]=row[0];
                            res[2]=row[1];
                            res[3]=row[2];
                            res[4]=pframe;
                            res[5]=frame;
                            outp.add(res);
                            csvout.writeNext(res);
                        }
                    }
                }
            }

            prevrow=(String[]) obj;
            rowcount++;

        }

        csvout.close();

        List<String[]> cnts = new ArrayList<String[]>();
        int t=0;
        boolean ToAdd = false;

        for(String[] r : outp)
        {
            System.out.println(r[1]);
            if(t==0)
            {
                String[] tmpcnt = new String[4];
                tmpcnt[0]=r[2];
                tmpcnt[1]=r[4];
                tmpcnt[2]=r[5];
                tmpcnt[3]="1";
                cnts.add(tmpcnt);
                t++;
            }
            else
            {
                ToAdd=false;
                for(int li=0 ; li<cnts.size() ; li++)
                {
                    String[] r2 = cnts.get(li);

                    if(r[4].equals(r2[1]) & r[5].equals(r2[2]))
                    {
                        String[] tmpcnt = new String[4];
                        tmpcnt[0]=r[2];
                        tmpcnt[1]=r[4];
                        tmpcnt[2]=r[5];

                        int tmpInt=Integer.parseInt(r2[3]);
                        tmpInt++;
                        tmpcnt[3]=String.valueOf(tmpInt);
                        cnts.remove(li);
                        cnts.add(tmpcnt);
                        ToAdd=false;
                    }
                    else
                    {
                        ToAdd=true;
                    }
                }

                if(ToAdd)
                {
                    String[] tmpcnt = new String[4];
                    tmpcnt[0]=r[2];
                    tmpcnt[1]=r[4];
                    tmpcnt[2]=r[5];
                    tmpcnt[3]="1";
                    cnts.add(tmpcnt);
                }
            }
        }

        List<String> states = new ArrayList<String>();
        CSVWriter csvout2 = new CSVWriter(new FileWriter(OutputFileFolder + "//vit2.csv"));
        for(String[] st : cnts)
        {
            csvout2.writeNext(st);

            if(!states.contains(st[1]))
            {
                states.add(st[1]);
            }

        }
        csvout2.close();

        List<String[]> outp2 = new ArrayList<String[]>();

        int cnt=0;
        for(String state : states)
        {
            //get the totals
            cnt=0;
            for(String[] cn : cnts)
            {
                if(state.equals(cn[1]))
                {
                    cnt=cnt+Integer.parseInt(cn[3]);
                }
            }
            String[] o = new String[2];
            o[0]=state;
            o[1]=String.valueOf(cnt);
            outp2.add(o);
        }

        //get per bigram probabilities
        List<String[]> outp3 = new ArrayList<String[]>();
        for(String[] o2 : outp2)
        {
            for(String[] cn : cnts)
            {
                if(o2[0].equals(cn[1]))
                {
                    /*
                    Double tp = Double.parseDouble(o2[1]) / Double.parseDouble(cn[3]);
                    String[] t2 = new String[3];
                    t2[0]=cn[3]
                    t2[1]=
                    t2[2]
                    outp3.add()
                    */
                }
            }
        }

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void popPredictedCategory(int it, int NumberOfLabelsPerTopic, String dataFolder)
{
    try
    {
        System.out.println("Processing predicted category.");
        String[] row;
        mPredictedCategory = new Matrix(categories.size(), numDocs);
        int dcc=0;
        int docLabelsSize=docLabels3.size();
        int start=-1;
        //boolean add=true;

        for(int nd=0 ; nd<docLabelsSize ; nd++)
        {
            int[] taddo = new int[4];
            taddo=(int[]) docLabels3.get(nd);

            if(taddo[0]!=start)
            {
                dcc=0;
                start=taddo[0];
            }

            if(dcc<=NumberOfLabelsPerTopic)
            {
                if(taddo[3]>=0) //check for -1
                {
                    mPredictedCategory.set(taddo[3], taddo[0], 1);
                    dcc++;
                }
            }



            /*
            for(int b=0 ; b<numLabels ; b++)
            {
                //System.out.println("b=" + b);

                int[] taddo = new int[4];
                taddo=(int[]) docLabels3.get(dcc);

                if(taddo[3]>=0) //check for -1
                {
                    mPredictedCategory.set(taddo[3], nd, 1);
                }

            }
            dcc++;

            for(int i=0 ; i<categories.size() ; i++)
            {
                for(int t=0 ; t<doclabels[1].length-1 ; t++)
                {
                    for(int r=0 ; r<TopLabels ; r++)
                    {
                        if(categories.get(i).equals(doclabels[nd][t][r]))
                        {

                        }
                    }
                }
            }
            */
        }

        CSVWriter csvpc = new CSVWriter(new FileWriter(dataFolder + slash + "it"  + it + slash + "r-mPredictedCategory-it" + it + ".csv"));
        for(int i=0 ; i<mPredictedCategory.getRowDimension() ; i++)
        {
            row=new String[mPredictedCategory.getColumnDimension()];
            for(int j=0 ; j<mPredictedCategory.getColumnDimension() ; j++)
            {
                row[j]=String.valueOf(mPredictedCategory.get(i,j));
            }
            csvpc.writeNext(row);
        }
        csvpc.close();


        System.out.println("Predicted Categories populated.");
        System.out.println("");
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void popGoldStandard(String file, int it, String dataFolder)
{
    try
    {
        CSVReader testinput = new CSVReader(new FileReader(file));
        testinputdata = testinput.readAll();
        testinput.close();

        System.out.println("Processing gold standard from ... " + file);
        String[] row;

        mGoldStandard = new Matrix(categories.size(), testinputdata.size());

        System.out.println(testinputdata.size() + " documents.");

        int docCount=0;
        for(Object obj : testinputdata)
        {
            row=(String[]) obj;
            String[] thisLabelSet = row[LabelsColumn-1].trim().split(" ");
            boolean isLabel = false;

            for(String lbl : thisLabelSet)
            {
                for(int i=0 ; i<categories.size() ; i++)
                {
                    if(categories.get(i).equals(lbl))
                    {
                        mGoldStandard.set(i, docCount, 1);
                    }
                }
            }
            docCount++;
        }

        /*
        for(int i=0 ; i<categories.size() ; i++)
        {
            int docCount=0;
            for(Object obj : testinputdata)
            {
                row=(String[]) obj;
                String[] thisLabelSet = row[LabelsColumn-1].trim().split(" ");
                boolean isLabel = false;

                for(String lbl : thisLabelSet)
                {
                    if(categories.get(i).equals(lbl))
                    {
                        isLabel=true;
                    }
                }

                if(isLabel)
                {
                    mGoldStandard.set(i, docCount, 1);
                }

                docCount++;
            }

        }
        */

        CSVWriter csvgs = new CSVWriter(new FileWriter(dataFolder + slash + "it"  + it + slash + "r-mGoldStandard-it" + it + ".csv"));
        for(int i=0 ; i<mGoldStandard.getRowDimension() ; i++)
        {
            row=new String[mGoldStandard.getColumnDimension()];
            for(int j=0 ; j<mGoldStandard.getColumnDimension() ; j++)
            {
               row[j]=String.valueOf(mGoldStandard.get(i,j));
            }
            csvgs.writeNext(row);
        }
        csvgs.close();

        System.out.println("Gold Standard populated.");
        System.out.println("");
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void popCategories(String file, int it, String dataFolder)
{
    try
    {
        CSVReader testinput = new CSVReader(new FileReader(file));
        testinputdata = testinput.readAll();
        testinput.close();

        System.out.println("Processing categories from ... " + file);
        String[] row;
        categories=new ArrayList();

        for(Object obj : testinputdata)
        {
            row=(String[]) obj;
            String[] thisLabelSet = row[LabelsColumn-1].split(" ");

            for(String lbl : thisLabelSet)
            {
                if(lbl.length()>1)
                {
                    if(!categories.contains(lbl))
                    {
                        categories.add(lbl);
                    }
                }
            }
        }

        Collections.sort(categories);

        CSVWriter csvcats = new CSVWriter(new FileWriter(dataFolder + slash + "it"  + it + slash + "r-categories-it" + it + ".csv"));
        for(int i=0 ; i<categories.size() ; i++)
        {
            //System.out.println(categories.get(i));
            row=new String[1];
            row[0]=categories.get(i);
            csvcats.writeNext(row);
        }
        csvcats.close();

        System.out.println(categories.size() + " categories.");
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void popResults(String file, int addon)
{
    try
    {
        CSVReader testinput = new CSVReader(new FileReader(file));
        testinputdata = testinput.readAll();
        testinput.close();

        System.out.println(file);
        String[] row;
        String[] Labels;
        List Liabels = new ArrayList();
        CSVWriter csvres = new CSVWriter(new FileWriter(OutputFileFolder + "\\results-" + addon + ".csv"));

        for(Object obj : testinputdata)
        {
            row=(String[]) obj;
            Labels = row[1].trim().split(" ");

            for(String l : Labels)
            {
                //System.out.println(row[0] + ":" + l);
                Liabels.add(l);
            }

            //System.out.println(row[0] + ": ");
            int g=Integer.parseInt(row[0])-1;

            for(int k=0 ; k<testtopicdistribution.size() ; k++)
            {
                for(int p=0 ; p<testtopicdistribution.size() ; p++)
                {
                    if(doctopics[k][p]>0)
                    {
                        if(k+addon==g)
                        {
                            //System.out.println(k+1 + ":" + doctopics[k][p]);

                            for(int j=0 ; j<labtopics[0].length ; j++)
                            {
                                if(j==doctopics[k][p])
                                {
                                    for(int h=numLabels-1 ; h>numLabels-1-TopLabels ; h--)
                                    {
                                        String tmpRes = labtopics[j][h][0];
                                        String[] oot = new String[6];
                                        if(Liabels.contains(labtopics[j][h][0]))
                                        {
                                            System.out.println(k+1 + "," + doctopics[k][p] + ",A," + tmpRes + ",1," + row[1].trim());
                                            oot[0]=String.valueOf(k+1);
                                            oot[1]=String.valueOf(doctopics[k][p]);
                                            oot[2]="A";
                                            oot[3]=tmpRes;
                                            oot[4]="1";
                                            oot[5]=row[1].trim();
                                        }
                                        else
                                        {
                                            System.out.println(k+1 + "," + doctopics[k][p] + ",A," + tmpRes + ",0," + row[1].trim());
                                            oot[0]=String.valueOf(k+1);
                                            oot[1]=String.valueOf(doctopics[k][p]);
                                            oot[2]="A";
                                            oot[3]=tmpRes;
                                            oot[4]="0";
                                            oot[5]=row[1].trim();
                                        }
                                        csvres.writeNext(oot);

                                    }
                                }
                            }


                        }

                    }
                }
            }


        }
        csvres.close();
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void popDocLabels(int it, boolean output, String dataFolder)
{
    try
    {
        //doclabels = new String[numDocs][numTopics][numLabels];
        //doclabels2 = new int[numDocs][numTopics][numLabels];
        docLabels3 = new ArrayList();

        System.out.println("Dimensioned doclabels array at [" + numDocs + "][" + numTopics + "][" + numLabels + "]");
        CSVWriter csvdl = new CSVWriter(new FileWriter(dataFolder + slash + "it"  + it + slash + "r-doclabels-it" + it + ".csv"));

        for(int k=0 ; k<numDocs ; k++)
        {
            for(int p=0 ; p<numDocs ; p++)
            {
                int acount=0;
                if(doctopics[k][p]!=0)
                {
                    for(int t=labtopics[1].length-1 ; t>-1 ; t--)
                    {
                        int[] taddo = new int[4];
                        String[] staddo = new String[4];
                        //doclabels[k][p][acount]=labtopics[doctopics[k][p]][t][0];
                        //doclabels2[k][p][acount]=categories.indexOf(labtopics[doctopics[k][p]][t][0]);
                        taddo[0]=k;
                        staddo[0]=String.valueOf(k);
                        taddo[1]=p;
                        staddo[1]=String.valueOf(p);
                        taddo[2]=acount;
                        staddo[2]=String.valueOf(acount);
                        taddo[3]=categories.indexOf(labtopics[doctopics[k][p]][t][0]);
                        staddo[3]=String.valueOf(categories.indexOf(labtopics[doctopics[k][p]][t][0]));
                        docLabels3.add(taddo);
                        csvdl.writeNext(staddo);
                        //System.out.println("taddo: " + k + ":" + p + ":" + acount + ":" + categories.indexOf(labtopics[doctopics[k][p]][t][0]));
                           acount++;
                    }
                }
            }
        }
        csvdl.close();

        /*
        if(output)
        {
            CSVWriter csvdl = new CSVWriter(new FileWriter(OutputFileFolder + "\\it" + it + "\\doclabels-it" + it + ".csv"));

            for(int nd=0 ; nd<numDocs ; nd++)
            {
                String[] row = new String[doclabels[2].length+1];
                csvdl.writeNext(row);
                for(int t=0 ; t<doclabels[1].length-1 ; t++)
                {
                    row[0]=String.valueOf(nd);
                    for(int r=0 ; r<doclabels[2].length-1 ; r++)
                    {
                        row[r+1]= doclabels[nd][t][r];
                    }
                    if(row[1]!=null)
                    {
                        csvdl.writeNext(row);
                    }
                }
            }
            csvdl.close();
        }
        */


        System.out.println("doclabels populated.");
        System.out.println("");
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void popLabTopics(String file, int it, String dataFolder)
{
    try
    {
        CSVReader trainingdata = new CSVReader(new FileReader(file));
        trainlabeldistribution = trainingdata.readAll();
        trainingdata.close();

        System.out.println(file);
        String[] row;
        numTopics=0;
        numLabels=0;

        for(Object obj : trainlabeldistribution)
        {
            row=(String[]) obj;
            numTopics=row.length-1;
            if(row[0].length()>0)
            {
                numLabels++;
            }
        }

        System.out.println(numLabels + " training labels.");
        System.out.println(numTopics + " training topics.");

        labtopics = new String[numTopics][numLabels][2];

        for(int tpc=0 ; tpc<numTopics ; tpc++)
        {
            int t=0;
            final String[][] tmpLabs = new String[numLabels][2];

            for(Object obje : trainlabeldistribution)
            {
                row=(String[]) obje;
                if(row[0].length()>0)
                {
                    tmpLabs[t][0]=row[0];
                    tmpLabs[t][1]=row[tpc+1];
                    t++;
                }
            }


            Arrays.sort(tmpLabs, new Comparator<String[]>() {
                @Override
                public int compare(final String[] o1, final String[] o2) {
                    final Double v1 = Double.valueOf(o1[1]);
                    final Double v2 = Double.valueOf(o2[1]);
                    return v1.compareTo(v2);
                }
            });

            t=0;
            for(final String[] s : tmpLabs)
            {
                //System.out.println(s[0] + " : " + s[1]);
                labtopics[tpc][t][0]=s[0];
                labtopics[tpc][t][1]=s[1];
                t++;
            }
        }

        System.out.println("labtopics populated.");

        CSVWriter csvdl = new CSVWriter(new FileWriter(dataFolder + slash + "it"  + it + slash + "r-labtopics-it" + it + ".csv"));

        String[] rowo = new String[numLabels];
        for(int n1=0 ; n1<numTopics ; n1++)
        {
            for(int n2=0 ; n2<numLabels ; n2++)
            {
                rowo[n2]=String.valueOf(labtopics[n1][n2][0]) + "(" + String.valueOf(labtopics[n1][n2][1]) + ")";
            }
            csvdl.writeNext(rowo);
        }
        csvdl.close();

        System.out.println("");
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void popDocTopics(String file, int it, double problim, String dataFolder)
{
   try
   {
       CSVReader testdata = new CSVReader(new FileReader(file));
       testtopicdistribution = testdata.readAll();
       testdata.close();

       System.out.println(file);
       String[] row;
       int t = 0;
       numDocs=testtopicdistribution.size();
       doctopics = new int[numDocs][numDocs];

       for(Object obj : testtopicdistribution)
       {

           row=(String[]) obj;
           double max=0.0;
           int topicnum=-1;
           int topTopics=0;
           List ttopic=new ArrayList();

           do
           {
               max=0.0;
               for(int m=1 ; m<row.length ; m++)
               {
                   if(!ttopic.contains(m))
                   {
                       if(Double.valueOf(row[m])>max)
                       {
                           max=Double.valueOf(row[m]);
                           topicnum=m-1;
                       }
                   }
               }

               if(max>problim)
               {
                   doctopics[t][topTopics]=topicnum;
                   topTopics++;
                   ttopic.add(topicnum+1);
               }

           }while(max>problim);

       t++;
       }

       CSVWriter csvdl = new CSVWriter(new FileWriter(dataFolder + slash + "it" + it + slash + "r-doctopics-it" + it + ".csv"));

       String[] rowo = new String[numDocs];
       for(int n1=0 ; n1<numDocs ; n1++)
       {
           for(int n2=0 ; n2<numDocs ; n2++)
           {
               rowo[n2]=String.valueOf(doctopics[n1][n2]);
           }
           csvdl.writeNext(rowo);
       }
       csvdl.close();


       /*
       for(int k=0 ; k<testtopicdistribution.size() ; k++)
       {
           for(int p=0 ; p<testtopicdistribution.size() ; p++)
           {
               if(doctopics[k][p]>0)
               {
                   System.out.println(k+1 + ":" + doctopics[k][p]);
               }
           }
           System.out.println("");
       }
       */

       System.out.println(numDocs + " docs.");
       System.out.println("doctopics populated.");
       System.out.println("");
   }
   catch (Exception ex)
   {
       System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
       ex.printStackTrace();
   }
}


public static void AnalyseData()
{
    try
    {
        CSVReader itsdata = new CSVReader(new FileReader(OutputFileFolder + "\\its.csv"));
        itsimport = itsdata.readAll();
        itsdata.close();
        String[] rowits;


        for(Object ob : itsimport)
        {
            rowits=(String[]) ob;
            CSVReader trainingdata = new CSVReader(new FileReader(rowits[0]));
            trainlabeldistribution = trainingdata.readAll();
            trainingdata.close();

            CSVReader testdata = new CSVReader(new FileReader(rowits[1]));
            testtopicdistribution = testdata.readAll();
            testdata.close();

            CSVReader testinput = new CSVReader(new FileReader(rowits[2]));
            testinputdata = testinput.readAll();
            testinput.close();

            results = new String[6][testtopicdistribution.size()];
            String[] row;
            String[] row2;
            int t = 0;

            for(Object obj : testtopicdistribution)
            {

                row=(String[]) obj;
                double max = 0.0;
                int topicnum = -1;

                for(int m=1 ; m<row.length ; m++)
                {
                    if(Double.valueOf(row[m])>max)
                    {
                        max=Double.valueOf(row[m]);
                        topicnum=m-1;
                    }
                }


                double maxlabelp=0.0;
                String maxLabel="";
                for(Object obj2 : trainlabeldistribution)
                {
                    row2=(String[]) obj2;
                    if(row2[0].length()>1)
                    {
                        for(int m=1 ; m<row2.length ; m++)
                        {
                            if(m-1==topicnum)
                            {
                                if(Double.valueOf(row2[m])>maxlabelp)
                                {
                                    maxlabelp=Double.valueOf(row2[m]);
                                    maxLabel=row2[0];
                                }
                            }
                        }
                    }
                }

                results[0][t] =  row[0];
                results[1][t] =  String.valueOf(max);
                results[2][t] =  String.valueOf(topicnum);
                results[3][t] =  String.valueOf(maxlabelp);
                results[4][t] =  maxLabel;

                t++;
            }

            for(Object obj : testinputdata)
            {
                row=(String[]) obj;
                for(int q=0 ; q<t ; q++)
                {

                    if(results[2][q].equals("-1"))
                    {
                        results[5][q]="0";
                    }
                    else
                    {
                        if(row[0].equals(results[0][q]))
                        {
                            results[5][q]="0";
                            if(row[1].contains(results[4][q]))
                            {
                                results[5][q]="1";
                            }
                        }
                    }
                }
            }


            //CSVWriter csvout = new CSVWriter(new FileWriter(OutputFileFolder + "\\it" + y + "-stats.csv"));
            for(int r=0 ; r<t ; r++)
            {
                System.out.println(results[0][r] + " :: " + results[1][r] + " :: " + results[2][r] + " :: " + results[3][r] + " :: " + results[4][r] + " :: " + results[5][r]);
                String[] outc = new String[6];
                outc[0]=results[0][r];
                outc[1]=results[1][r];
                outc[2]=results[2][r];
                outc[3]=results[3][r];
                outc[4]=results[4][r];
                outc[5]=results[5][r];
                //csvout.writeNext(outc);
            }
            //csvout.close();
        }
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void createDatasetsFromInputFiles(int type)
{
    String[] results = new String[6];

    try
    {
        File f = new File(InputFileFolder);
        File[] matchingFiles = f.listFiles();
        System.out.println("Scanning ... " + InputFileFolder);
        List inputdata;

        for(File tf : matchingFiles)
        {
            results[0]=tf.getName();
            System.out.println("Processing file ... " + tf.getName());
            String filename = InputFileFolder + "\\" + tf.getName();
            CSVReader csvdata = new CSVReader(new FileReader(filename));
            inputdata = csvdata.readAll();
            csvdata.close();

            if (results[0].equals("t6-outfile.csv"))
            {
                String[] row;
                String previousFrame = "";

                CSVWriter csvtest = new CSVWriter(new FileWriter(DatasetsFileFolder + "\\eg-sentences-test1.csv"));
                CSVWriter csvtrain = new CSVWriter(new FileWriter(DatasetsFileFolder + "\\eg-sentences-train1.csv"));

                for(Object r : inputdata)
                {
                    row = (String[]) r;
                    System.out.println(row[0] + " - " + row[1] + " - " + row[2]);
                    if(!row[1].equals(previousFrame))
                    {
                        System.out.println("break");
                        csvtest.writeNext(row);
                    }
                    else
                    {
                        csvtrain.writeNext(row);
                    }

                    previousFrame=row[1];
                }

                csvtest.close();
                csvtrain.close();

            }



            if (type==1)
            {
                CSVWriter csvtest=null;
                CSVWriter csvtrain=null;

                int rownumheldout = inputdata.size()/10;
                System.out.println(rownumheldout);

                int startpos = 0;
                int endpos = rownumheldout;
                for(int ns=0 ; ns<numsections ; ns++)
                {
                    int thisit=ns+1;
                    String trainfilename=DatasetsFileFolder + "\\train-" + thisit + ".csv";
                    String testfilename=DatasetsFileFolder + "\\test-" + thisit + ".csv";
                    csvtest = new CSVWriter(new FileWriter(testfilename));
                    csvtrain = new CSVWriter(new FileWriter(trainfilename));

                    int count = 0;

                    for(Object r : inputdata)
                    {
                        String[] row;
                        row = (String[]) r;
                        /*
                        row[0]=String.valueOf(count+1);
                        String tempLabels=row[LabelsColumn-2] + " " + row[LabelsColumn];
                        //row[LabelsColumn-1]=row[LabelsColumn-1] + " " + row[LabelsColumn];

                        String[] trow = new String[row.length-2];

                        for(int h=0 ; h<trow.length-2 ; h++)
                        {
                            trow[h]=row[h];
                        }
                        trow[LabelsColumn-2]=tempLabels;

                        */
                        String[] trow = new String[4];
                        trow[0]=String.valueOf(count+1);
                        trow[1]=row[0];
                        trow[2]=row[3].trim();
                        trow[3]=row[4].trim();

                        if(count>=startpos & count<endpos)
                        {
                            csvtest.writeNext(trow);
                        }
                        else
                        {
                            csvtrain.writeNext(trow);
                        }

                        count++;

                    }

                    csvtest.close();
                    csvtrain.close();
                    startpos=startpos+rownumheldout;
                    endpos=endpos+rownumheldout;

                    runLDA(thisit, trainfilename, testfilename);
                    its[0][ns]=OutputFileFolder + "\\it" + thisit + "\\train" + thisit + "-label-topic-distributions.csv";
                    its[1][ns]=OutputFileFolder + "\\it" + thisit + "\\test" + thisit + "-document-topic-distributions.csv";
                    its[2][ns]=testfilename;
                }

            }


            if(type==2)
            {
                String currID = "";
                int count = 0;
                //String trainfilename="";
                CSVWriter csvout=null;
                String filenameforLDA=DatasetsFileFolder + "\\file_dummy.csv";
                csvout = new CSVWriter(new FileWriter(filenameforLDA));

                for(Object r : inputdata)
                {
                    String[] row;
                    row = (String[]) r;
                    String[] trow = new String[4];
                    trow[0]=String.valueOf(count+1);
                    trow[1]=row[0];
                    trow[2]=row[1].trim();
                    trow[3]=row[2].trim() + " " + row[3].trim() + " " + row[4].trim();
                    String perpFileName="";

                    if(!row[0].equals(currID))
                    {
                        csvout.close();

                        if(!filenameforLDA.equals(DatasetsFileFolder + "\\file_dummy.csv"))
                        {
                            if(count>2)
                            {
                                perpFileName=DatasetsFileFolder + "\\file_" + currID + ".csv";
                                runLDA(Integer.parseInt(currID), perpFileName, perpFileName);
                            }
                        }
                        filenameforLDA=DatasetsFileFolder + "\\file_" + row[0] + ".csv";
                        csvout = new CSVWriter(new FileWriter(filenameforLDA));
                        count=0;
                    }

                    csvout.writeNext(trow);
                    count++;
                    currID = row[0];
                }

            }


            CSVWriter csvits = new CSVWriter(new FileWriter(OutputFileFolder + "\\its.csv"));
            for(int y=0 ; y<numsections ; y++)
            {
                String[] tmp = new String[3];
                tmp[0]=its[0][y].replace("\\", "\\\\");
                tmp[1]=its[1][y].replace("\\", "\\\\");
                tmp[2]=its[2][y].replace("\\", "\\\\");
                csvits.writeNext(tmp);
            }
            csvits.close();

        } // for each input file

    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}


public static void runLDA(int it, String trainfilename, String testfilename)
{
    try
    {
        String cmd="";


        //llda train
        cmd="java -jar " + FileFolder + "\\tmt-0.4.0.jar \"" + FileFolder + "\\1-llda-learn.scala" + "\" \"" + trainfilename + "\" \"" + it + "\" \"" + LabelsColumn + "\" \"" + DocColumn + "\" \"" + v + "\" \"" + ix + "\" \"" + myLoc + "\" \"" + TermSmoothing + "\"";
        System.out.println(cmd);
        try
        {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream is = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<llda-learn>");
            while ((line=br.readLine())!=null)
            {
                System.out.println(line);
            }
            System.out.println("</llda-learn>");
            int exitVal = p.waitFor();
            System.out.println("Process exitValue: " + exitVal);

        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }


        //llda infer on training data
        cmd="java -jar " + FileFolder + "\\tmt-0.4.0.jar \"" + FileFolder + "\\2-llda-infer-train.scala" + "\" \"" + trainfilename + "\" \"" + it + "\" \"" + LabelsColumn + "\" \"" + DocColumn + "\" \"" + v + "\" \"" + ix + "\" \"" + myLoc + "\"";
        System.out.println(cmd);
        try
        {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream is = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<llda-infer-1>");
            while ((line=br.readLine())!=null)
            {
                System.out.println(line);
            }
            System.out.println("</llda-infer-1>");
            int exitVal = p.waitFor();
            System.out.println("Process exitValue: " + exitVal);

        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }


        //llda infer on test
        cmd="java -jar " + FileFolder + "\\tmt-0.4.0.jar \"" + FileFolder + "\\2-llda-infer.scala" + "\" \"" + testfilename + "\" \"" + it + "\" \"" + LabelsColumn + "\" \"" + DocColumn + "\" \"" + v + "\" \"" + ix + "\" \"" + myLoc + "\"";
        System.out.println(cmd);
        try
        {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream is = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<llda-infer-2>");
            while ((line=br.readLine())!=null)
            {
                System.out.println(line);
            }
            System.out.println("</llda-infer-2>");
            int exitVal = p.waitFor();
            System.out.println("Process exitValue: " + exitVal);

        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
    catch (Exception ex)
    {
        System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
        ex.printStackTrace();
    }
}

}


