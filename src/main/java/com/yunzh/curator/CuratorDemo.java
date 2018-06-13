package com.yunzh.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class CuratorDemo {

    public static void main(String[] args) throws Exception {

        CuratorFramework cratorFramework = CuratorFrameworkFactory
                .builder()
                // 连接zookeeper集群
                .connectString("192.168.25.130:2181,192.168.25.131:2181,192.168.25.132:2181")
                // 超时时间
                .sessionTimeoutMs(4000)
                // 重发机制，如果连接失败每隔1s重新连接一次，尝试3次
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                // 设置根节点
                .namespace("curator")
                .build();

        cratorFramework.start();

        // add
       cratorFramework.create()
               // 如果父节点不存在，可以一同创建出来
                .creatingParentsIfNeeded()
               // 创建持久节点
                .withMode(CreateMode.PERSISTENT)
                .forPath("/yunzh/mic","hello".getBytes());

        // query
        Stat stat = new Stat();
        byte[] bytes = cratorFramework.getData()
                // 将状态储存到stat中
                .storingStatIn(stat)
                .forPath("/yunzh/mic");
        System.out.println(new String(bytes));

        // delete
        cratorFramework.delete()
                // 删除子节点
                .deletingChildrenIfNeeded()
                .forPath("/yunzh/mic");

        // set
        cratorFramework.setData()
                // 修改需要version值
                .withVersion(stat.getVersion())
                .forPath("/yunzh/mic", "nihao".getBytes());

        cratorFramework.close();
    }
}
