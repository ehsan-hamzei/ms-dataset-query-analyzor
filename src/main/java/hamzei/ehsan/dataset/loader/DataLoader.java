package hamzei.ehsan.dataset.loader;


import com.google.gson.stream.JsonReader;
import hamzei.ehsan.dataset.config.DataSetConfig;
import hamzei.ehsan.dataset.model.BriefDataModel;
import hamzei.ehsan.dataset.model.DataModel;
import hamzei.ehsan.dataset.model.PassageModel;
import hamzei.ehsan.dataset.solr.SolrHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ehsan Hamzei on 5/4/2017.
 * Loading Datasets to Solr
 * It is worth mentioning there are several requirements... (Creating a Core in Solr and correct configuration in DataSetConfig class :))
 * In Solr Core the Structure for the data must be defined...
 * In Solr for "query","query_type", "passages", and "id" the add field must be done appropriately (with indexing, tokenizing and ...)
 *
 */
public class DataLoader {
    private static boolean loadToSolr = true, loadToDB = true;
    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    public static void flush(String[] args) {
        if(args != null && args.length ==1) {
            if(args[0].equals(DataSetConfig.ARG_SOLR))
                flushSolr();
            else if (args[0].equals(DataSetConfig.ARG_DB))
                flushDB();
        } else {
            flushDB();
            flushSolr();
        }
    }

    private static void flushSolr () {
        SolrHandler.flush();
    }

    private static void flushDB () {

    }

    public static void load(String [] args, String path) throws IOException {
        /*args[]: null :: load to solr and db
                  SOLR :: load only to solr
                  DB   :: load only to db
        */
        if(args != null && args.length == 1) {
            LOG.info("LOADING ARG IS :: "+args[0]);
            if(args[0].equals(DataSetConfig.ARG_SOLR))
                loadToDB = false;
            else if (args[0].equals(DataSetConfig.ARG_DB))
                loadToSolr = false;
        } else
            LOG.info("Loading is set to Default (SOLR and DB) Due to the NULL argument or Invalid one");
        LOG.debug("Trying to Read the file based on the provided path");
        JsonReader reader = new JsonReader(new FileReader(path));
        LOG.debug("File address is correct... It must be check if it is in well shape or not!!");
        reader.setLenient(true);
        List<DataModel> dms = new ArrayList<DataModel>();
        int counter = 0;
        try {
            while (reader.hasNext()) {
                try {
                    counter ++;
                    if(counter != 0 && counter% DataSetConfig.BULK_SIZE == 0) {
                        if(loadToSolr) {
                            SolrHandler.insertToSolr(dms, false);
                            LOG.debug("BULK COMMITTING!! and REFRESHING THE BULK LIST");
                            dms = new ArrayList<DataModel>();
                        }
                    }
                    reader.beginObject();
                    DataModel dm = new DataModel();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("query")) {
                            dm.setQuery(reader.nextString());
                        } else if (name.equals("query_type")) {
                            dm.setQueryType(reader.nextString());
                        } else if (name.equals("query_id")) {
                            LOG.debug("Processing ID:" + reader.nextString());
                        } else if (name.equals("passages")) {
                            List<PassageModel> models = new ArrayList<PassageModel>();
                            reader.beginArray();
                            while (reader.hasNext()) {
                                reader.beginObject();
                                PassageModel pm = new PassageModel();
                                while (reader.hasNext()) {
                                    String name2 = reader.nextName();
                                    if (name2.equals("passage_text")) {
                                        pm.setPassageText(reader.nextString());
                                    } else if (name2.equals("passage_url")) {
                                        pm.setPassageUrl(reader.nextString());
                                    } else {
                                        reader.skipValue(); //avoid some unhandle events
                                    }
                                }
                                reader.endObject();
                                models.add(pm);
                            }
                            reader.endArray();
                            dm.setPassages(models);
                        } else {
                            reader.skipValue(); //avoid some unhandle events
                        }
                    }
                    reader.endObject();
                    if(dm.getQueryType().equals("location"))
                        dms.add(dm);
                } catch (IllegalStateException e) {
                    reader.close();
                    if(loadToSolr) {
                        SolrHandler.insertToSolr(dms, true);
                        LOG.info("LAST BULK COMMITTING!! and REFRESHING THE BULK LIST; Our Data is Completely loaded");
                    }
                    break;
                } catch (Exception e) {
                    LOG.error("Error Occured in loading the data! The message is "+e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error Occured in loading the data! The message is "+e.getMessage(), e);
        }
    }

    public static void start() {
        try {
            load(new String[]{DataSetConfig.ARG_SOLR}, DataSetConfig.DEV_DATA_FILE_PATH);
            load(new String[]{DataSetConfig.ARG_SOLR}, DataSetConfig.TEST_DATA_FILE_PATH);
            load(new String[]{DataSetConfig.ARG_SOLR}, DataSetConfig.TRAIN_DATA_FILE_PATH);
        } catch (Throwable t) {
            LOG.error("Error Occured in loading the data! The message is "+t.getMessage(), t);
        }
    }
    public static void main (String[] args) {
        start();
    }
}
