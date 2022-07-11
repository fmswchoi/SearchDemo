package com.demo.searcher.vo.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KaKaoMetaResponseVo {
    private Integer totalCount;
    private Integer pageableCount;
    private boolean isEnd;
    private KaKaoSameNameResponseVo sameName;
}
