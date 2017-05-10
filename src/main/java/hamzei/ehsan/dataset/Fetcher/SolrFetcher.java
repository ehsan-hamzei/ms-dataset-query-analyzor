package hamzei.ehsan.dataset.Fetcher;

import hamzei.ehsan.dataset.model.BriefDataModel;
import hamzei.ehsan.dataset.solr.SolrHandler;

import java.util.List;

/**
 * Created by Ehsan Hamzei on 5/10/2017.
 */
public class SolrFetcher {
    private static SolrFetcher instance;
    private SolrFetcher () {
        //Singleton Usage :)
    }

    public List<BriefDataModel> getPaginateData (Integer pageId, Integer pageSize) {
        return SolrHandler.getLocationData(pageId, pageSize);
    }

    public Long getDataSize () {
        return SolrHandler.getLocationDataSize();
    }

    public static  SolrFetcher getInstance() {
        return instance = new SolrFetcher();
    }

}