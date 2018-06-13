package com.yunzh.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorWatcher {

    public static void main(String[] args) throws Exception {

        // 连接zookeeper
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

        //addListenerWithPathChildCache(cratorFramework, "/yunzh");
        //addListenerWithNodeCache(cratorFramework, "/yunzha");
        addListenerWithTreeCache(cratorFramework,"/yunzh");

        System.in.read();
    }

    /**
     * PathChildCache 监听一个节点下子节点的创建、删除、更新
     * NodeCache  监听一个节点的更新和创建事件
     * TreeCache  综合PatchChildCache和NodeCache的特性
     */

    /**
     * 创建一个PathChildCache监控子节点
     * Receive Event:CONNECTION_RECONNECTED
     * Receive Event:CHILD_ADDED
     * Receive Event:CHILD_REMOVED
     * Receive Event:CHILD_ADDED
     * Receive Event:CHILD_UPDATED
     * @param cratorFramework
     * @param path
     * @throws Exception
     */
    public static void addListenerWithPathChildCache(CuratorFramework cratorFramework, String path) throws Exception {

        PathChildrenCache pathChildrenCache =
                new PathChildrenCache(cratorFramework, path, false);

        PathChildrenCacheListener pathChildrenCacheListener =
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                        System.out.println("Receive Event:" + event.getType());
                    }
                };

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

        pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
    }

    // 创建一个nodecache监控某个节点
    public static void addListenerWithNodeCache(CuratorFramework curatorFramework, String path) throws Exception {
        final NodeCache nodeCache =
                new NodeCache(curatorFramework, path, false);

        NodeCacheListener nodeCacheListener = new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("Receive Event:" + nodeCache.getCurrentData().getPath());
            }
        };

        nodeCache.getListenable().addListener(nodeCacheListener);

        nodeCache.start();
    }

        /*
            NODE_ADDED-->/yunzh
            NODE_UPDATED-->/yunzh
            NODE_REMOVED-->/yunzh
            NODE_ADDED-->/yunzh
            NODE_ADDED-->/yunzh/mic
            NODE_UPDATED-->/yunzh/mic
            NODE_REMOVED-->/yunzh/mic
        */
    public static void addListenerWithTreeCache(CuratorFramework curatorFramework, String path) throws Exception {

        TreeCache treeCache = new TreeCache(curatorFramework, path);

        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
                System.out.println(event.getType() + "-->" + event.getData().getPath());
            }
        };

        treeCache.getListenable().addListener(treeCacheListener);

        treeCache.start();
    }
}
