package com.techcorp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String        message;
    private LocalDateTime timestamp;
    private int           status;
    private String        path;

    public ErrorResponse() { this.timestamp = LocalDateTime.now(); }
    
    @JsonCreator
    public ErrorResponse(
        @JsonProperty("message") String message,
        @JsonProperty("status")  int    status,
        @JsonProperty("path")    String path
    ) {
        this.message   = message;
        this.timestamp = LocalDateTime.now();
        this.status    = status;
        this.path      = path;
    }

    @JsonCreator
    public ErrorResponse(
        @JsonProperty("message")   String        message,
        @JsonProperty("timestamp") LocalDateTime timestamp,
        @JsonProperty("status")    int           status,
        @JsonProperty("path")      String        path
    ) {
        this.message   = message;
        this.timestamp = timestamp;
        this.status    = status;
        this.path      = path;
    }

    public String        getMessage()   { return message;   }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int           getStatus()    { return status;    }
    public String        getPath()      { return path;      }

    public void setMessage(String message)            { this.message   = message;   }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setStatus(int status)                 { this.status    = status;    }
    public void setPath(String path)                  { this.path      = path;      }
}

