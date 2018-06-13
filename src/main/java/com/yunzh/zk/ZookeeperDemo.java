package com.yunzh.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperDemo {

    public static void main(String[] args) {

        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
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

            try {
                countDownLatch.await();
                // System.out.println(zooKeeper.getState());
                // zooKeeper.create("/zk-test","0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                Thread.sleep(1000);
                Stat stat = new Stat();
                byte[] bytes = zooKeeper.getData("/zk-test", null, stat);
                System.out.println(new String(bytes));

                stat = zooKeeper.setData("/zk-test", "1".getBytes(), stat.getVersion());
                byte[] data = zooKeeper.getData("/zk-test", null, stat);
                System.out.println(new String(data));

                stat = zooKeeper.setData("/zk-test", "1".getBytes(), stat.getVersion());
                byte[] datas = zooKeeper.getData("/zk-test", null, stat);
                System.out.println(new String(datas));

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
