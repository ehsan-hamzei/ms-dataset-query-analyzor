package hamzei.ehsan.dataset.nlp;

import edu.stanford.nlp.ie.KBPRelationExtractor;
import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ehsan Hamzei on 5/10/2017.
 */
public class NLPHandler {
    private static NLPHandler instance;

    private NLPHandler() {

    }

    public List<String> getLocationInformation(String query) {
        List<String> locations = new ArrayList<String>();
        Sentence sentence = new Sentence(query);
        List<String> words = sentence.words();
        List<String> nerTags = sentence.nerTags();
        for (int i = 0; i < nerTags.size(); i++)
            if (nerTags.get(i).equals(KBPRelationExtractor.NERTag.LOCATION.name))
                locations.add(words.get(i));
        return locations;
    }

    public List<String> getNameInformation(String query) {
        List<String> names = new ArrayList<String>();
        Sentence sentence = new Sentence(query);
        List<String> words = sentence.words();
        List<String> pos = sentence.posTags();
        for (int i = 0; i < pos.size(); i++)
            if (pos.get(i).startsWith("N"))
                names.add(words.get(i));
        return names;
    }

    public static NLPHandler getInstance() {
        if (instance == null)
            instance = new NLPHandler();
        return instance;
    }

    public static void main(String[] args) {
        NLPHandler handler = NLPHandler.getInstance();
        System.out.println(handler.getLocationInformation("where is slovakia"));
        System.out.println(handler.getNameInformation("where is slovakia"));
    }
}