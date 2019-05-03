package edu.temple.cla.policydb.capcodeassignment;

public class Criteria {

    /**
     * @return the CRITERIA
     */
    public static Criteria[] getCriteria() {
        return CRITERIA;
    }
    
    public static Criteria getCriteriaByIndex(int i) {
        return CRITERIA[i];
    }
    
    public static Criteria getCriteriaByTitle(String title) {
        for (Criteria c : CRITERIA)
            if (title.equals(c.title)) return c;
        return null;
    }

    private final int index;
    private final String title;
    private final int currentCode;
    private final String keyWords;
    private final String filter;
    private final int newPPACode;
    private final int newCAPCode;

    public Criteria(int index, String title, int currentCode, String keyWords, 
            String filter, int newPPACode, int newCAPCode) {
        this.index = index;
        this.title = title;
        this.currentCode = currentCode;
        this.keyWords = keyWords;
        this.filter = filter;
        this.newPPACode = newPPACode;
        this.newCAPCode = newCAPCode;
    }

    private static final Criteria[] CRITERIA = {
        new Criteria(0, "Daycare", 5, "daycare or \"child care\" or paternity " 
                + "or maternity or aftercare", "", 5, 13), 
        new Criteria(1, "Art", 6, "\" art \" or culture or music or concerts or " 
                + "history or museum or mural", "", 6, 23), 
        new Criteria(2, "Real Estate", 12, "\"eminent domain\" or landlord or " 
                + "\"real estate\" or property or tenant", "", 12, 14), 
        new Criteria(3, "Lottery", 20, "lottery or lotto", "", 20, 15), 
        new Criteria(4, "Commemorative", 20, "commemorative or honoring", "", 20, 23), 
        new Criteria(5, "Local Taxes", 24, "taxes or revenue or assessment or " 
                + "millage or school or property or budget or bond or debt " 
                + "or credit", "tax", 24, 1), 
        new Criteria(6, "Immigration", 5, "immigra or refugee or alien or " 
                + "border or amnesty or naturalization", "", 9, 9), 
        new Criteria(7, "Aquaculture", 7, "aquaculture or \"fish farm\"", "", 4, 4)
    };

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the currentCode
     */
    public int getCurrentCode() {
        return currentCode;
    }

    /**
     * @return the keyWords
     */
    public String getKeyWords() {
        return keyWords;
    }

    /**
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @return the newPPACode
     */
    public int getNewPPACode() {
        return newPPACode;
    }

    /**
     * @return the newCAPCode
     */
    public int getNewCAPCode() {
        return newCAPCode;
    }

}
