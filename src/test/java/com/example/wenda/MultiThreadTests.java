package com.example.wenda;


import javax.annotation.security.RunAs;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread {
    private int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println(String.format("%d: %d", tid, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{
    private BlockingQueue<String> q;
    public Consumer(BlockingQueue<String> q){
        this.q = q;
    }
    @Override
    public void run() {
        try{
            //消费线程，不停地取，打印
            while(true){
                System.out.println(Thread.currentThread().getName() + " : " + q.take());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
class Producer implements Runnable{
    private BlockingQueue<String> q;
    public Producer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try{
            //插入线程
            for(int i = 0; i < 100; i++){
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

public class MultiThreadTests {
    public static void testThread() {
        /*
        for(int i = 0; i < 10; i++){
            new MyThread(i).start();
        }
        */
        for (int i = 0; i < 10; i++) {
            final int finali = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; j++) {
                            Thread.sleep(1000);
                            System.out.println(String.format("%d: %d", finali, j));
                        }
                    } catch (Exception e) {

                    }
                }
            }).start();
        }
    }

    public static Object obj = new Object();

    public static void testSynchronized1() {
        synchronized (obj) {
            try {
                for (int j = 0; j < 10; j++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3: %d", j));
                }
            } catch (Exception e) {

            }
        }
    }

    public static void testSynchronized2() {
        synchronized (new Object()) {
            try {
                for (int j = 0; j < 10; j++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4: %d", j));
                }
            } catch (Exception e) {

            }
        }
    }

    public static void testSynchronized() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //因为T3和T4共享同一个对象，所以谁先进入，就等着，等到进去的把10个数都打完，才能再用
                    //如果它们没共享同一个对象的话，就自己打自己的。
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue() {
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "consumer1").start();
        new Thread(new Consumer(q), "consumer2").start();
    }


    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static int userId;

    public static void testThreadLocal() {
        //10条线程，每条都设置线程的userId,睡一秒后打印出来
        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //
                    try {
                        threadLocalUserIds.set(finalI);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:" + threadLocalUserIds.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //将所有东西提交给线程池
    public static void testExecutor() {
        ExecutorService service = Executors.newSingleThreadExecutor();//单线程执行框架，执行完了一个线程以后才执行第二个
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1 : " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2 : " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void testExecutor2() {
        ExecutorService service = Executors.newFixedThreadPool(2);//并行打印出来
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1 : " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2 : " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        service.shutdown();//任务执行完毕后关闭
    }

    private static int count = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void testWithoutAtomic() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++)
                            count++;
                        System.out.println(count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testAtomic() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++)
                            atomicInteger.getAndAdd(1);
                        System.out.println(atomicInteger);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }
    }
    public static void testFuture(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 1;
            }
        });
        service.shutdown();
        try{
            //等待直到计算完成,然后再取值
            System.out.println(future.get());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //testThread();
        //testSynchronized();
        //testBlockingQueue();
        //testThreadLocal();
        //testExecutor2();
        //testWithoutAtomic();
        //testAtomic();
        testFuture();
    }
}
