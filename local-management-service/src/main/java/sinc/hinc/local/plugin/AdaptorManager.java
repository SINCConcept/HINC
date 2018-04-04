package sinc.hinc.local.plugin;

import org.slf4j.Logger;
import sinc.hinc.common.utils.HincConfiguration;
import sinc.hinc.communication.factory.MessageClientFactory;
import sinc.hinc.local.LocalManagementService;
import sinc.hinc.local.PropertiesManager;

import java.util.*;

public class AdaptorManager {

    Map<String, Adaptor> adaptors = new HashMap<>();

    public static String DEFAULT_SOURCE_SETTINGS = "./sources.conf";
    static Logger logger = HincConfiguration.getLogger();

    // detect enabled adaptors from source config
    public void init(){
        String enables = PropertiesManager.getParameter("global.enable", DEFAULT_SOURCE_SETTINGS);
        String exchange = PropertiesManager.getParameter("global.exchange", DEFAULT_SOURCE_SETTINGS);
        logger.debug("Enabled adaptors are: " + enables + ". Slit it: " + Arrays.toString(enables.split(",")));
        for (String s : enables.split(",")) {
            String adaptorName = s.trim();
            Adaptor adaptor = new Adaptor();
            adaptor.setName(adaptorName);
            adaptor.setExchange(exchange);
            adaptor.setSettings(PropertiesManager.getSettings(adaptorName, DEFAULT_SOURCE_SETTINGS));

            adaptors.put(adaptorName, adaptor);
            logger.debug("Enabled adaptor: " + s);
        }
    }

    public void scanAll(){
        for(Adaptor adaptor: adaptors.values()){
            adaptor.scanResources();
        }
    }

    public Map<String, Adaptor> getAdaptors() {
        return adaptors;
    }

    public void setAdaptors(Map<String, Adaptor> adaptors) {
        this.adaptors = adaptors;
    }

}