package com.elderly.care.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResponse {

    private String id;
    private Integer eventId;
    private String ambulanceId;
    private Integer eta;
    private Float distance;
    private ResponseStatus status;
    private LocalDateTime dispatchedAt;
    private AbnormalEvent event;

    public enum ResponseStatus {
        DISPATCH("已调度"),
        EN_ROUTE("途中"),
        ARRIVED("已到达"),
        COMPLETED("已完成");

        private final String description;

        ResponseStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
