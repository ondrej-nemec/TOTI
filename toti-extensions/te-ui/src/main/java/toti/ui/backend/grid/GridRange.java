package toti.ui.backend.grid;

public class GridRange {
   
    public static GridRange create(int totalCount, Integer pageIndex, Integer pageSize) {
        if (totalCount == 0) {
             return new GridRange(0, 0, 1);
        }
        if (pageSize == null || pageIndex == null) {
        	return new GridRange();
        }
        if (pageSize < 1) {
        	pageSize = totalCount; // return new GridRange(0, 0, 1);
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
    
    private final boolean used;
    
    public GridRange() {
		this.used = false;
	}

    private GridRange(int offset, Integer limit, Integer pageIndex) {
        this.offset = offset;
        this.limit = limit;
        this.pageIndex = pageIndex;
        this.used = true;
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
    
    public boolean isPresent() {
    	return used;
    }

	@Override
	public String toString() {
		return "GridRange [offset=" + offset + ", limit=" + limit + ", pageIndex=" + pageIndex + "]";
	}
}
