package com.choose_admin.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.choose.admin.common.IndexVo;
import com.choose.admin.system.Server;
import com.choose.mapper.DishesMapper;
import com.choose.mapper.RecommendMapper;
import com.choose.mapper.SysLogMapper;
import com.choose.mapper.UserMapper;
import com.choose.recommoend.pojos.Recommend;
import com.choose_admin.service.common.AdminCommonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/11 下午3:56
 */
@Service
public class AdminCommonServiceImpl implements AdminCommonService {

    @Resource
    private DishesMapper dishesMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RecommendMapper recommendMapper;

    @Override
    public IndexVo getIndex() {
        try {
            IndexVo indexVo = new IndexVo();
            indexVo.setTodoItems(dishesMapper.getPendingList());
            Server server = new Server();
            server.copyTo();
            indexVo.setCpu((int) server.getCpu().getSys());
            indexVo.setJvm(server.getJvm().getRunTime());
            indexVo.setMemory((int) server.getMem().getUsage());
            indexVo.setAverageResponseTime(sysLogMapper.getAverageResponseTimeToday() == null ? 0 : sysLogMapper.getAverageResponseTimeToday());
            indexVo.setTotalUser(Math.toIntExact(userMapper.selectCount(null)));
            indexVo.setTotalDishes(Math.toIntExact(dishesMapper.selectCount(null)));
            indexVo.setActiveUsersToday(123);
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
            // 构建查询条件
            QueryWrapper<Recommend> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .ge(Recommend::getCreateTime, startOfDay)
                    .le(Recommend::getCreateTime, endOfDay);
            indexVo.setPredictionToday(Math.toIntExact(recommendMapper.selectCount(queryWrapper)));
            indexVo.setRecommendList(recommendMapper.getWeekRecommend());
            indexVo.setTotalUsers(getTotalUsers());
            indexVo.setNewUsers(getNewUsers());
            return indexVo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Long> getTotalUsers() {
        List<Map<String, Object>> monthUsers = userMapper.getMonthUsers();
        List<Long> totalUsers = new ArrayList<>();
        for (Map<String, Object> user : monthUsers) {
            // 确保类型转换正确
            totalUsers.add((Long) user.get("total_users"));
        }
        return totalUsers;
    }

    public List<Long> getNewUsers() {
        List<Map<String, Object>> monthUsers = userMapper.getMonthUsers();
        List<Long> newUsers = new ArrayList<>();
        for (Map<String, Object> user : monthUsers) {
            // 确保类型转换正确
            BigDecimal newUsersBigDecimal = (BigDecimal) user.get("new_users");
            newUsers.add(newUsersBigDecimal.longValue());
        }
        return newUsers;
    }
}
