package com.yunzh.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class WatcherDemo {

    public static void main(String[] args) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            ZooKeeper zooKeeper = new ZooKeeper
                    ("192.168.25.130:2181,192.168.25.131:2181,192.168.25.132:2181",
                            4000, new Watcher() {
                        @Override
                        public void process(WatchedEvent watchedEvent) {
                            if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                                System.out.println("zk连接成功");
                                countDownLatch.countDown();
                            }
                        }
                    });
            countDownLatch.await();

            zooKeeper.getData("zk-test", true, null);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
