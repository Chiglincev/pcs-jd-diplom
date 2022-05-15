public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry o) {
        int result = o.count - count;
        if (count != o.count) {
            return result / Math.abs(result);
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return pdfName + " " + page + " " + count;
    }
}
