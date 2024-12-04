package com.choose.admin.common;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/11 下午3:43
 */
@Data
public class IndexVo {

    private TodoItems todoItems;

    private SystemUsage systemUsage = new SystemUsage();

    private ApiPerformance apiPerformance = new ApiPerformance();

    private DataOverview dataOverview = new DataOverview();

    private List<Integer> recommendList;

    private List<Long> totalUsers;

    private List<Long> newUsers;


    public void setCpu(Integer cpu) {
        systemUsage.setCpu(cpu);
    }

    public void setMemory(Integer memory) {
        systemUsage.setMemory(memory);
    }

    public void setJvm(String jvm) {
        systemUsage.setJvm(jvm);
    }

    public void setAverageResponseTime(Double averageResponseTime) {
        apiPerformance.setAverageResponseTime(averageResponseTime);
    }

    public void setTotalUser(Integer totalUser) {
        dataOverview.setTotalUser(totalUser);
    }

    public void setActiveUsersToday(Integer activeUsersToday) {
        dataOverview.setActiveUsersToday(activeUsersToday);
    }

    public void setTotalDishes(Integer totalDishes) {
        dataOverview.setTotalDishes(totalDishes);
    }

    public void setPredictionToday(Integer predictionToday) {
        dataOverview.setPredictionToday(predictionToday);
    }


}



@Data
class SystemUsage {
    private Integer cpu;

    private Integer memory;

    private String jvm;
}

@Data
class ApiPerformance {
    private Double averageResponseTime;
}

@Data
class DataOverview {
    private Integer totalUser;

    private Integer activeUsersToday;

    private Integer totalDishes;

    private Integer predictionToday;
}
