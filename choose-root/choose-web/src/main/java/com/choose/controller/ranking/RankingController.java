package com.choose.controller.ranking;

import com.choose.ranking.dto.RankingDto;
import com.choose.ranking.vo.RankingVo;
import com.choose.service.ranking.RankingService;
import com.choose.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 排行榜控制器
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/5 上午1:21
 */
@RestController
@RequestMapping("/choose/ranking")
public class RankingController {

    @Resource
    private RankingService rankingService;


    @PostMapping("/v1/getColumnList")
    public Result getColumnList() {
        return Result.ok(rankingService.getColumn());
    }

    @PostMapping("/v1/getRanking")
    public Result getRanking(@Valid  @RequestBody RankingDto dto) {
        List<RankingVo> ranking = rankingService.getRanking(dto);
        return Result.ok(ranking);
    }
}
