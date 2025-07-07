package com.example.pdf;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/pdf")
public class PDFController {

    @PostMapping("/merge")
    public ResponseEntity<Resource> mergePDFs(@RequestParam("files") MultipartFile[] files) throws IOException {
        PDDocument mergedDoc = new PDDocument();

        for (MultipartFile file : files) {
            PDDocument doc = PDDocument.load(file.getInputStream());
            for (PDPage page : doc.getPages()) {
                mergedDoc.addPage(page);
            }
            doc.close();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mergedDoc.save(out);
        mergedDoc.close();

        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merged.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    @PostMapping("/split")
    public ResponseEntity<List<Resource>> splitPDF(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("ranges") List<String> ranges) throws IOException {
        PDDocument doc = PDDocument.load(file.getInputStream());
        List<Resource> result = new ArrayList<>();

        for (String range : ranges) {
            String[] bounds = range.split("-");
            int start = Integer.parseInt(bounds[0]);
            int end = Integer.parseInt(bounds[1]);

            PDDocument part = new PDDocument();
            for (int j = start; j <= end; j++) {
                part.addPage(doc.getPage(j - 1));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            part.save(out);
            part.close();

            result.add(new ByteArrayResource(out.toByteArray()));
        }

        doc.close();
        return ResponseEntity.ok(result);
    }
}