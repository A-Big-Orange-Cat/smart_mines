package com.jkzz.smart_mines.communication.HslCommunication.MQTT;

import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftBasic;
import com.jkzz.smart_mines.communication.HslCommunication.BasicFramework.SoftIncrementCount;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.HslProtocol;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Net.NetworkBase.NetworkDoubleBase;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Security.AesCryptography;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Security.HslSecurity;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Security.RSACryptoServiceProvider;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Transfer.RegularByteTransform;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.ActionOperateExTwo;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResult;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExOne;
import com.jkzz.smart_mines.communication.HslCommunication.Core.Types.OperateResultExTwo;
import com.jkzz.smart_mines.communication.HslCommunication.StringResources;
import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 基于MQTT协议的RPC调用客户端，支持加密操作。可以访问远程服务器的注册的RPC接口<br />
 * RPC call client based on MQTT protocol, support encryption operation. The registered RPC interface that can access the remote server
 */
public class MqttSyncClient extends NetworkDoubleBase {

    // region Constructor

    private SoftIncrementCount incrementCount;                            // 自增的数据id对象
    private MqttConnectionOptions connectionOptions;                      // 连接服务器时的配置信息

    //endregion

    //region InitializationOnConnect
    private String StringEncoding = "UTF-8";                              // 使用字符串通信时的编码

    // endregion

    // region NetworkDoubleBase Override
    private RSACryptoServiceProvider cryptoServiceProvider = null;        // rsa加密解密对象
    private AesCryptography aesCryptography = null;                       // aes加密对象

    /**
     * 实例化一个MQTT的同步客户端<br />
     * Instantiate an MQTT synchronization client
     *
     * @param options 连接的配置信息
     */
    public MqttSyncClient(MqttConnectionOptions options) {
        this.setByteTransform(new RegularByteTransform());
        this.connectionOptions = options;
        this.setIpAddress(options.IpAddress);
        this.setPort(options.Port);
        this.incrementCount = new SoftIncrementCount(65536, 1);
        this.setConnectTimeOut(options.ConnectTimeout);
        this.setReceiveTimeOut(60_000);
    }

    // endregion

    // region Public Method

    /**
     * 通过指定的ip地址及端口来实例化一个同步的MQTT客户端<br />
     * Instantiate a synchronized MQTT client with the specified IP address and port
     *
     * @param ipAddress IP地址信息
     * @param port      端口号信息
     */
    public MqttSyncClient(String ipAddress, int port) {
        this.connectionOptions = new MqttConnectionOptions();
        this.connectionOptions.IpAddress = ipAddress;
        this.connectionOptions.Port = port;
        this.setByteTransform(new RegularByteTransform());
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.incrementCount = new SoftIncrementCount(65536, 1);
        this.setReceiveTimeOut(60_000);
    }

