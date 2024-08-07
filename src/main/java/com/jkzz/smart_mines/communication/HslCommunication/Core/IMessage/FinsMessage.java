package com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage;

import com.jkzz.smart_mines.communication.HslCommunication.Utilities;

public class FinsMessage implements INetMessage {
    private byte[] HeadBytes = null;
    private byte[] ContentBytes = null;
    private byte[] SendBytes = null;

    public int ProtocolHeadBytesLength() {
        return 8;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;

        byte[] buffer = new byte[4];
        buffer[0] = HeadBytes[7];
        buffer[1] = HeadBytes[6];
        buffer[2] = HeadBytes[5];
        buffer[3] = HeadBytes[4];
        return Utilities.getInt(buffer, 0);
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        if (HeadBytes == null) return false;
        if (HeadBytes[0] == 0x46 && HeadBytes[1] == 0x49 && HeadBytes[2] == 0x4E && HeadBytes[3] == 0x53) {
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
