package leap.internal;
import com.leapmotion.leap.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import leap.Leap;

import static javafx.application.Platform.exit;

public class Preprocessing {

    private static Vector AddedVelocity = Vector.zero();

    /*----COLLECTED DYNAMIC DATA FOR PREPROCESSING----*/
    public static Vector AverageVelocity = Vector.zero();

    public static void UpdateDatapoint(Frame FRAME) throws IOException, CsvException, InterruptedException {

        if(FRAME.hands().count() <= 0)
            Leap.FRAME.WarnOutputData("[WARNING]: Capture may be erroneous! Hand lost");

        //ERR HERE
        if(Leap.GESTURE.equals("") || Leap.GESTURE.isBlank() || Leap.GESTURE.isEmpty()){
            Leap.FRAME.WarnOutputData("[ERROR]: Invalid Gesture inputted");
            return;
        }

        Leap.NEXT_TICK_READY = false;
        Leap.TICKS_CURRENT++;

        if(Leap.TICKS_CURRENT == 1) {
            //STATIC - VALUES WE NEED TO COLLECT
            while(!Leap.NEXT_TICK_READY)
                System.out.print(""); //VOID?

            System.out.println("------ Capture Segment Started: 1 tick ------\n");

            AddedVelocity = Vector.zero();
            ArrayList<String> TEMP = new ArrayList<String>();
            for(Float RETURN : Utilities.NormalizeFingerLength(FRAME))
                TEMP.add(RETURN.toString());

            for(Float RETURN: Utilities.NormalizeAdjacentAngles(FRAME))
                TEMP.add(RETURN.toString());

            for(Float RETURN: Utilities.ConsecutiveFingersDistance(FRAME))
                TEMP.add(RETURN.toString());

            TEMP.add(Utilities.PalmCurvatureRadius(FRAME).toString());
            TEMP.add(Leap.GESTURE);

            String[] ReturnValue = TEMP.toArray(new String[TEMP.size()]);
            AppendStatic(ReturnValue);

            Leap.FRAME.SetOutputData(PreviewFeedback(ReturnValue));
        }

        //DYNAMIC - VALUES WE NEED TO COLLECT
        AddedVelocity = AddedVelocity.plus(FRAME.hands().get(0).palmVelocity());
        AverageVelocity = AddedVelocity.divide(Leap.TICKS_CURRENT);

        if(Leap.TICKS_CURRENT == Leap.TICKS_PER_CAPTURE) {
            Leap.THIS_SESSION++;

            String[] ReturnValue = {
                    String.valueOf(AverageVelocity.getX()),
                    String.valueOf(AverageVelocity.getY()),
                    String.valueOf(AverageVelocity.getZ()),
                    Leap.GESTURE };

            AppendDynamic(ReturnValue);
            Leap.FRAME.SetOutputData(PreviewFeedback(ReturnValue), false);
            Leap.FRAME.SetOutputData(ValidateCollection(), false);
            Leap.FRAME.SetOutputData(String.valueOf(Leap.THIS_SESSION), false);
            System.out.printf("------ Capture Segment Terminated: %s tick  ------\n", Leap.TICKS_CURRENT);
            Leap.TICKS_CURRENT = 0;

        }
    }


    public static String[] PreviewFeedback(String[] IN){
        ArrayList<String> RETURN_VALUE = new ArrayList<String>();

        for(int i = IN.length - 1; i >= IN.length-2; i--){
            RETURN_VALUE.add("[PREVIEW INDEX " + i + " ]: " + IN[i]);
        }
        return RETURN_VALUE.toArray(new String[RETURN_VALUE.size()]);
    }


    public static String[] ValidateCollection(){
        ArrayList<String> RETURN_FEEDBACK = new ArrayList<String>();



        RETURN_FEEDBACK.add(Utilities.ReturnLines(String.format("data/%s.csv", Leap.STATIC_FILE)) == Utilities.ReturnLines(String.format("data/%s.csv", Leap.STATIC_FILE))
                ? "[LINES]: EQUIVALENT AT " + Utilities.ReturnLines(String.format("data/%s.csv", Leap.STATIC_FILE)):
                 "[LINES]: DIFFERING AT STATIC: " + Utilities.ReturnLines(String.format("data/%s.csv", Leap.STATIC_FILE)) + " | DYNAMIC: " + Utilities.ReturnLines(String.format("data/%s.csv", Leap.DYNAMIC_FILE)));

        return RETURN_FEEDBACK.toArray(new String[RETURN_FEEDBACK.size()]);

    }