    protected OperateResult InitializationOnConnect(Socket socket) {
        RSACryptoServiceProvider rsa = null;
        if (this.connectionOptions.UseRSAProvider) {
            cryptoServiceProvider = new RSACryptoServiceProvider();

            OperateResult sendKey = Send(socket, MqttHelper.BuildMqttCommand((byte) 0xFF, null,
                    HslSecurity.ByteEncrypt(cryptoServiceProvider.GetPEMPublicKey()), null).Content);
            if (!sendKey.IsSuccess) return sendKey;

            OperateResultExTwo<Byte, byte[]> key = ReceiveMqttMessage(socket, getReceiveTimeOut(), null);
            if (!key.IsSuccess) return key;

            try {
                byte[] serverPublicToken = cryptoServiceProvider.DecryptLargeData(HslSecurity.ByteDecrypt(key.Content2));
                rsa = new RSACryptoServiceProvider(null, serverPublicToken);
            } catch (Exception ex) {
                CloseSocket(socket);
                return new OperateResult("RSA check failed: " + ex.getMessage());
            }
        }
        OperateResultExOne<byte[]> command = MqttHelper.BuildConnectMqttCommand(this.connectionOptions, "HUSL", rsa);
        if (!command.IsSuccess) return command;

        // 发送连接的报文信息
        OperateResult send = Send(socket, command.Content);
        if (!send.IsSuccess) return send;

        // 接收服务器端注册返回的报文信息
        OperateResultExTwo<Byte, byte[]> receive = ReceiveMqttMessage(socket, getReceiveTimeOut(), null);
        if (!receive.IsSuccess) return receive;

        // 检查连接的返回状态是否正确
        OperateResult check = MqttHelper.CheckConnectBack(receive.Content1, receive.Content2);
        if (!check.IsSuccess) {
            CloseSocket(socket);
            return check;
        }

        if (this.connectionOptions.UseRSAProvider) {
            try {
                String key = new String(cryptoServiceProvider.DecryptLargeData(SoftBasic.BytesArrayRemoveBegin(receive.Content2, 2)), StandardCharsets.UTF_8);
                this.aesCryptography = new AesCryptography(key);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        this.incrementCount.ResetCurrentValue();          // 重置消息计数
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(Socket socket, byte[] send) {
        OperateResultExTwo<Byte, byte[]> read = ReadMqttFromCoreServer(socket, send, null, null, null);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content2);
    }

    private OperateResultExTwo<Byte, byte[]> ReadMqttFromCoreServer(
            Socket socket,
            byte[] send,
            ActionOperateExTwo<Long, Long> sendProgress,
            ActionOperateExTwo<String, String> handleProgress,
            ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResult sendResult = Send(socket, send);
        if (!sendResult.IsSuccess) return OperateResultExTwo.CreateFailedResult(sendResult);

        // 先确认对方是否接收完数据
        while (true) {
            OperateResultExTwo<Byte, byte[]> server_receive = ReceiveMqttMessage(socket, getReceiveTimeOut(), null);
            if (!server_receive.IsSuccess) return OperateResultExTwo.CreateFailedResult(server_receive);

            OperateResultExTwo<String, byte[]> server_back = MqttHelper.ExtraMqttReceiveData(server_receive.Content1, server_receive.Content2);
            if (!server_back.IsSuccess) return OperateResultExTwo.CreateFailedResult(server_back);

            if (server_back.Content2.length != 16)
                return new OperateResultExTwo<Byte, byte[]>(StringResources.Language.ReceiveDataLengthTooShort());
            long already = Utilities.getLong(server_back.Content2, 0);
            long total = Utilities.getLong(server_back.Content2, 8);
            if (sendProgress != null) sendProgress.Action(already, total);
            if (already == total) break;
        }

        // 如果接收到进度报告，就继续接收，直到不是进度报告的数据为止
        while (true) {
            OperateResultExTwo<Byte, byte[]> receive = ReceiveMqttMessage(socket, getReceiveTimeOut(), receiveProgress);
            if (!receive.IsSuccess) return OperateResultExTwo.CreateFailedResult(receive);

            if ((receive.Content1 & 0xf0) >> 4 == MqttControlMessage.REPORTPROGRESS) {
                OperateResultExTwo<String, byte[]> extra = MqttHelper.ExtraMqttReceiveData(receive.Content1, receive.Content2);
                if (handleProgress != null)
                    handleProgress.Action(extra.Content1, Utilities.getString(extra.Content2, "UTF-8"));
            } else {
                return OperateResultExTwo.CreateSuccessResult(receive.Content1, receive.Content2);
            }
        }
    }

    private OperateResultExOne<byte[]> ReadMqttFromCoreServer(
            byte control, byte flags, byte[] variableHeader, byte[] payLoad,
            ActionOperateExTwo<Long, Long> sendProgress,
            ActionOperateExTwo<String, String> handleProgress,
            ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();

        simpleHybirdLock.Enter();

        // 获取有用的网络通道，如果没有，就建立新的连接
        OperateResultExOne<Socket> resultSocket = GetAvailableSocket();
        if (!resultSocket.IsSuccess) {
            IsSocketError = true;
            if (AlienSession != null) AlienSession.Offline();
            simpleHybirdLock.Leave();
            result.CopyErrorFromOther(resultSocket);
            return result;
        }

        OperateResultExOne<byte[]> command = MqttHelper.BuildMqttCommand(control, flags, variableHeader, payLoad, this.aesCryptography);
        if (!command.IsSuccess) {
            simpleHybirdLock.Leave();
            result.CopyErrorFromOther(command);
            return result;
        }

        OperateResultExTwo<Byte, byte[]> read = ReadMqttFromCoreServer(resultSocket.Content, command.Content, sendProgress, handleProgress, receiveProgress);
        if (read.IsSuccess) {
            IsSocketError = false;
            if ((read.Content1 & 0xf0) >> 4 == MqttControlMessage.FAILED) {
                OperateResultExTwo<String, byte[]> extra = MqttHelper.ExtraMqttReceiveData(read.Content1, read.Content2, this.aesCryptography);
                result.IsSuccess = false;
                result.ErrorCode = Integer.parseInt(extra.Content1);
                result.Message = new String(extra.Content2, StandardCharsets.UTF_8);
            } else {
                result.IsSuccess = read.IsSuccess;
                result.Content = read.Content2;
                result.Message = StringResources.Language.SuccessText();
            }
        } else {
            IsSocketError = true;
            if (AlienSession != null) AlienSession.Offline();
            result.CopyErrorFromOther(read);
        }

        ExtraAfterReadFromCoreServer(read);

        simpleHybirdLock.Leave();
        if (!isPersistentConn) CloseSocket(resultSocket.Content);
        return result;
    }

    /// <summary>
    /// 读取服务器的已经驻留的指定主题的消息内容<br />
    /// Read the topic list of all messages that have resided on the server
    /// </summary>
    /// <param name="topic">指定的主题消息</param>
    /// <param name="receiveProgress">结果进度报告</param>
    /// <returns>消息列表对象</returns>
//    public OperateResultExOne<MqttClientApplicationMessage> ReadTopicPayload( String topic, ActionOperateExTwo<Long, Long> receiveProgress )
//    {
//        OperateResultExOne<byte[]> command = MqttHelper.BuildMqttCommand( MqttControlMessage.PUBREC, (byte) 0x00, MqttHelper.BuildSegCommandByString( topic ), null );
//        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult( command );
//
//        OperateResultExOne<byte[]> read = ReadMqttFromCoreServer( command.Content, null, null, receiveProgress );
//        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );
//
//        OperateResultExTwo<String, byte[]> mqtt = MqttHelper.ExtraMqttReceiveData( MqttControlMessage.PUBLISH, read.Content );
//        if (!mqtt.IsSuccess) return OperateResultExOne.CreateFailedResult( mqtt );
//
//        return OperateResultExOne.CreateSuccessResult( JObject.Parse( new String( mqtt.Content2, StandardCharsets.UTF_8) ).ToObject<MqttClientApplicationMessage>( ) );
//    }

    // endregion

    // region Public Properties

    /**
     * 从MQTT服务器同步读取数据，将payload发送到服务器，然后从服务器返回相关的数据，支持数据发送进度报告，服务器执行进度报告，接收数据进度报告操作<br />
     * Synchronously read data from the MQTT server, send the payload to the server, and then return relevant data from the server,
     * support data transmission progress report, the server executes the progress report, and receives the data progress report
     *
     * @param topic           主题信息
     * @param payload         负载数据
     * @param sendProgress    发送数据给服务器时的进度报告，第一个参数为已发送数据，第二个参数为总发送数据
     * @param handleProgress  服务器处理数据的进度报告，第一个参数Topic自定义，通常用来传送操作百分比，第二个参数自定义，通常用来表示服务器消息
     * @param receiveProgress 从服务器接收数据的进度报告，第一个参数为已接收数据，第二个参数为总接收数据
     * @return 服务器返回的数据信息
     */
    public OperateResultExTwo<String, byte[]> Read(String topic, byte[] payload,
                                                   ActionOperateExTwo<Long, Long> sendProgress,
                                                   ActionOperateExTwo<String, String> handleProgress,
                                                   ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResultExOne<byte[]> read = ReadMqttFromCoreServer(MqttControlMessage.PUBLISH, (byte) 0x00, MqttHelper.BuildSegCommandByString(topic), payload,
                sendProgress, handleProgress, receiveProgress);
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult(read);

        return MqttHelper.ExtraMqttReceiveData(MqttControlMessage.PUBLISH, read.Content, this.aesCryptography);
    }

    /**
     * 从MQTT服务器同步读取数据，将payload发送到服务器，然后从服务器返回相关的数据<br />
     * Synchronously read data from the MQTT server, send the payload to the server, and then return relevant data from the server
     *
     * @param topic   主题信息
     * @param payload 负载数据
     * @return 服务器返回的数据信息
     */
    public OperateResultExTwo<String, byte[]> Read(String topic, byte[] payload) {
        return Read(topic, payload, null, null, null);
    }

    /**
     * 从MQTT服务器同步读取数据，将指定编码的字符串payload发送到服务器，然后从服务器返回相关的数据，并转换为指定编码的字符串，支持数据发送进度报告，服务器执行进度报告，接收数据进度报告操作<br />
     * Synchronously read data from the MQTT server, send the specified encoded string payload to the server,
     * and then return the data from the server, and convert it to the specified encoded string,
     * support data transmission progress report, the server executes the progress report, and receives the data progress report
     *
     * @param topic           主题信息
     * @param payload         负载数据
     * @param sendProgress    发送数据给服务器时的进度报告，第一个参数为已发送数据，第二个参数为总发送数据
     * @param handleProgress  服务器处理数据的进度报告，第一个参数Topic自定义，通常用来传送操作百分比，第二个参数自定义，通常用来表示服务器消息
     * @param receiveProgress 从服务器接收数据的进度报告，第一个参数为已接收数据，第二个参数为总接收数据
     * @return 服务器返回的数据信息
     */
    public OperateResultExTwo<String, String> ReadString(String topic, String payload,
                                                         ActionOperateExTwo<Long, Long> sendProgress,
                                                         ActionOperateExTwo<String, String> handleProgress,
                                                         ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResultExTwo<String, byte[]> read = Read(topic, Utilities.IsStringNullOrEmpty(payload) ? null : Utilities.getBytes(payload, StringEncoding),
                sendProgress, handleProgress, receiveProgress);
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult(read);

        return OperateResultExTwo.CreateSuccessResult(read.Content1, Utilities.getString(read.Content2, StringEncoding));
    }

    /**
     * 读取服务器的已经注册的API信息列表，将返回API的主题路径，注释信息，示例的传入的数据信息。<br />
     * Read the registered API information list of the server, and return the API subject path, annotation information, and sample incoming data information.
     * <p>
     * <br/><br />
     * 注意，返回的数据是JSON格式的
     *
     * @return 包含是否成功的api信息的列表
     */
    public OperateResultExOne<String> ReadRpcApis() {
        OperateResultExOne<byte[]> read = ReadMqttFromCoreServer(MqttControlMessage.SUBSCRIBE, (byte) 0x00, MqttHelper.BuildSegCommandByString(""), null,
                null, null, null);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExTwo<String, byte[]> mqtt = MqttHelper.ExtraMqttReceiveData(MqttControlMessage.PUBLISH, read.Content, this.aesCryptography);
        if (!mqtt.IsSuccess) return OperateResultExOne.CreateFailedResult(mqtt);

        return OperateResultExOne.CreateSuccessResult(new String(mqtt.Content2, StandardCharsets.UTF_8));
    }


    // endregion

    // region Private Member

    /**
     * 读取服务器的已经驻留的所有消息的主题列表<br />
     * Read the topic list of all messages that have resided on the server
     *
     * @return 消息列表对象
     */
    public OperateResultExOne<String[]> ReadRetainTopics() {
        OperateResultExOne<byte[]> read = ReadMqttFromCoreServer(MqttControlMessage.PUBACK, (byte) 0x00, MqttHelper.BuildSegCommandByString(""), null,
                null, null, null);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExTwo<String, byte[]> mqtt = MqttHelper.ExtraMqttReceiveData(MqttControlMessage.PUBLISH, read.Content, this.aesCryptography);
        if (!mqtt.IsSuccess) return OperateResultExOne.CreateFailedResult(mqtt);

        return OperateResultExOne.CreateSuccessResult(HslProtocol.UnPackStringArrayFromByte(mqtt.Content2));
    }

    public MqttConnectionOptions getConnectionOptions() {
        return connectionOptions;
    }

    public void setConnectionOptions(MqttConnectionOptions connectionOptions) {
        this.connectionOptions = connectionOptions;
    }

    /**
     * 获取使用字符串访问的时候，使用的编码信息，默认为UT8编码
     * Get the encoding information used when accessing with a string, the default is UT8 encoding
     *
     * @return 字符串的编码信息
     */
    public String getStringEncoding() {
        return StringEncoding;
    }

    public void setStringEncoding(String stringEncoding) {
        StringEncoding = stringEncoding;
    }

    // endregion

    // region Object Override

    public String toString() {
        return "MqttSyncClient[" + this.connectionOptions.IpAddress + ":" + this.connectionOptions.Port + "]";
    }

    //endregion
}
