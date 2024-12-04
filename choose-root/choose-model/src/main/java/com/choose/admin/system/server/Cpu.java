package com.choose.admin.system.server;

import lombok.Data;

/**
 * <p>
 * cpu相关信息
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/7/3 下午9:13
 */
@Data
public class Cpu {

    /**
     * 核心数
     */
    private int cpuNum;

    /**
     * cpu总的使用率
     */
    private double total;

    /**
     * cpu的系统使用率
     */
    private double sys;

    /**
     * cpu当前使用率
     */
    private double used;

    /**
     * cpu当前等待率
     */
    private double wait;

    /**
     * cpu当前空闲率
     */
    private double free;

    public double getTotal() {
        return Arith.round(Arith.mul(total, 100), 2);
    }


    public double getSys() {
        return Arith.round(Arith.mul(sys / total, 100), 2);
    }


    public double getUsed() {
        return Arith.round(Arith.mul(used / total, 100), 2);
    }


    public double getWait() {
        return Arith.round(Arith.mul(wait / total, 100), 2);
    }


    public double getFree() {
        return Arith.round(Arith.mul(free / total, 100), 2);
    }


}
