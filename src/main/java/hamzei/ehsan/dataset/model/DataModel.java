package hamzei.ehsan.dataset.model;

import java.util.List;
import java.util.UUID;

/**
 * Created by Ehsan Hamzei on 5/4/2017.
 */
public class DataModel {
    private String query, queryType;
    private List<PassageModel> passages;
    private int queryId;
    private String id;
    public String getQuery() {
        return query;
    }

    public DataModel() {
        this.id = UUID.randomUUID().toString();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public List<PassageModel> getPassages() {
        return passages;
    }

    public void setPassages(List<PassageModel> passages) {
        this.passages = passages;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id: "+id+"\tquery: "+query+"\tquery_type: "+queryType;
    }
}
