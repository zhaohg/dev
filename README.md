1、查看是否开启慢查询日志 
	show variables like 'slow_query_log'
2.开启慢查询日志
	set global slow_query_log=on;
3、设置没有索引的记录到慢查询日志 
	set global log_queries_not_using_indexes=on;
4、查看超过多长时间的sql进行记录到慢查询日志 
	show variables like 'long_query_time';
5、设置慢查询的时间 
	set long_query_time=1;
6、设置慢查询文件日志路径
	set global slow_query_log_file="/usr/local/mysql/data/zhaohg-slow.log"

慢查询日志工具
1、mysqldumpslow -t 10 /usr/local/mysql/data/zhaohg-slow.log | more
上面意思是查询比较慢的10条SQL 并返回详细结果

2、pt-query-digest /usr/local/mysql/data/zhaohg-slow.log > show.report 输出到文件
pt-query-digest /usr/local/mysql/data/zhaohg-slow.log -review h=127.0.0.1,D=test,p=root,P=3306,u=root,t=query_review --create-reviewtable --review-history t=hosename_slow

优化SQL
1、查询次数多耗时的
	pt-query-digest分析的前几个
2、IO大的SQL
	pt-query-digest分析的Rows examine项
3、未命中索引的SQL
	pt-query-digest分析的Rows examine、Rows examine的对比

SQL及索引优化
1、使用explain查询SQL执行计划
	> explain select * from store limit 10;
> +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------+
| id | select_type | table | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra |
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------+
|  1 | SIMPLE      | store | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    2 |   100.00 | NULL  |
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

	explain返回各列的含义:
	table:显示这一行的数据是关于哪张表的
	type:这是重要的列，显示连接使用了何种类型。从最好的到最差的连接类型为const/eq_reg/ref/range/index和ALL
	possiable_keys:显示可能应用在这张表中的索引。如果为空，没有可能的索引。
	key:实际使用的索引。如果为Null,则没有使用索引。
	key_len:使用的索引的长度，在不损失精确性的情况下，长度越短越好。
	ref:显示索引的哪一列被使用了，如果可能的话，是一个常数。
	rows:MYSQL认为必须检查的用来返回请求数据的行数。
	Extra: "Using filsort"看到这个就需要优化了，mysql需要额外的步骤来发现如何对返回的行排序。它根据连接的类型以及存储排序键值和匹配条件的全部行的行指针来排序全部行；“Using temporary”看到这个的时候需要优化了，这里Mysql需要创建一个临时表来存储结果，这通常发生在对不同的列集进行order by上，而不是group by上

Count Max优化

> explain select max(payment_date) from payment \G
 	优化->payment_date上创建索引

 	分别查出2006h和2007de行数
	select count(year='2006' or null) as '2006' ,count(year='2007' or null) as '2007' from film;

子查询优化成join查询注意去重

Group by优化
	group by可能会出现临时表（Using temporary），文件排序（Using filesort）等，影响效率。可以通过关联的子查询，group by放到子查询来避免产生临时表和文件排序，可以节省io，
Limit优化
	limit常用于分页，伴随order by 从句 大多数会使用Filesorts，会造成大量io.
	1、应使用有索引的列或者主键进行order by操作
	2、记录上一次返回的主键，在下次查询时使用主键过滤(where)，依赖id连续

如何选择合适的列简历索引
	1、在where、group by、order by、on从句中出现的列
	2、索引字段越小越好
	3、离散度大的列放到联合索引的前面
	select * from payment where staff_id =2 and customer_id = 584;
	是index(sftaff_id,customer_id)好？还是index(customer_id,sftaff_id)好？
	select count(distinct sftaff_id) from payment;
	由于sftaff_id行数少，离散度好，说以需要用index(customer_id,sftaff_id)；
	4、重复索引、冗余索引(包含了主键的联合索引)
		(1)查询重复冗余索引
		useinformation_schema;
		select 
			a.TABLE_SCHEMA AS '数据名',
			a.TABLE_NAME AS '表名',
			a.INDEX_NAME AS '索引1',
			b.INDEX_NAME AS '索引2',
			a.COLUMN_NAME as '重复列名'
		from STATISTICS a JOIN STATISTICS b ON
			a.TABLE_SCHEMA = b.TABLE_SCHEMA 
			AND a.TABLE_NAME = b.TABLE_NAME
			AND a.SEQ_IN_INDEX = b.SEQ_IN_INDEX
			AND a.COLUMN_NAME = b.COLUMN_NAME
		(2)使用pt-duplicate-key-checker工具检查重复冗余索引
		pt-duplicate-key-checker -uroot -p'mysql' -h 127.0.0.1
    5、索引维护方法
        删除不用的索引>
        通过慢日志查询配合pt-index-usage工具分析索引使用情况
    	pt-index-usage -uroot -p 'mysql' mysql-slow.log

表结构优化
    (1)数据类型>
        日期(int存储)：使用from_unixtime()将int类型转换为日期 使用unix_timestamp()将日期转换为int
        IP地址(bigint存储)：插入的时候：INET_ATON(192.158.2.114).读取的时候要INET_NTOA(ip)转化。而将ip地址的存贮形式改为BIGINT
    (2)范式化 避免插入更新删除操作异常或多条数据
    (3)反范式化 适当增加冗余字段已达到优化查询速度的目的
    (4)表垂直拆分 数据过宽（冷热列拆分）
    (5)表水平拆分 数据量过大 1-对id进行hash 然后取模mod(hash(id),5)取出0-4，针对不同的hashId放到不同的表。跨分区表查询，统计等操作；

系统配置优化
    1、操作系统的优化
        网络方面，修改/etc/sysctl.conf文件，增加tcp支持的队列数，减少断开连接时，资源的回收。
        net.ipv4.tcp_max_syn_backlog=65535
        net.ipv4.tcp_max_tw_buckets=8000
        打开文件数的限制。修改/etc/security/limits.conf文件，增加一下内容以修改打开文件数量的限制。
    2、mysql配置优化
        my.cnf文件 
        innodb_buffer_pool_size 用于配置innodb的缓存池，一般配置为总内存的75%   
        innodb_buffer_pool_instances
        innodb_log 
        innodb_flush_log_at_trx_commit 影响io，可取（0、1、2）一般设为2，安全要求高设为1
        innodb_read_io_threads innodb_write_io_threads 默认为4(根据cpu的内核数)
    

总结
order by 用索引
group by用子查询先筛出
limit前加取值范围，取值范围加索引
水平拆分和垂直拆分
水平时前后端分开 前端用分库 后端用总库
垂直时把长度大 不常用的划分到附加表