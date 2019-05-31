#!/bin/bash

#MongoDB备份脚本
today=`date +%Y%m%d`
mongodump -h localhost -d salary -o /Users/com.zhaohg/mongobackup/$today


#30 1 10 * * /Users/com.zhaohg/mongobackup/backupmongo.sh