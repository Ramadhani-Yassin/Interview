package com.interview.WebApp.repository;

import com.interview.WebApp.entity.DataRecord;
import com.interview.WebApp.entity.Upload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DataRecordRepository extends JpaRepository<DataRecord, Long> {
    Page<DataRecord> findByUpload(Upload upload, Pageable pageable);

    @Query("select dr from DataRecord dr where dr.upload = :upload and (" +
            "lower(dr.col01) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col02) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col03) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col04) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col05) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col06) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col07) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col08) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col09) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col10) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col11) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col12) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col13) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col14) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col15) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col16) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col17) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col18) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col19) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col20) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col21) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col22) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col23) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col24) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col25) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col26) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col27) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col28) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col29) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col30) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col31) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col32) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col33) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col34) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col35) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col36) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col37) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col38) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col39) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col40) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col41) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col42) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col43) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col44) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col45) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col46) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col47) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col48) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col49) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col50) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col51) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col52) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col53) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col54) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col55) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col56) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col57) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col58) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col59) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col60) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col61) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col62) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col63) like lower(concat('%', :q, '%')) or " +
            "lower(dr.col64) like lower(concat('%', :q, '%'))" +
            ")")
    Page<DataRecord> search(@Param("upload") Upload upload, @Param("q") String q, Pageable pageable);
} 