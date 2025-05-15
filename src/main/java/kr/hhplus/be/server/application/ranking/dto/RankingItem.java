package kr.hhplus.be.server.application.ranking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RankingItem {
    private final Long productId;
    private final Double score;

    @JsonCreator
    public RankingItem(
            @JsonProperty("productId") Long productId,
            @JsonProperty("score") Double score
    ) {
        this.productId = productId;
        this.score = score;
    }


}
