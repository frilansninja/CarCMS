package se.meltastudio.cms.parts.api;

/**
 * Search query for spare parts.
 */
public class PartSearchQuery {

    private String query;
    private String category;
    private int limit = 20;

    public PartSearchQuery() {
    }

    public PartSearchQuery(String query, String category, int limit) {
        this.query = query;
        this.category = category;
        this.limit = limit;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
