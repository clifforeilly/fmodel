/**
 * Created with IntelliJ IDEA.
 * User: cliff
 * Date: 10/06/13
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.saar.coli.salsa.reiter.framenet.*;
import de.saar.coli.salsa.reiter.framenet.flatformat.*;
import de.saar.coli.salsa.reiter.framenet.flatformat.Sentence;
import de.saar.coli.salsa.reiter.framenet.fncorpus.*;
import de.saar.coli.salsa.reiter.framenet.salsatigerxml.*;
import de.uniheidelberg.cl.reiter.util.*;
import de.saar.coli.salsa.reiter.framenet.FrameNet;

public class FNFrame
{
    String fnPath;
    FrameNet fn;
    File fnHome;
    DatabaseReader reader;

    public FNFrame(String myLoc, String Slash)
    {
        try
        {
            fnPath = "G:\\ShareOne\\Cliff\\Dropbox\\MSD\\Tech\\fndata-1.5\\fndata-1.5";
            fn = new FrameNet();
            fnHome = new File(fnPath);
            reader = new FNDatabaseReader15(fnHome, true);
            fn.readData(reader);
        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();

        }
    }



    public List<String> getAllLUs()
    {
        List<String> ret = new ArrayList<String>();
        for(LexicalUnit lu : fn.getLexicalUnits())
        {
            ret.add(lu.getLexemeString());
        }
        return ret;
    }

    public List<String[]> getFrames(String lemma, String pos, int outtype)
    {
        List<String[]> outputFrames = new ArrayList<String[]>();
        //System.out.println("Checking " + lemma + ":" + type);

        try
        {
            if(outtype==2)
            {
                for(LexicalUnit lu : fn.getLexicalUnits())
                {
                    String[] mspace = new String[22];
                    mspace[0]=" ";
                    mspace[1]=" ";
                    mspace[2]=" ";
                    mspace[3]=" ";
                    mspace[4]=" ";
                    mspace[5]=" ";
                    mspace[6]=" ";
                    mspace[7]=" ";
                    mspace[8]=" ";
                    mspace[9]=" ";
                    mspace[10]=" ";
                    mspace[11]=" ";
                    mspace[12]=" ";
                    mspace[13]=" ";
                    mspace[14]=" ";
                    mspace[15]=" ";
                    mspace[16]=" ";
                    mspace[17]=" ";
                    mspace[18]=" ";
                    mspace[19]=" ";
                    mspace[20]=" ";
                    mspace[21]=" ";

                    String thisLU=lu.getLexemeString();
                    if(thisLU.equals(lemma))
                    {
                        mspace = new String[22];
                        mspace[0]=lemma;
                        mspace[1]=lu.getFrame().getName();
                        mspace[2]=" ";
                        mspace[3]=" ";
                        mspace[4]=" ";
                        mspace[5]=" ";
                        mspace[6]=" ";
                        mspace[7]=" ";
                        mspace[8]=" ";
                        mspace[9]=" ";
                        mspace[10]=" ";
                        mspace[11]=" ";
                        mspace[12]=" ";
                        mspace[13]=" ";
                        mspace[14]=" ";
                        mspace[15]=" ";
                        mspace[16]=" ";
                        mspace[17]=" ";
                        mspace[18]=" ";
                        mspace[19]=" ";
                        mspace[20]=" ";
                        mspace[21]=" ";

                        //outputFrames.add(lu.getFrame().getName());
                        System.out.println("       frame:" + lu.getFrame().getName());
                        for(String FE : getFrameElements(lu.getFrame().getName()))
                        {
                            //outputFrames.add(FE);
                            mspace[2]=mspace[2] + " " + FE;
                            //System.out.println("           elements:" + FE);
                        }

                        for(String LU : getFrameLUs(lu.getFrame().getName()))
                        {
                            //outputFrames.add(FE);
                            mspace[3]=mspace[3] + " " + LU;
                            //System.out.println("           Lexical Units:" + LU);
                        }


                        for(Frame IdF : lu.getFrame().isInheritedBy())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[4]=mspace[4] + " " + IdF.getName();
                            //System.out.println("           IsInheritedBy:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().perspectivized())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[5]=mspace[5] + " " + IdF.getName();
                            //System.out.println("           PerspectiveOn:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().uses())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[6]=mspace[6] + " " + IdF.getName();
                            //System.out.println("           Uses:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().usedBy())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[7]=mspace[7] + " " + IdF.getName();
                            //System.out.println("           IsUsedBy:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().hasSubframe())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[8]=mspace[8] + " " + IdF.getName();
                            System.out.println("           HasSubframe:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().inchoative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[9]=mspace[9] + " " + IdF.getName();
                            //System.out.println("           Inchoative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().inchoativeStative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[10]=mspace[10] + " " + IdF.getName();
                            //System.out.println("           InchoativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().causative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[11]=mspace[11] + " " + IdF.getName();
                            //System.out.println("           Causative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().causativeStative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[12]=mspace[12] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().allInheritedFrames())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[13]=mspace[13] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().allInheritingFrames())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[14]=mspace[14] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().earlier())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[15]=mspace[15] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().inheritsFrom())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[16]=mspace[16] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().later())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[17]=mspace[17] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().neutral())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[18]=mspace[18] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().referred())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[19]=mspace[19] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().referring())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[20]=mspace[20] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().subframeOf())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[21]=mspace[21] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }
                    }


                    if(!mspace[0].equals(" "))
                    {
                        outputFrames.add(mspace);
                    }
                }
            }

        if(outtype==1)
        {
            for(LexicalUnit lu : fn.getLexicalUnits())
            {
                String[] mspace = new String[22];
                mspace[0]=" ";
                mspace[1]=" ";
                mspace[2]=" ";
                mspace[3]=" ";
                mspace[4]=" ";
                mspace[5]=" ";
                mspace[6]=" ";
                mspace[7]=" ";
                mspace[8]=" ";
                mspace[9]=" ";
                mspace[10]=" ";
                mspace[11]=" ";
                mspace[12]=" ";
                mspace[13]=" ";
                mspace[14]=" ";
                mspace[15]=" ";
                mspace[16]=" ";
                mspace[17]=" ";
                mspace[18]=" ";
                mspace[19]=" ";
                mspace[20]=" ";
                mspace[21]=" ";

                String thisLU=lu.getLexemeString();
                if(thisLU.equals(lemma))
                {
                    String thisLUPOS=lu.getPartOfSpeech().toString();
                    if(thisLUPOS.equals(pos))
                    {
                        mspace = new String[22];
                        mspace[0]=lemma;
                        mspace[1]=lu.getFrame().getName();
                        mspace[2]=" ";
                        mspace[3]=" ";
                        mspace[4]=" ";
                        mspace[5]=" ";
                        mspace[6]=" ";
                        mspace[7]=" ";
                        mspace[8]=" ";
                        mspace[9]=" ";
                        mspace[10]=" ";
                        mspace[11]=" ";
                        mspace[12]=" ";
                        mspace[13]=" ";
                        mspace[14]=" ";
                        mspace[15]=" ";
                        mspace[16]=" ";
                        mspace[17]=" ";
                        mspace[18]=" ";
                        mspace[19]=" ";
                        mspace[20]=" ";
                        mspace[21]=" ";

                        //outputFrames.add(lu.getFrame().getName());
                        System.out.println("       frame:" + lu.getFrame().getName());
                        for(String FE : getFrameElements(lu.getFrame().getName()))
                        {
                            //outputFrames.add(FE);
                            mspace[2]=mspace[2] + " " + FE;
                            //System.out.println("           elements:" + FE);
                        }

                        for(String LU : getFrameLUs(lu.getFrame().getName()))
                        {
                            //outputFrames.add(FE);
                            mspace[3]=mspace[3] + " " + LU;
                            //System.out.println("           Lexical Units:" + LU);
                        }


                        for(Frame IdF : lu.getFrame().isInheritedBy())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[4]=mspace[4] + " " + IdF.getName();
                            //System.out.println("           IsInheritedBy:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().perspectivized())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[5]=mspace[5] + " " + IdF.getName();
                            //System.out.println("           PerspectiveOn:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().uses())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[6]=mspace[6] + " " + IdF.getName();
                            //System.out.println("           Uses:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().usedBy())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[7]=mspace[7] + " " + IdF.getName();
                            //System.out.println("           IsUsedBy:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().hasSubframe())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[8]=mspace[8] + " " + IdF.getName();
                            System.out.println("           HasSubframe:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().inchoative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[9]=mspace[9] + " " + IdF.getName();
                            //System.out.println("           Inchoative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().inchoativeStative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[10]=mspace[10] + " " + IdF.getName();
                            //System.out.println("           InchoativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().causative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[11]=mspace[11] + " " + IdF.getName();
                            //System.out.println("           Causative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().causativeStative())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[12]=mspace[12] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().allInheritedFrames())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[13]=mspace[13] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().allInheritingFrames())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[14]=mspace[14] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().earlier())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[15]=mspace[15] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().inheritsFrom())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[16]=mspace[16] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().later())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[17]=mspace[17] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().neutral())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[18]=mspace[18] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().referred())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[19]=mspace[19] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().referring())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[20]=mspace[20] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                        for(Frame IdF : lu.getFrame().subframeOf())
                        {
                            //outputFrames.add(IdF.getName());
                            mspace[21]=mspace[21] + " " + IdF.getName();
                            //System.out.println("           CausativeStative:" + IdF.getName());
                        }

                    }
                }

                if(!mspace[1].equals(" "))
                {
                    outputFrames.add(mspace);
                }
            }
        }

            if(outtype==3) //get just frames
            {
                for(LexicalUnit lu : fn.getLexicalUnits())
                {
                    String[] mspace = new String[22];
                    mspace[0]=" ";
                    mspace[1]=" ";

                    String thisLU=lu.getLexemeString();
                    if(thisLU.equals(lemma))
                    {
                        String thisLUPOS=lu.getPartOfSpeech().toString();
                        if(thisLUPOS.equals(pos))
                        {
                            mspace = new String[22];
                            mspace[0]=lemma;
                            mspace[1]=lu.getFrame().getName();
                        }
                    }

                    if(!mspace[1].equals(" "))
                    {
                        outputFrames.add(mspace);
                    }
                }
            }

        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();

        }
        return outputFrames;
    }


    public List<String> getFrameLUs(String frame)
    {
        List<String> outputLUs = new ArrayList<String>();
        try
        {
            Frame f = fn.getFrame(frame);
            for(LexicalUnit lu : f.getLexicalUnits())
            {
                outputLUs.add(lu.getName());
            }
        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();

        }
        return outputLUs;
    }

    public List<String> getFrameElements(String frame)
    {
        List<String> outputFrameElements = new ArrayList<String>();
        try
        {
            Frame f = fn.getFrame(frame);
            for(FrameElement fe : f.getFrameElements().values())
            {
                outputFrameElements.add(fe.getName());
            }
        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();

        }
        return outputFrameElements;
    }

    public String getFrameFromFrameElement(String sfe)
    {
        String outputFrames = "";
        try
        {
            outputFrames=fn.getFrameElement(sfe).getFrame().getName();
        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();

        }
        return outputFrames;
    }

    public void getLUtypes()
    {
        try
        {
            List<String> output = new ArrayList<String>();
            for(LexicalUnit lu : fn.getLexicalUnits())
            {
                if(!output.contains(lu.getPartOfSpeech().toString()))
                {
                    output.add(lu.getPartOfSpeech().toString());
                }

                if(lu.getPartOfSpeech().toString().equals("Adjective"))
                {
                    System.out.println("Adjective: " + lu.toString());
                }

                if(lu.getPartOfSpeech().toString().equals("Adverb"))
                {
                    System.out.println("Adverb: " + lu.toString());
                }

                if(lu.getPartOfSpeech().toString().equals("Interjection"))
                {
                    System.out.println("Interjection: " + lu.toString());
                }

                if(lu.getPartOfSpeech().toString().equals("Preposition"))
                {
                    System.out.println("Preposition: " + lu.toString());
                }

                if(lu.getPartOfSpeech().toString().equals("Conjunction"))
                {
                    System.out.println("Conjunction: " + lu.toString());
                }

                if(lu.getPartOfSpeech().toString().equals("Determiner"))
                {
                    System.out.println("Determiner: " + lu.toString());
                }

            }

            for(String lut : output)
            {
                System.out.println(lut);
            }
        }
        catch (Exception ex)
        {
            System.out.println("Error:-" + ex.toString() + ", " + ex.getMessage() + ", " + ex.getLocalizedMessage());
            ex.printStackTrace();

        }
    }
}
