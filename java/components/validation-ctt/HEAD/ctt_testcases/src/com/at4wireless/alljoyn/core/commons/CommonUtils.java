package com.at4wireless.alljoyn.core.commons;

import java.nio.ByteBuffer;
import java.util.UUID;

public class CommonUtils
{
    public static UUID getUuidFromByteArray(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }
}
