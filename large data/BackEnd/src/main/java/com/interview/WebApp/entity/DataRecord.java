package com.interview.WebApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "data_records", indexes = {
        @Index(name = "idx_data_records_upload", columnList = "upload_id"),
        @Index(name = "idx_data_records_col1", columnList = "col01"),
        @Index(name = "idx_data_records_col2", columnList = "col02"),
        @Index(name = "idx_data_records_col3", columnList = "col03")
})
public class DataRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "upload_id", nullable = false)
    @JsonIgnore
    private Upload upload;

    @Column(length = 2048) private String col01;
    @Column(length = 2048) private String col02;
    @Column(length = 2048) private String col03;
    @Column(length = 2048) private String col04;
    @Column(length = 2048) private String col05;
    @Column(length = 2048) private String col06;
    @Column(length = 2048) private String col07;
    @Column(length = 2048) private String col08;
    @Column(length = 2048) private String col09;
    @Column(length = 2048) private String col10;
    @Column(length = 2048) private String col11;
    @Column(length = 2048) private String col12;
    @Column(length = 2048) private String col13;
    @Column(length = 2048) private String col14;
    @Column(length = 2048) private String col15;
    @Column(length = 2048) private String col16;
    @Column(length = 2048) private String col17;
    @Column(length = 2048) private String col18;
    @Column(length = 2048) private String col19;
    @Column(length = 2048) private String col20;
    @Column(length = 2048) private String col21;
    @Column(length = 2048) private String col22;
    @Column(length = 2048) private String col23;
    @Column(length = 2048) private String col24;
    @Column(length = 2048) private String col25;
    @Column(length = 2048) private String col26;
    @Column(length = 2048) private String col27;
    @Column(length = 2048) private String col28;
    @Column(length = 2048) private String col29;
    @Column(length = 2048) private String col30;
    @Column(length = 2048) private String col31;
    @Column(length = 2048) private String col32;
    @Column(length = 2048) private String col33;
    @Column(length = 2048) private String col34;
    @Column(length = 2048) private String col35;
    @Column(length = 2048) private String col36;
    @Column(length = 2048) private String col37;
    @Column(length = 2048) private String col38;
    @Column(length = 2048) private String col39;
    @Column(length = 2048) private String col40;
    @Column(length = 2048) private String col41;
    @Column(length = 2048) private String col42;
    @Column(length = 2048) private String col43;
    @Column(length = 2048) private String col44;
    @Column(length = 2048) private String col45;
    @Column(length = 2048) private String col46;
    @Column(length = 2048) private String col47;
    @Column(length = 2048) private String col48;
    @Column(length = 2048) private String col49;
    @Column(length = 2048) private String col50;
    @Column(length = 2048) private String col51;
    @Column(length = 2048) private String col52;
    @Column(length = 2048) private String col53;
    @Column(length = 2048) private String col54;
    @Column(length = 2048) private String col55;
    @Column(length = 2048) private String col56;
    @Column(length = 2048) private String col57;
    @Column(length = 2048) private String col58;
    @Column(length = 2048) private String col59;
    @Column(length = 2048) private String col60;
    @Column(length = 2048) private String col61;
    @Column(length = 2048) private String col62;
    @Column(length = 2048) private String col63;
    @Column(length = 2048) private String col64;
} 