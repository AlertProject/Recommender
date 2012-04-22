package eu.alertproject.iccs.socrates.datastore.internal;

import eu.alertproject.iccs.socrates.datastore.api.*;
import eu.alertproject.iccs.socrates.domain.*;
import org.apache.commons.collections15.map.FastHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * User: fotis
 * Date: 21/04/12
 * Time: 23:28
 */

@Service("datastoreRecommendationService")
public class JpaDatastoreRecommendationService implements DatastoreRecommendationService{

    private Logger logger = LoggerFactory.getLogger(JpaDatastoreRecommendationService.class);

    @Autowired
    UuidIssueDao uuidIssueDao;
    @Autowired
    UuidSubjectDao uuidSubjectDao;
    @Autowired
    IssueSubjectDao issueSubjectDao;
    @Autowired
    UuidClassDao uuidClassDao;

    private NavigableSet<Double> descRecsKeySet;


    @Override
    public List<IdentityBean> findByForClass(
            String classification,
            Integer issueId,
            double threshold,
            double similarityWeight,
            double rankingWeight,
            int maxRecommendations) {


        //TODO: @Fotis We need to sort by similarity! ?
        List<UuidIssue> uuidIssues = uuidIssueDao.findByIssueId(issueId,threshold);
        List<IdentityBean> recs = new ArrayList<IdentityBean>();
        TreeMap<Double, IdentityBean> recsFull = new TreeMap<Double, IdentityBean>();
        Double finalSimilarity = 0.0;

        Double maxWeight = uuidClassDao.getMaxWeight(classification);

        long start = System.currentTimeMillis();
        logger.trace("List<IdentityBean> findByForClass() About to look for {} issues ",uuidIssues.size());
        for (UuidIssue ui : uuidIssues) {

            UuidClass byUuidAndClass = uuidClassDao.findByUuidAndClass(ui.getUuid(), classification);

            if(byUuidAndClass == null){
                continue;
            }

            double ranking = byUuidAndClass.getWeight()/maxWeight;


            finalSimilarity = ((ui.getSimilarity() * similarityWeight) + (ranking * rankingWeight)) / (similarityWeight + rankingWeight);
            //TODO: We need to retrieve the name and surname of the developer from STARDOM
            recsFull.put(finalSimilarity, new IdentityBean(ui.getUuid(), "name", "surname", ui.getSimilarity(), ranking));
        }

        logger.trace("List<IdentityBean> findByForClass() The process too {} ",System.currentTimeMillis()-start);

        Set<Double> descRecsKeySet = recsFull.descendingKeySet();
        Iterator keySetIterator = descRecsKeySet.iterator();
        Integer counter = 0;
        while (keySetIterator.hasNext()) {
            recs.add(recsFull.get(keySetIterator.next()));
            counter++;
            if (counter > maxRecommendations) {
                break;
            }
        }
        if (recs == null) {
            logger.debug("recs are null");
        }
        return recs;

    }

    @Override
    public List<Bug> retrieveForDevId(String uuid,
                                      double threshold,
                                      double similarityWeight,
                                      double rankingWeight,
                                      int maxRecommendations) {

        //TODO: @Fotis We need to sort by similarity! ?
        List<UuidIssue> uuidIssues = uuidIssueDao.findByUuid(uuid,threshold);
        List<Bug> recs = new ArrayList<Bug>();
        TreeMap<Double, Bug> recsFull = new TreeMap<Double, Bug>();
        String bugDescription="";

        for (UuidIssue ui : uuidIssues) {

            bugDescription="";

            //TODO: We need to retrieve the name and surname of the developer from STARDOM
            List<IssueSubject> issueSubjects = issueSubjectDao.findByIssueId(ui.getIssueId());
            HashMap<String,Double> annotationsMap= new FastHashMap<String, Double>();

           for (IssueSubject is : issueSubjects) {
               bugDescription += " " +is.getSubject() +" ";
               annotationsMap.put(is.getSubject(), is.getWeight());
           }

            recsFull.put(ui.getSimilarity(), new Bug(ui.getIssueId(), "bug #" + ui.getIssueId(), bugDescription,annotationsMap));

        }



        Set<Double> descRecsKeySet = recsFull.descendingKeySet();
        Iterator keySetIterator = descRecsKeySet.iterator();
        Integer counter = 0;
        while (keySetIterator.hasNext()) {
            recs.add(recsFull.get(keySetIterator.next()));
            counter++;
            if (counter > maxRecommendations) {
                break;
            }
        }
        if (recs == null) {
            logger.debug("recs are null");
        }
        return recs;

    }
}
