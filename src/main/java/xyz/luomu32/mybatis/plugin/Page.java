package xyz.luomu32.mybatis.plugin;

import java.util.ArrayList;

public class Page<T> extends ArrayList<T> {

    private long total;

    private int pageSize;

    private int currentPage;


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

}
