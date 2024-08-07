package com.jkzz.smart_mines.communication.HslCommunication.Core.IMessage;

public class MelsecQnA3EBinaryMessage implements INetMessage {
    private byte[] HeadBytes = null;
    private byte[] ContentBytes = null;
    private byte[] SendBytes = null;

    public int ProtocolHeadBytesLength() {
        return 9;
    }

    public int GetContentLengthByHeadBytes() {
        if (HeadBytes == null) return 0;
        return (HeadBytes[7] & 0xff) + (HeadBytes[8] & 0xff) * 256;
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        if (HeadBytes == null) return false;
        if ((HeadBytes[0] & 0xff) == 0xd0 && HeadBytes[1] == 0x00) {
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
