import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

public class test-pdf-simple {
    public static void main(String[] args) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
    }
}

