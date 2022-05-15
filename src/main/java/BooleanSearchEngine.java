import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    //коллекция списка PageEntry с ключом в виде слова
    public static Map<String, List<PageEntry>> listResult = new HashMap<>();

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

                //создание PageEntry и заполнение списка PageEntry по ключевым словам
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
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> output = new ArrayList<>();

        //проходит по списку всех PageEntry, фильтрует по ключевому слову и выводит список PageEntry.
        for (Map.Entry<String, List<PageEntry>> set : listResult.entrySet()) {
            if (set.getKey().equals(word)) {
                output.addAll(set.getValue());
            }
        }
        output = output.stream().
                            sorted().
                            toList();
        return output;
    }
}
