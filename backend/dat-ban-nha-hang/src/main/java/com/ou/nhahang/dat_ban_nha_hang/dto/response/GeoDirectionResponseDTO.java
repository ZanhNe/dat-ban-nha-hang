package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoDirectionResponseDTO(
                @JsonProperty("routes") Route[] routes) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Route(
                        // @JsonProperty("legs") Leg[] legs,
                        @JsonProperty("bounds") Bounds bounds,
                        @JsonAlias("overview_polyline") Polyline polyline) {
        }

        public record Polyline(
                        @JsonProperty("points") String points) {
        }

        public record Bounds(
                        @JsonProperty("northeast") Location northeast,
                        @JsonProperty("southwest") Location southwest) {
        }

        public record Location(
                        @JsonAlias("lat") Double latitude,
                        @JsonAlias("lng") Double longitude) {
        }

        // @JsonIgnoreProperties(ignoreUnknown = true)
        // public record Leg(
        // @JsonProperty("distance") Distance distance,
        // @JsonProperty("duration") Duration duration) {
        // }

        // public record Distance(
        // @JsonProperty("text") String text,
        // @JsonProperty("value") Long value) {
        // }

        // public record Duration(
        // @JsonProperty("text") String text,
        // @JsonProperty("value") Long value) {
        // }
}
