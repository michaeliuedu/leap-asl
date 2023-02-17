package gg.bear.modules.language;

import com.clearspring.analytics.util.Pair;
import gg.bear.modules.Leap;
import gg.bear.helper.Logger;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LanguageEngine { //keep reading as long as words are consecutive in at least a certain X value, we garuntee it is wanted, oterwise it is transitional

    public StringBuilder CurrentWord;
    public ArrayList<String> SessionWords;
    private final JLanguageTool GrammarEngine;
    private String LastCharacter;
    public int consecutive = 0;


    public LanguageEngine(){
        CurrentWord = new StringBuilder();
        LastCharacter = new String();
        SessionWords = new ArrayList<>();
        GrammarEngine = new JLanguageTool(new AmericanEnglish());

        Logger.LOG("Successfully loaded language engine");
    }

    public void Compute(String character){
        if(CurrentWord.isEmpty()) Leap.visuals.RemoveSuggestions();

        consecutive = LastCharacter.equals(character) ? consecutive + 1 : 0;
        if(consecutive >= Leap.config.REQUIRED_CONSECUTIVE_DETECTIONS && (CurrentWord.toString().isBlank() || character.charAt(0) != CurrentWord.charAt(CurrentWord.length() - 1)))
            CurrentWord.append(character);

        LastCharacter = character;

        Leap.visuals.TEXT_OUTPUT.setText(Stringify());
    }

    public void FinishWord() throws IOException {
        if(!CurrentWord.toString().isBlank()){
            Logger.WARN("Detected line break: appending word: " + CurrentWord);
            Leap.visuals.WARN(Autocorrect(String.valueOf(CurrentWord)).toString());
            SessionWords.add(String.valueOf(CurrentWord));
            CurrentWord = new StringBuilder();
        }
    }

    public HashMap<Pair<Integer, Integer>, List<String>> Autocorrect(String arg) throws IOException {
        HashMap<Pair<Integer, Integer>, List<String>> RET = new HashMap<Pair<Integer, Integer>, List<String>>();
        for (RuleMatch match : GrammarEngine.check(arg.toLowerCase())){
            RET.put(new Pair(match.getFromPos(), match.getToPos()), match.getSuggestedReplacements());
            for(String sug : match.getSuggestedReplacements())
                Leap.visuals.AppendSuggestion(sug);
        }
        Logger.WARN("[CORRECTIONS]: " + RET);
        return RET;
    }

    public String Stringify(){
        StringBuilder TEMP = new StringBuilder();
        for(String VAR : this.SessionWords) {
            String append = String.format("%s ", VAR);
            TEMP.append(append);
        }

        TEMP.append(CurrentWord);
        return TEMP.toString();
    }

}
