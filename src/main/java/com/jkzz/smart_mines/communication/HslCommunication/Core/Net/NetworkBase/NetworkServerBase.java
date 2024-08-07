package com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase;

public class NetworkServerBase extends NetworkBase {

    // region Constructor

    /// <summary>

    /**
     * 服务器引擎是否启动<br />
     * Whether the server engine is started
     */
    private boolean IsStarted = false;

    // endregion

    // region Public Properties
    /**
     * 获取或设置服务器的端口号，如果是设置，需要在服务器启动前设置完成，才能生效。<br />
     * Gets or sets the port number of the server. If it is set, it needs to be set before the server starts to take effect.
     * <br /><br />
     * 需要在服务器启动之前设置为有效
     */
    private int Port = 0;

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public NetworkServerBase() {
        IsStarted = false;
        Port = 0;
    }

    public boolean isStarted() {
        return IsStarted;
    }

    protected void setStarted(boolean started) {
        IsStarted = started;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    // endregion
//
//    // region Protect Method
//
//    /// <summary>
//    /// 异步传入的连接申请请求<br />
//    /// Asynchronous incoming connection request
//    /// </summary>
//    /// <param name="iar">异步对象</param>
//    protected void AsyncAcceptCallback( IAsyncResult iar )
//    {
//        //还原传入的原始套接字
//        if (iar.AsyncState is Socket server_socket)
//        {
//            Socket client = null;
//            try
//            {
//                // 在原始套接字上调用EndAccept方法，返回新的套接字
//                client = server_socket.EndAccept( iar );
//                ThreadPool.QueueUserWorkItem( new WaitCallback( ThreadPoolLogin ), client );
//            }
//            catch (ObjectDisposedException)
//            {
//                // 服务器关闭时候触发的异常，不进行记录
//                return;
//            }
//            catch (Exception ex)
//            {
//                // 有可能刚连接上就断开了，那就不管
//                client?.Close( );
//                LogNet?.WriteException( ToString(), StringResources.Language.SocketAcceptCallbackException, ex );
//            }
//
//            // 如果失败，尝试启动三次
//            int i = 0;
//            while (i < 3)
//            {
//                try
//                {
//                    server_socket.BeginAccept( new AsyncCallback( AsyncAcceptCallback ), server_socket );
//                    break;
//                }
//                catch (Exception ex)
//                {
//                    Thread.Sleep( 1000 );
//                    LogNet?.WriteException( ToString( ), StringResources.Language.SocketReAcceptCallbackException, ex );
//                    i++;
//                }
//            }
//
//            if (i >= 3)
//            {
//                LogNet?.WriteError( ToString( ), StringResources.Language.SocketReAcceptCallbackException );
//                // 抛出异常，终止应用程序
//                throw new Exception( StringResources.Language.SocketReAcceptCallbackException );
//            }
//        }
//    }
//
//    private void ThreadPoolLogin( object obj )
//    {
//        if (obj is Socket socket)
//        {
//            IPEndPoint endPoint = (IPEndPoint)socket.RemoteEndPoint;
//            OperateResult check = SocketAcceptExtraCheck( socket, endPoint );
//            if (!check.IsSuccess)
//            {
//                LogNet?.WriteDebug( ToString( ), $"[{endPoint}] Socket Accept Extra Check Failed : {check.Message}" );
//                CloseSocket(Socket);
//            }
//            else
//            {
//                ThreadPoolLogin( socket, endPoint );
//            }
//        }
//    }
//
//    /// <summary>
//    /// 当客户端连接到服务器，并听过额外的检查后，进行回调的方法<br />
//    /// Callback method when the client connects to the server and has heard additional checks
//    /// </summary>
//    /// <param name="socket">socket对象</param>
//    /// <param name="endPoint">远程的终结点</param>
//    protected void ThreadPoolLogin( Socket socket, IPEndPoint endPoint ) => socket?.Close( );
//
//    // endregion
//
//    // region Start And Close
//
//    /// <summary>
//    /// 当客户端的socket登录的时候额外检查的操作，并返回操作的结果信息。<br />
//    /// The operation is additionally checked when the client's socket logs in, and the result information of the operation is returned.
//    /// </summary>
//    /// <param name="socket">套接字</param>
//    /// <param name="endPoint">终结点</param>
//    /// <returns>验证的结果</returns>
//    protected OperateResult SocketAcceptExtraCheck( Socket socket, IPEndPoint endPoint ) => OperateResult.CreateSuccessResult( );
//
//    /**
//     * 服务器启动时额外的初始化信息，可以用于启动一些额外的服务的操作。<br />
//     * The extra initialization information when the server starts can be used to start some additional service operations.
//     */
//    protected void StartInitialization( ) { }
//
//    /**
//     * 指定端口号来启动服务器的引擎<br />
//     * Specify the port number to start the server's engine
//     * @param port 指定一个端口号
//     * @throws IOException 发送的异常
//     */
//    public void ServerStart( int port ) throws IOException {
//        if (!IsStarted)
//        {
//            StartInitialization( );
//            CoreSocket = new ServerSocket(port);
//            CoreSocket = new Socket( AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp );
//            CoreSocket.Bind( new IPEndPoint( IPAddress.Any, port ) );
//            CoreSocket.Listen( 500 );
//            CoreSocket.BeginAccept( new AsyncCallback( AsyncAcceptCallback ), CoreSocket );
//            IsStarted = true;
//            Port = port;
//            if(LogNet!=null) LogNet.WriteInfo( toString( ), StringResources.Language.NetEngineStart() );
//        }
//    }
//
//    /**
//     * 使用已经配置好的端口启动服务器的引擎<br />
//     * Use the configured port to start the server's engine
//     */
//    public void ServerStart( ) throws IOException {
//        ServerStart( Port );
//    }
//
//    /**
//     * 服务器关闭的时候需要做的事情<br />
//     * Things to do when the server is down
//     */
//    protected void CloseAction( ) { }
//
//    /**
//     * 关闭服务器的引擎<br />
//     * Shut down the server's engine
//     */
//    public void ServerClose( ) {
//        if (IsStarted) {
//            IsStarted = false;
//            CloseAction();
//            CloseSocket(CoreSocket);
//            if (LogNet != null) LogNet.WriteInfo(toString(), StringResources.Language.NetEngineClose());
//        }
//    }
//
//    // endregion
//
//    // region Create Alien Connection
//
//    /**************************************************************************************************
//     *
//     *    此处实现了连接Hsl异形客户端的协议，特殊的协议实现定制请联系作者
//     *    QQ群：592132877
//     *
//     *************************************************************************************************/
//
//    /**
//     * 创建一个指定的异形客户端连接，使用Hsl协议来发送注册包<br />
//     * Create a specified profiled client connection and use the Hsl protocol to send registration packets
//     * @param ipAddress Ip地址
//     * @param port 端口号
//     * @param dtuId 设备唯一ID号，最长11
//     * @return 是否成功连接
//     */
//    public OperateResult ConnectHslAlientClient(String ipAddress, int port , String dtuId)
//    {
//        if (dtuId.length() > 11) dtuId = dtuId.substring( 11 );
//        byte[] sendBytes = new byte[28];
//        sendBytes[0] = 0x48;
//        sendBytes[1] = 0x73;
//        sendBytes[2] = 0x6E;
//        sendBytes[3] = 0x00;
//        sendBytes[4] = 0x17;
//
//        Encoding.ASCII.GetBytes( dtuId ).CopyTo( sendBytes, 5 );
//        // 创建连接
//        OperateResultExOne<Socket> connect = CreateSocketAndConnect( ipAddress, port, 10000 );
//        if (!connect.IsSuccess) return connect;
//
//        // 发送数据
//        OperateResult send = Send( connect.Content, sendBytes );
//        if (!send.IsSuccess) return send;
//
//        // 接收数据
//        OperateResultExOne<byte[]> receive = ReceiveByMessage( connect.Content, 10000, new AlienMessage( ) );
//        if (!receive.IsSuccess) return receive;
//
//        switch (receive.Content[5])
//        {
//            case 0x01:
//            {
//                CloseSocket(connect.Content);
//                return new OperateResult( StringResources.Language.DeviceCurrentIsLoginRepeat() );
//            }
//            case 0x02:
//            {
//                CloseSocket(connect.Content);
//                return new OperateResult( StringResources.Language.DeviceCurrentIsLoginForbidden() );
//            }
//            case 0x03:
//            {
//                CloseSocket(connect.Content);
//                return new OperateResult( StringResources.Language.PasswordCheckFailed() );
//            }
//        }
//
//        ThreadPoolLogin( connect.Content );
//        return OperateResult.CreateSuccessResult( );
//    }

    // endregion

    // region Object Override

    public String toString() {
        return "NetworkServerBase[" + Port + "]";
    }

    // endregion
}
