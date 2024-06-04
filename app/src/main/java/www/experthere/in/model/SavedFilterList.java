package www.experthere.in.model;

import java.util.HashSet;

public class SavedFilterList {

    String range,from,to,star;

    HashSet<String> uniqueFilters ;

    public HashSet<String> getUniqueFilters() {
        return uniqueFilters;
    }

    public void setUniqueFilters(HashSet<String> uniqueFilters) {
        this.uniqueFilters = uniqueFilters;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getRange() {
        return range;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getStar() {
        return star;
    }
}
