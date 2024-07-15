package com.jkzz.smart_mines.utils.utilsinterface;

import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;

public interface ThrowExceptionFunction {

    /**
     * 抛出异常信息
     *
     * @param appExceptionCodeMsg 自定义异常信息
     */
    void throwAppException(AppExceptionCodeMsg appExceptionCodeMsg);

}
