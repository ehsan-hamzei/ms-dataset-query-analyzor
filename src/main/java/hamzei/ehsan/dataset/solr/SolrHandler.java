package hamzei.ehsan.dataset.solr;

import hamzei.ehsan.dataset.config.DataSetConfig;
import hamzei.ehsan.dataset.model.BriefDataModel;
import hamzei.ehsan.dataset.model.DataModel;
import hamzei.ehsan.dataset.model.NLPDataModel;
import hamzei.ehsan.dataset.model.PassageModel;
import hamzei.ehsan.dataset.nlp.NLPHandler;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ehsan Hamzei on 5/5/2017.
 */
public class SolrHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SolrHandler.class);
    private static String SOLR_ADDRESS;
    private static SolrClient client;

    private SolrHandler() {};

    static {
        LOG.info("SolrHandler is Starting...");
        SOLR_ADDRESS = DataSetConfig.SOLR_URL+DataSetConfig.SOLR_COLLECTION_NAME;
        LOG.info("Solr Address is : "+SOLR_ADDRESS);
        createConnection();
        LOG.info("SolrHandler is Started :)");
    }
    private static boolean createConnection() {
        try {
            client = new HttpSolrClient.Builder(SOLR_ADDRESS).build();
            return true;
        } catch (Throwable t) {
            LOG.error("Error in creating solr connection, message is : " + t.getMessage());
            return false;
        }
    }

    private static boolean closeConnection() {
        if(client != null) {
            try {
                client.close();
                client = null;
            } catch (Throwable t) {
                LOG.error("Error in stopping solr connection, message is : " + t.getMessage());
                return false;
            }
        }
        return true;
    }

    private static boolean persist(DataModel dm) {
        if(client == null)
            createConnection();
        try {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", dm.getId());
            doc.addField("query", dm.getQuery());
            doc.addField("query_type", dm.getQueryType());
            List<PassageModel> passages = dm.getPassages();
            if(passages.size() > 0) {
                String[] strs = new String[2*passages.size()];
                for(int i=0; i<passages.size(); i++) {
                    strs[2*i] = passages.get(i).getPassageUrl();
                    strs[2*i+1] = passages.get(i).getPassageText();
                }
                doc.addField("passages", strs);
            }
            client.add(doc);
            client.commit();
        } catch (Throwable t) {
            LOG.error("Error in persisting object to Solr, message is : " + t.getMessage());
            return false;
        }
        return true;
    }

    private static boolean flushSolr() {
        if(client == null)
            createConnection();
        try {
            client.deleteByQuery(DataSetConfig.SOLR_COLLECTION_NAME, "*:*");
            client.commit();
        } catch (Throwable t) {
            LOG.error("Error in flushing the Solr, message is : " + t.getMessage());
            return false;
        }
        return true;
    }

    private static boolean persist(List<DataModel> dms) {
        if(client == null)
            createConnection();
        NLPHandler nlpHandler = NLPHandler.getInstance();
        try {
            List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            for(DataModel dm : dms) {
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", dm.getId());
                doc.addField("query", dm.getQuery());
                doc.addField("query_type", dm.getQueryType());
                List<PassageModel> passages = dm.getPassages();
                if (passages.size() > 0) {
                    String[] strs = new String[2 * passages.size()];
                    for (int i = 0; i < passages.size(); i++) {
                        strs[2 * i] = passages.get(i).getPassageUrl();
                        strs[2 * i + 1] = passages.get(i).getPassageText();
                    }
                    doc.addField("passages", strs);
                }
                doc.addField("locations", nlpHandler.getLocationInformation(dm.getQuery()));
                doc.addField("names", nlpHandler.getNameInformation(dm.getQuery()));
                docs.add(doc);
            }
            client.add(docs);
            client.commit();
        } catch (Throwable t) {
            LOG.error("Error in persisting objects to Solr, message is : " + t.getMessage());
            t.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean insertToSolr(DataModel dm, boolean killConnection) {
        try {
            if(client == null)
                createConnection();
            if(dm != null)
                persist(dm);
            if(killConnection)
                closeConnection();
            return true;
        } catch (Throwable t) {
            LOG.error("Error in InsertToSolr, message is : "+ t.getMessage());
            return false;
        }
    }

    public static boolean insertToSolr(List<DataModel> dms, boolean killConnection) {
        try {
            if(client == null)
                createConnection();
            if(dms != null && dms.size() > 0)
                persist(dms);
            if(killConnection)
                closeConnection();
            return true;
        } catch (Throwable t) {
            LOG.error("Error in InsertToSolr (Bulk), message is : "+ t.getMessage());
            return false;
        }
    }

    public static boolean flush() {
        boolean result = flushSolr();
        closeConnection();
        return result;
    }

    public static List<BriefDataModel> getLocationData(int pageId, int pageSize) {
        List<BriefDataModel> data = new ArrayList<BriefDataModel>();
        if (client == null)
            createConnection();
        try {
            SolrQuery query = new SolrQuery("{!term f=query_type}location");
            query.setStart((pageId - 1) * pageSize);
            query.setRows(pageSize);
            query.setFields("id,query,query_type");
            QueryResponse response = client.query(query);
            SolrDocumentList docs = response.getResults();
            for (SolrDocument doc :docs) {
                BriefDataModel dm = new BriefDataModel();
                dm.setId((String) doc.getFieldValue("id"));
                dm.setQuery((String) doc.getFieldValue("query"));
                dm.setQueryType((String) doc.getFieldValue("query_type"));
                data.add(dm);
            }
        } catch (Exception e) {
            LOG.error("Error in Fetching Results, Error Message Is : " + e.getMessage(), e);
        }
        return data;
    }

    public static long getLocationDataSize() {
        if (client == null)
            createConnection();
        try {
            SolrQuery query = new SolrQuery("{!term f=query_type}location");
            return client.query(query).getResults().getNumFound();
        } catch (Exception e) {
            LOG.error("Error in Fetching Result Size, Error Message Is : " + e.getMessage(), e);
        }
        return 0L;
    }
}