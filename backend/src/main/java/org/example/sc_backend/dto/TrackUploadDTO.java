package org.example.sc_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TrackUploadDTO {
    private Long userId;
    private String date;
    private List<TrackPointDTO> trackPoints;

    @Data
    public static class TrackPointDTO {
        private Double latitude;
        private Double longitude;
        private Double lat;
        private Double lng;
        private String timestamp;
        private String time;
        private Double speed;
    }
}
