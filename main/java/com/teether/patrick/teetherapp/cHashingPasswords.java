package com.teether.patrick.teetherapp;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;



public class cHashingPasswords
{
    public static HashCode hash(String value)
    {
        final HashCode hashCode = Hashing.sha1().hashString(value, Charset.defaultCharset());
        return hashCode;
    }
}
