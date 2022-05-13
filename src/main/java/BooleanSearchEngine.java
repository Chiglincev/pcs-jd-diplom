import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    public static List<HashMap> indexWords = new ArrayList<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        var doc = new PdfDocument(new PdfReader(pdfsDir));
        int maxNumPage = doc.getNumberOfPages();

        Map<String, PageEntry> listResult = new HashMap<>();

        for (int numPage = 1; numPage < maxNumPage; numPage++) {
            var page = doc.getPage(numPage);
            var text = PdfTextExtractor.getTextFromPage(page);
            var words = text.split("\\P{IsAlphabetic}+");

            Map<String, Integer> freqs = new HashMap<>();
            for (var word : words) {
                if (word.isEmpty()) {
                    continue;
                }
                freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
            }

            Iterator<String> iter = freqs.keySet().iterator();
            while (iter.hasNext()) {
                String word = iter.next();
                int count = freqs.get(word);
                listResult.put(word, new PageEntry(pdfsDir.getName(), numPage, count));
            }

            indexWords.add((HashMap)listResult);
        }
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> output = new ArrayList<>();

        for (HashMap<String, PageEntry> listResult : indexWords) {
            Iterator<String> iter = listResult.keySet().iterator();
            while (iter.hasNext()) {
                if (word.equals(iter.next())) {
                    output.add(listResult.get(word));
                }
            }
        }
        output.stream().sorted();
        return output;
    }
}
