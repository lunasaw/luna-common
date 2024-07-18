package com.luna.common.page;

import java.io.Serializable;

public class PageQuery implements Serializable {

    private static final long serialVersionUID      = -8021019118985676905L;
    protected int             page;
    protected int             pageSize;
    protected Integer         offset;
    public static final int   DEFAULT_PAGE_SIZE     = 10;

    public static final int   DEFAULT_MAX_PAGE_SIZE = 100;

    public int getPage() {
        if (page <= 0) {
            return 0;
        }
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        if (pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        if (offset != null) {
            return offset;
        }
        pageSize = (pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        pageSize = Math.min(pageSize, DEFAULT_MAX_PAGE_SIZE);
        return page * pageSize;
    }

}
