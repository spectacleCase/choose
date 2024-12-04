package com.choose.service.ranking;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.ranking.dto.RankingDto;
import com.choose.ranking.pojos.Column;
import com.choose.ranking.pojos.Ranking;
import com.choose.ranking.vo.RankingVo;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/5 上午1:23
 */

public interface RankingService extends IService<Ranking>{

    /**
     * 获取栏目
     */
    List<Column> getColumn();

    /**
     * 根据id获取排行榜
     */
    List<RankingVo> getRanking(RankingDto dto);
}