    public static void AppendStatic(String[] Value) throws IOException, CsvException {
        //Called at the beginning of each tick
        System.out.println("      Writing Static Values      ");
        Path DESIRED_PATH = Paths.get(String.format("data/%s.csv", Leap.STATIC_FILE));
        File CREATED_FILE = Files.notExists(DESIRED_PATH) ? new File(String.format("data/%s.csv", Leap.STATIC_FILE)) : null;

        try{
            CSVReader Reader = new CSVReader(new FileReader(String.format("data/%s.csv", Leap.STATIC_FILE)));
            List<String[]> PopulatedElements = Reader.readAll();

            String[] CurrentHeader = (PopulatedElements.size() > 0 ? PopulatedElements.get(0) : null);
            String[] DesiredHeader = {
             /*Finger Lengths*/ "fingerlength_1", "fingerlength_2", "fingerlength_3", "fingerlength_4", "fingerlength_5", //  5 Feature(s)
             /*Paired Angles*/  "fingerangle_1", "fingerangle_2", "fingerangle_3", "fingerangle_4",                       //  4 Feature(s)
             /*Every Distance*/ "fingerdist_12", "fingerdist_13", "fingerdist_14", "fingerdist_15", "fingerdist_23",
                                "fingerdist_24", "fingerdist_25", "fingerdist_34", "fingerdist_35", "fingerdist_45",      // 10 Feature(s)
             /*Palm Curvature*/ "palmradius",                                                                             //  1 Feature(s)

             /*Class: Gesture*/ "gesture"
            };

            if(Value.length != DesiredHeader.length){
                System.out.println("[WARNING]: Missing/misaligned value detected: Omitting datapoint");
                return;
            }

            FileWriter TempWriter = new FileWriter(String.format("data/%s.csv", Leap.STATIC_FILE), false);
            CSVWriter Writer = new CSVWriter(TempWriter);

            if(CurrentHeader == null || CurrentHeader.length == 0)
                Writer.writeNext(DesiredHeader);

            else if (!Arrays.equals(DesiredHeader, CurrentHeader)){
                System.out.println("Changing header from: " + Arrays.toString(CurrentHeader) + " to: " + Arrays.toString(DesiredHeader));
                List<String[]> NoHeader = PopulatedElements.subList(1, PopulatedElements.size());
                Writer.writeNext(DesiredHeader);
                Writer.writeAll(NoHeader);
            }
            else
                Writer.writeAll(PopulatedElements);

            Writer.writeNext(Value);
            Writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("      Finished Writing Static Values      ");
    }


    public static void AppendDynamic(String[] Value){
        //Called at the end of each tick
        System.out.println("      Writing Dynamic Values      ");
        Path DESIRED_PATH = Paths.get(String.format("data/%s.csv", Leap.DYNAMIC_FILE));
        File CREATED_FILE = Files.notExists(DESIRED_PATH) ? new File(String.format("data/%s.csv", Leap.DYNAMIC_FILE)) : null;

        try{
            CSVReader Reader = new CSVReader(new FileReader(String.format("data/%s.csv", Leap.DYNAMIC_FILE)));
            List<String[]> PopulatedElements = Reader.readAll();

            String[] CurrentHeader = (PopulatedElements.size() > 0 ? PopulatedElements.get(0) : null);
            String[] DesiredHeader = {
                    /*Mean Velocity*/ "avgvelocity_x", "avgvelocity_y", "avgvelocity_z",                                  //  3 Feature(s)

                    /*Class: Gesture*/ "gesture"

            };

            if(Value.length != DesiredHeader.length){
                System.out.println("[WARNING]: Missing/misaligned value detected: Omitting datapoint");
                return;
            }

            FileWriter TempWriter = new FileWriter(String.format("data/%s.csv", Leap.DYNAMIC_FILE), false);
            CSVWriter Writer = new CSVWriter(TempWriter);

            if(CurrentHeader == null || CurrentHeader.length == 0)
                Writer.writeNext(DesiredHeader);

            else if (!Arrays.equals(DesiredHeader, CurrentHeader)){
                System.out.println("Changing header from: " + Arrays.toString(CurrentHeader) + " to: " + Arrays.toString(DesiredHeader));
                List<String[]> NoHeader = PopulatedElements.subList(1, PopulatedElements.size());
                Writer.writeNext(DesiredHeader);
                Writer.writeAll(NoHeader);
            }
            else
                Writer.writeAll(PopulatedElements);

            Writer.writeNext(Value);
            Writer.close();

        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
        System.out.println("      Finished Writing Dynamic Values      \n");
    }

}
