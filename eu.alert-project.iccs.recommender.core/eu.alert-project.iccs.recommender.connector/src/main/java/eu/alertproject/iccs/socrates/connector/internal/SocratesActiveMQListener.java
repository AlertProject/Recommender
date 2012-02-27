package eu.alertproject.iccs.socrates.connector.internal;

import eu.alertproject.iccs.events.api.AbstractActiveMQListener;
import eu.alertproject.iccs.socrates.domain.ArtefactUpdated;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;

/**
 * User: fotis
 * Date: 25/02/12
 * Time: 14:48
 */
public abstract class SocratesActiveMQListener<T extends ArtefactUpdated> extends AbstractActiveMQListener{

    private Logger logger = LoggerFactory.getLogger(SocratesActiveMQListener.class);

    @Override
    public final void process(Message message) throws IOException, JMSException {

        ObjectMapper mapper = new ObjectMapper();
        String text = ((TextMessage) message).getText();
        updateSimilarities(processText(mapper,text));

    }

    abstract void updateSimilarities(T artefactUpdated);
    public abstract T processText(ObjectMapper mapper, String text) throws IOException;

}
