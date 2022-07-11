package com.demo.searcher.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "keyword_count")
@IdClass(KeywordCount.PK.class)
public class KeywordCount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int seq;
    @Id
    private String keyword;
    private int count;

    @Data
    static class PK implements Serializable {
        private int seq;
        private String keyword;
    }
}
