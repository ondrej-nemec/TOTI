package toti.application;

public class GridRange {
   
    public static GridRange create(int totalCount, int pageIndex, int pageSize) {
        if (totalCount == 0) {
             return new GridRange(0, 0, 1);
        }
        if (pageSize < 1) {
             return new GridRange(0, 0, 1);
        }
        if (pageIndex < 1) {
             pageIndex = 1;
        }
        if ((pageIndex-1) * pageSize >= totalCount) { // if indexing from 0 just '>'
             pageIndex = 1;
        }
        return new GridRange(
             (pageIndex-1) * pageSize, // if indexing from 0 add 1 to total
             Math.min(pageSize, totalCount - (pageIndex-1) * pageSize),
             pageIndex
        );
    }

    private int offset;
    private int limit;
    private int pageIndex;

    private GridRange(int offset, int limit, int pageIndex) {
        this.offset = offset;
        this.limit = limit;
        this.pageIndex = pageIndex;
    }
    
    public int getPageIndex() {
    	return pageIndex;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
