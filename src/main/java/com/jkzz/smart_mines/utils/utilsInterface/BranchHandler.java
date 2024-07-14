package com.jkzz.smart_mines.utils.utilsInterface;

public interface BranchHandler {

    /**
     * 分支操作
     *
     * @param trueHandle  为true时要进行的操作
     * @param falseHandle 为false时要进行的操作
     */
    void trueOrFalseHandler(Runnable trueHandle, Runnable falseHandle);

}
