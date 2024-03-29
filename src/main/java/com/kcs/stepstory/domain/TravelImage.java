package com.kcs.stepstory.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "TravelImage")
public class TravelImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travelImageId",nullable = false)
    private Long travelImageId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travelReportId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TravelReport travelReport;

    @Column(name = "imageUrl",nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detailCourseId", nullable = false)
    private DetailCourse detailCourse;

    @Builder
    public TravelImage(TravelReport travelReport, String imageUrl, DetailCourse detailCourse){
        this.travelReport = travelReport;
        this.imageUrl = imageUrl;
        this.detailCourse = detailCourse;
    }

    public void updateTravelImage(DetailCourse detailCourse){
        this.detailCourse = detailCourse;
    }


}
