import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    //коллекция списков PageEntry с ключом в виде слова
    public Map<String, List<PageEntry>> listResult = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        for (File pdf : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(pdf));
            int maxNumPage = doc.getNumberOfPages();

            for (int numPage = 1; numPage < maxNumPage; numPage++) {
                var page = doc.getPage(numPage);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");

                //подсчёт частоты слов
                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
                }

                //создание PageEntry и заполнение списка PageEntry по ключевому слову
                for (Map.Entry<String, Integer> set : freqs.entrySet()) {
                    String key = set.getKey();
                    int value = set.getValue();
                    PageEntry pageEntry = new PageEntry(pdf.getName(), numPage, value);

                    if (!listResult.containsKey(key)) {
                        listResult.put(key, new ArrayList<PageEntry>());
                    }
                    listResult.get(key).add(pageEntry);
                }
            }
        }
        //сортировка по количеству совпадений
        for (Map.Entry<String, List<PageEntry>> set : listResult.entrySet()) {
            listResult.put(set.getKey(), set.getValue().stream().sorted().toList());
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return listResult.get(word);
    }
}
