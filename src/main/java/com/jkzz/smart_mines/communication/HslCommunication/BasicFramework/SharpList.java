package com.jkzz.smart_mines.communication.HslCommunication.BasicFramework;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Thread.SimpleHybirdLock;

import java.lang.reflect.Array;

/**
 * 一个高效的数组管理类，用于高效控制固定长度的数组实现<br />
 * An efficient array management class for efficient control of fixed-length array implementations
 *
 * @param <T> 类型对象
 */
public class SharpList<T> {
    private Class<T> typeClass;
    private T[] array;
    private int capacity = 2048;
    private int count = 0;
    private int lastIndex = 0;
    private SimpleHybirdLock hybirdLock;

    /**
     * 实例化一个对象，需要指定数组的最大数据对象
     *
     * @param count      数据的个数
     * @param appendLast 是否从最后一个数添加
     */
    @SuppressWarnings("unchecked")
    public SharpList(Class<T> type, int count, boolean appendLast) {
        if (count > 8192) capacity = 4096;

        this.typeClass = type;
        this.array = (T[]) Array.newInstance(type, capacity + count);
        this.hybirdLock = new SimpleHybirdLock();
        this.count = count;
        if (appendLast) this.lastIndex = count;
    }

    /**
     * 获取数据的个数信息<br/>
     * Get the number of arrays
     *
     * @return 个数信息
     */
    public int getCount() {
        return this.count;
    }

    /**
     * 新增一个数据值<br />
     * Add a data value
     *
     * @param value 数据值信息
     */
    @SuppressWarnings("unchecked")
    public void AddValue(T value) {
        this.hybirdLock.Enter();

        if (lastIndex < (capacity + count)) {
            array[lastIndex++] = value;
        } else {
            // 需要重新挪位置了
            T[] buffer = (T[]) Array.newInstance(this.typeClass, capacity + count);
            System.arraycopy(array, capacity, buffer, 0, count);
            array = buffer;
            lastIndex = count;
        }

        hybirdLock.Leave();
    }

    /**
     * 批量的增加数据
     * Increase data in batches
     *
     * @param values 批量数据信息
     */
    public void Add(Iterable<T> values) {
        for (T value : values) {
            AddValue(value);
        }
    }

    /**
     * 获取数据的数组值
     * Get array value of data
     *
     * @return 数组值
     * @throws ClassCastException 数据转换异常的类
     */
    @SuppressWarnings("unchecked")
    public T[] ToArray() {
        T[] result = null;
        hybirdLock.Enter();

        if (lastIndex < count) {
            result = (T[]) Array.newInstance(this.typeClass, lastIndex);
            System.arraycopy(array, 0, result, 0, lastIndex);
        } else {
            result = (T[]) Array.newInstance(this.typeClass, count);
            System.arraycopy(array, lastIndex - count, result, 0, count);
        }
        hybirdLock.Leave();
        return result;
    }

    /**
     * 获取指定索引的位置的数据
     * Gets the data at the specified index
     *
     * @param index 索引位置
     * @return 数据值
     * @throws IndexOutOfBoundsException 索引超出的异常
     */
    public T getByIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0) throw new IndexOutOfBoundsException("Index must larger than zero");
        if (index >= count) throw new IndexOutOfBoundsException("Index must smaller than array length");
        T tmp = null;
        hybirdLock.Enter();

        if (lastIndex < count) {
            tmp = array[index];
        } else {
            tmp = array[index + lastIndex - count];
        }

        hybirdLock.Leave();
        return tmp;
    }

    /**
     * 设置指定索引的位置的数据
     * Sets the data at the specified index
     *
     * @param index 索引位置
     * @param value 数据值
     * @throws IndexOutOfBoundsException
     */
    public void setByIndex(int index, T value) throws IndexOutOfBoundsException {

        if (index < 0) throw new IndexOutOfBoundsException("Index must larger than zero");
        if (index >= count) throw new IndexOutOfBoundsException("Index must smaller than array length");
        hybirdLock.Enter();

        if (lastIndex < count) {
            array[index] = value;
        } else {
            array[index + lastIndex - count] = value;
        }

        hybirdLock.Leave();
    }
}
