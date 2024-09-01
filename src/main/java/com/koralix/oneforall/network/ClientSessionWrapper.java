package com.koralix.oneforall.network;

public interface ClientSessionWrapper {
    ClientSession session();
    void session(ClientSession session);
}
