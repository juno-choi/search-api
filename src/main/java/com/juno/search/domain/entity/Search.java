package com.juno.search.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long id;

    @Column(unique = true, length = 100)
    private String keyword;

    private int count;
    private LocalDateTime modifiedAt;
    private LocalDateTime createdAt;

    public Search(String keyword, int count, LocalDateTime modifiedAt, LocalDateTime createdAt) {
        this.keyword = keyword;
        this.count = count;
        this.modifiedAt = modifiedAt;
        this.createdAt = createdAt;
    }

    public static Search of(String keyword){
        LocalDateTime now = LocalDateTime.now();
        return new Search(keyword, 1, now, now);
    }
    public void plusCount(){
        this.count++;
        this.modifiedAt = LocalDateTime.now();
    }
}
