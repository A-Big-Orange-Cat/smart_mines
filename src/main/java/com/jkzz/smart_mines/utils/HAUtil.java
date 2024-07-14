package com.jkzz.smart_mines.utils;

import com.jkzz.smart_mines.communication.core.Monitor;
import com.jkzz.smart_mines.communication.core.impl.*;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.enumerate.impl.DeviceTypeEnum;
import com.jkzz.smart_mines.exception.AppException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HAUtil {

    public static Monitor getMonitor(Integer deviceTypeId) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeEnum.getByValue(deviceTypeId);
        switch (deviceTypeEnum) {
            case 智能风门:
                return new fengMen();
            case 智能风门_百叶风窗:
                return new fengMenBaiYe();
            case 智能风门_推拉风窗:
                return new fengMenTuiLa();
            case 智能风窗_单:
                return new fengChuangDan();
            case 智能风窗_双:
                return new fengChuangShuang();
            case 精准测风:
                return new ceFeng();
            case 综采喷雾:
                return new zongCai();
            case 多功能喷雾:
                return new penWu();
            case 分风器:
                return new fenFengQi();
            case 防火门:
                return new fangHuoMen();
            case 副井口风门:
                return new fuJingKouFengMen();
            default:
                return null;
        }
    }

    public static Integer getInteger(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (!(value instanceof Integer)) {
            throw new AppException(AppExceptionCodeMsg.ERROR_DATA_TYPE);
        }
        return (Integer) value;
    }

    public static String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (!(value instanceof String)) {
            throw new AppException(AppExceptionCodeMsg.ERROR_DATA_TYPE);
        }
        return (String) value;
    }

    public static Long getLong(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (!(value instanceof Long)) {
            throw new AppException(AppExceptionCodeMsg.ERROR_DATA_TYPE);
        }
        return (Long) value;
    }

    public static <T> List<T> getList(Map<String, Object> data, String key, Class<T> tClass) {
        Object value = data.get(key);
        List<T> list = new ArrayList<>();
        if (value instanceof ArrayList<?>) {
            for (Object o : (List<?>) value) {
                list.add(tClass.cast(o));
            }
            return list;
        }
        return null;
    }

}
