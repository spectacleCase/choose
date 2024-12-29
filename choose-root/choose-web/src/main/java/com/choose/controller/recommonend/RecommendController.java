package com.choose.controller.recommonend;

import com.choose.common.dto.CommentPageDto;
import com.choose.recommoend.dto.RecommendDto;
import com.choose.recommoend.vo.RecommendListVo;
import com.choose.service.recommend.RecommendService;
import com.choose.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 推荐控制器
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/3 下午1:45
 */
@RestController
@RequestMapping("/choose/recommend")
public class RecommendController {

    @Resource
    private RecommendService recommendService;


    @PostMapping("/v1/recommend")
    public Result recommend(@Valid  @RequestBody RecommendDto dto) {
       return Result.ok(recommendService.recommendMinus(dto));
    }

    @PostMapping("/v1/recommend-record")
    public Result recommendRecord(@Valid  @RequestBody CommentPageDto dto) {
        List<RecommendListVo> recommends = recommendService.recommendRecord(dto);
        return Result.ok(recommends);
    }

    /**
     * 推荐成功
     */
    @GetMapping("/v1/re_su")
    public Result recommendSuccess(String id) {
        recommendService.recommendSuccess(id);
        return Result.ok();
    }
}
