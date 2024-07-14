package com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage;

import java.nio.charset.StandardCharsets;

/**
 * 富士SPB的消息内容
 */
public class FujiSPBMessage implements INetMessage {
    private byte[] HeadBytes = null;
    private byte[] ContentBytes = null;
    private byte[] SendBytes = null;

    public int ProtocolHeadBytesLength() {
        return 5;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;
        return Integer.parseInt(new String(HeadBytes, 3, 2, StandardCharsets.US_ASCII), 16) * 2 + 2;
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        return true;
    }

    public int GetHeadBytesIdentity() {
        return 0;
    }

    public byte[] getHeadBytes() {
        return HeadBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        HeadBytes = headBytes;
    }

    public byte[] getContentBytes() {
        return ContentBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        ContentBytes = contentBytes;
    }

    public byte[] getSendBytes() {
        return SendBytes;
    }

    public void setSendBytes(byte[] sendBytes) {
        SendBytes = sendBytes;
    }
}
