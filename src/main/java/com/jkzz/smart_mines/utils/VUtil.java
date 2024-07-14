package com.jkzz.smart_mines.utils;

import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.utils.utilsInterface.BranchHandler;
import com.jkzz.smart_mines.utils.utilsInterface.HandlerFunction;
import com.jkzz.smart_mines.utils.utilsInterface.PresentOrElseHandler;
import com.jkzz.smart_mines.utils.utilsInterface.ThrowExceptionFunction;

public final class VUtil {

    public static ThrowExceptionFunction isTrue(boolean b) {
        return (appExceptionCodeMsg) -> {
            if (b) {
                throw new AppException(appExceptionCodeMsg);
            }
        };
    }

    public static HandlerFunction handler(boolean b) {
        return (handle) -> {
            if (b) {
                handle.run();
            }
        };
    }

    public static BranchHandler isTrueOrFalse(boolean b) {
        return (trueHandle, falseHandle) -> {
            if (b) {
                trueHandle.run();
            } else {
                falseHandle.run();
            }
        };
    }

    public static PresentOrElseHandler<?> isBlankOrNoBlank(String s) {
        return (action, emptyAction) -> {
            if (null == s || s.isEmpty()) {
                emptyAction.run();
            } else {
                action.accept(s);
            }
        };
    }

}
