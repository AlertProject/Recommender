package eu.alertproject.iccs.socrates.calculator.api;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 9/27/12
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SimilarityComputationService {


    void computeSimilaritesForIdentity(String uuid);
    void computeSimilaritesForIssue(Integer id);
    void computeSimilarityForComponent(String component);

    void computeSimilaritiesForAllIdentities();
    void computeSimilaritiesForAllIssues();
    void computeSimilaritiesForAllComponents();


}
