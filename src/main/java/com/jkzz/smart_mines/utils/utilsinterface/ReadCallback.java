package com.jkzz.smart_mines.utils.utilsinterface;

import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;

public interface ReadCallback<T> {

    OperateResultExOne<T> execute();

}
