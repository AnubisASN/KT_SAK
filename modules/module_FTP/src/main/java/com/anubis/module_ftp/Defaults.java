/*
Copyright 2011-2013 Pieter Pareit
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.anubis.module_ftp;

public class Defaults {
    protected static int inputBufferSize = 256;
    public static int dataChunkSize = 65536;  //做64k数据块的文件I/O
    public static final int tcpConnectionBacklog = 5;
    public static final int SO_TIMEOUT_MS = 30000; // 套接字超时毫秒，根据RFC，FTP控制会话应该从ASCII开始。
    //然而，许多客户端即使支持它，也不会打开UTF-8，所以我们只是默认打开它。
    public static final String SESSION_ENCODING = "UTF-8";

    public static final boolean do_mediascanner_notify = true;

    public static int getInputBufferSize() {
        return inputBufferSize;
    }

    public static void setInputBufferSize(int inputBufferSize) {
        Defaults.inputBufferSize = inputBufferSize;
    }

    public static int getDataChunkSize() {
        return dataChunkSize;
    }

    public static void setDataChunkSize(int dataChunkSize) {
        Defaults.dataChunkSize = dataChunkSize;
    }
}
