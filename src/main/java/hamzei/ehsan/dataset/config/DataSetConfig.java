package hamzei.ehsan.dataset.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by Ehsan Hamzei on 5/5/2017.
 */
public class DataSetConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DataSetConfig.class);
    //Solr Parameters
    public static String SOLR_URL;
    public static String SOLR_COLLECTION_NAME;
    //Database Parameters
    public static String DATABASE_URL;
    public static String DATABASE_USER;
    public static String DATABASE_PASS;
    //Bulk Insertion Parameter
    public static Integer BULK_SIZE;

    //Dataset Parameters
    public static String TRAIN_DATA_FILE_PATH;
    public static String TEST_DATA_FILE_PATH;
    public static String DEV_DATA_FILE_PATH;

    //Arguments
    public static String ARG_SOLR;
    public static String ARG_DB;

    //Fetch Parameters
    public static Integer PAGINATE_FETCH_SIZE;

    static {
        try {
            InputStream input = new FileInputStream("src/main/resources/properties.properties");
            Properties properties = new Properties();
            properties.load(input);
            Field[] datasetFields = DataSetConfig.class.getFields();
            for (Field f : datasetFields) {
                Class<?> type = f.getType();
                if (type == String.class) {
                    LOG.debug("String Field :: " + f.getName());
                    f.set(DataSetConfig.class, properties.get(f.getName()));
                } else if (type == Integer.class) {
                    LOG.debug("Integer Field :: " + f.getName());
                    f.set(DataSetConfig.class, Integer.parseInt(properties.get(f.getName()).toString()));
                }
            }
        } catch (IOException e) {
            LOG.error("IOException for Loading the properties file :| the error message is : " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error("Illegal Access Exception for Setting the properties value for DatasetConfing class, the error message is : " + e.getMessage(), e);
        }
    }
}