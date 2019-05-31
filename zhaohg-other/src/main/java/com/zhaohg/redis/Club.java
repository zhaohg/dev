package com.zhaohg.redis;


import java.io.Serializable;
import java.util.Date;

/**
 * 俱乐部
 * @author zhaohg
 * @Date 2016年7月28日
 * @Time 下午1:43:53
 */
public class Club implements Serializable {
    /**
     * 俱乐部id
     */
    private int    id;
    /**
     * 俱乐部名
     */
    private String clubName;
    /**
     * 俱乐部描述
     */
    private String clubInfo;
    /**
     * 创建日期
     */
    private Date   createDate;
    /**
     * 排名
     */
    private int    rank;

    public Club(int id, String clubName, String clubInfo, Date createDate, int rank) {
        super();
        this.id = id;
        this.clubName = clubName;
        this.clubInfo = clubInfo;
        this.createDate = createDate;
        this.rank = rank;
    }

    public Club() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubInfo() {
        return clubInfo;
    }

    public void setClubInfo(String clubInfo) {
        this.clubInfo = clubInfo;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Club [id=" + id + ", clubName=" + clubName + ", clubInfo=" + clubInfo + ", createDate=" + createDate
                + ", rank=" + rank + "]";
    }
}