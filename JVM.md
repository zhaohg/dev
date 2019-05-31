堆内存溢出：死循环给list添加对象
元数据内存溢出：循环加载class
栈内存溢出

内存溢出导出
1、内存溢出自动导出
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./
2、jmap导出内存溢出
jmap -dump:format=b,file=heap.hprof <pid>

jstack Cpu彪高、死锁
jstack <pid> > stack.txt
top -p <pid> -H
printf "%x" <pid> #十进制转16进制

