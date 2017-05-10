package hamzei.ehsan;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import hamzei.ehsan.dataset.Fetcher.SolrFetcher;
import hamzei.ehsan.dataset.config.DataSetConfig;
import hamzei.ehsan.dataset.loader.DataLoader;
import hamzei.ehsan.dataset.model.BriefDataModel;
import hamzei.ehsan.dataset.nlp.NLPHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 *
 */
public class App 
{
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private static SolrFetcher solrFetcher = SolrFetcher.getInstance();
    private static NLPHandler nlpHandler = NLPHandler.getInstance();
    static {
        PropertyConfigurator.configure("src/main/resources/log4j.properties");
    }

    public static void main( String[] args ) {
        LOG.info("Starting Application For Analysing the MS BING Query DataSet!");
        LOG.info("Load Location Data to Solr");
        DataLoader.start(); //loading only location dataset to solr

        LOG.info("Analyzing Solr Data");
//        Long sizeOfData = solrFetcher.getDataSize();
//        Long maxPageId;
//        if (sizeOfData % DataSetConfig.PAGINATE_FETCH_SIZE == 0)
//            maxPageId = sizeOfData / DataSetConfig.PAGINATE_FETCH_SIZE;
//        else
//            maxPageId = sizeOfData / DataSetConfig.PAGINATE_FETCH_SIZE + 1;
//
//        for (int i = 0; i < maxPageId; i++) {
//            List<BriefDataModel> fetchData = solrFetcher.getPaginateData(i, DataSetConfig.PAGINATE_FETCH_SIZE);
//            for (BriefDataModel dm : fetchData) {
//                List<String> locations = nlpHandler.getLocationInformation(dm.getQuery());
//                List<String> names = nlpHandler.getNameInformation(dm.getQuery());
//            }
//
//        }

    }

}
