package com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage;

import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

public class MelsecQnA3EAsciiMessage implements INetMessage {
    private byte[] HeadBytes = null;
    private byte[] ContentBytes = null;
    private byte[] SendBytes = null;

    public int ProtocolHeadBytesLength() {
        return 18;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;

        byte[] buffer = new byte[4];
        buffer[0] = HeadBytes[14];
        buffer[1] = HeadBytes[15];
        buffer[2] = HeadBytes[16];
        buffer[3] = HeadBytes[17];

        return Integer.parseInt(Utilities.getString(buffer, "ascii"), 16);
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        if (HeadBytes == null) return false;
        if (HeadBytes[0] == (byte) 'D' && HeadBytes[1] == (byte) '0' && HeadBytes[2] == (byte) '0' && HeadBytes[3] == (byte) '0') {
            return true;
        } else {
            return false;
        }
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
