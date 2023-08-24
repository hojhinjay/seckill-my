package com.example.seckillmy.config;

public interface ILock {

    boolean tryLock(Long timeoutSec);

    void unLock();
}
