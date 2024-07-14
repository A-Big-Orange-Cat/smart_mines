package com.jkzz.smart_mines.utils.utilsInterface;

import java.util.function.Consumer;

public interface PresentOrElseHandler<T> {

    /**
     * 值不为空时执行消费操作，值为空时执行其他操作
     *
     * @param action      值不为空时执行的消费操作
     * @param emptyAction 值为空时执行的操作
     */
    void presentOrElseHandler(Consumer<? super T> action, Runnable emptyAction);

}
