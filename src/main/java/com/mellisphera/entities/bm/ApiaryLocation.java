package com.mellisphera.entities.bm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ApiaryLocation implements Serializable {

    @JsonProperty("apiaryId")
    private String apiaryId;

    @JsonProperty("start")
    private long start;

    @JsonProperty("end")
    private long end;

    public ApiaryLocation() {
    }

    public ApiaryLocation(String apiaryId, long start, long end) {
        this.apiaryId = apiaryId;
        this.start = start;
        this.end = end;
    }

    public String getApiaryId() {
        return apiaryId;
    }

    public void setApiaryId(String apiaryId) {
        this.apiaryId = apiaryId;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "ApiaryLocation{" +
                "apiaryId='" + apiaryId + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
