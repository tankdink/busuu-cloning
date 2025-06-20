package com.busuu.app.services.question;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.question.QuestionFillBlankResponse;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.busuu.app.dtos.responses.question.QuestionTrueFalseResponse;
import com.busuu.app.dtos.responses.question.matching.QuestionMatchingResponse;
import com.busuu.app.dtos.responses.question.multiple_choice.QuestionMultipleChoiceResponse;
import com.busuu.app.dtos.responses.question.ordering.QuestionOrderingResponse;
import com.busuu.app.entities.questions.Question;
import com.busuu.app.entities.questions.QuestionFillBlank;
import com.busuu.app.entities.questions.QuestionTrueFalse;
import com.busuu.app.entities.questions.QuestionType;
import com.busuu.app.entities.questions.matching.QuestionMatching;
import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import com.busuu.app.entities.questions.ordering.QuestionOrdering;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.InvalidFileException;
import com.busuu.app.repositories.questions.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService implements IQuestionService {
    private final QuestionRepository questionRepository;

    private final ModelMapper modelMapper;


    @Override
    public Page<QuestionResponse> getByLessonId(String requestId, String lessonId, int page, int size, String sortBy, String sortDirection) {
        try {

            //Pageable
            Sort sort;
            if (sortBy.equals("default"))
            {
                sort = Sort.by(
                        Sort.Order.by("grammarSectionId").with(Sort.Direction.fromString(sortDirection)),
                        Sort.Order.by("lessonId").with(Sort.Direction.fromString(sortDirection))
                );
            }
            else { sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection))); }
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Question> questions = questionRepository.findByLessonId(lessonId, pageable);
            return convertQuestionResponse(questions);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get questions by lesson id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    public Page<QuestionResponse> getByGrammarSectionId(String requestId, String grammarSectionId, int page, int size, String sortBy, String sortDirection) {
        try {

            //Pageable
            Sort sort;
            if (sortBy.equals("default"))
            {
                sort = Sort.by(
                        Sort.Order.by("grammarSectionId").with(Sort.Direction.fromString(sortDirection)),
                        Sort.Order.by("lessonId").with(Sort.Direction.fromString(sortDirection))
                );
            }
            else { sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection))); }
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Question> questions = questionRepository.findByGrammarSectionId(grammarSectionId, pageable);
            return convertQuestionResponse(questions);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get questions by grammar section id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    public QuestionResponse getById(String requestId, String questionId)
    {
        Question question = questionRepository.findById(questionId)
                .orElseThrow( ()-> new DataNotFoundException("Cannot find question with ID " + questionId) );

        QuestionResponse response = modelMapper.map(question, QuestionResponse.class);
        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());

        return response;
    }

    @Override
    public Page<QuestionResponse> getByQuestionType(String requestId, String questionType, int page, int size, String sortBy, String sortDirection)
    {
        try {

            //Convert String to enum before passing to repository
            QuestionType type = QuestionType.valueOf(questionType.toUpperCase());

            //Pageable
            Sort sort;
            if (sortBy.equals("default"))
            {
                sort = Sort.by(
                        Sort.Order.by("grammarSectionId").with(Sort.Direction.fromString(sortDirection)),
                        Sort.Order.by("lessonId").with(Sort.Direction.fromString(sortDirection))
                );
            }
            else { sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection))); }
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Question> questions = questionRepository.findByQuestionType(type, pageable);
            return convertQuestionResponse(questions);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get questions by grammar section id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByLessonId(String requestId, String lessonId) {
        try {
            questionRepository.deleteByLessonId(lessonId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete questions by lesson id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByGrammarSectionId(String requestId, String grammarSectionId) {
        try {
            questionRepository.deleteByGrammarSectionId(grammarSectionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete questions by grammar section id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByQuestionId(String requestId, String questionId) {
        try {
            questionRepository.deleteById(questionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete questions by id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_QUESTION, requestId);
        }
    }

    private List<QuestionResponse> convertQuestionResponse(List<Question> questions) {
        return questions.stream().map(
                question -> {
                    if (question instanceof QuestionFillBlank) {
                        QuestionFillBlankResponse response = modelMapper.map(question, QuestionFillBlankResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionTrueFalse) {
                        QuestionTrueFalseResponse response = modelMapper.map(question, QuestionTrueFalseResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionOrdering) {
                        QuestionOrderingResponse response = modelMapper.map(question, QuestionOrderingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMultipleChoice) {
                        QuestionMultipleChoiceResponse response = modelMapper.map(question, QuestionMultipleChoiceResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMatching) {
                        QuestionMatchingResponse response = modelMapper.map(question, QuestionMatchingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else
                    {
                        QuestionResponse response = modelMapper.map(question, QuestionResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }

                }
        ).toList();
    }

    private Page<QuestionResponse> convertQuestionResponse(Page<Question> questions) {
        return questions.map(
                question -> {
                    if (question instanceof QuestionFillBlank) {
                        QuestionFillBlankResponse response = modelMapper.map(question, QuestionFillBlankResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionTrueFalse) {
                        QuestionTrueFalseResponse response = modelMapper.map(question, QuestionTrueFalseResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionOrdering) {
                        QuestionOrderingResponse response = modelMapper.map(question, QuestionOrderingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMultipleChoice) {
                        QuestionMultipleChoiceResponse response = modelMapper.map(question, QuestionMultipleChoiceResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMatching) {
                        QuestionMatchingResponse response = modelMapper.map(question, QuestionMatchingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else
                    {
                        QuestionResponse response = modelMapper.map(question, QuestionResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }

                    //return null;
                }
        );
    }

    @Override
    public String extractQuestionFileWord(MultipartFile file) throws IOException
    {
        String name = file.getOriginalFilename().toLowerCase();

        //.doc file
        if (name.endsWith(".doc"))
        {
            try (HWPFDocument doc = new HWPFDocument(file.getInputStream()))
            {
                return doc.getDocumentText();
            }
        }
        else //.docx file
        {
            try (XWPFDocument docx = new XWPFDocument(file.getInputStream()))
            {
                return docx.getParagraphs()
                        .stream()
                        .map(XWPFParagraph::getText)
                        .collect(Collectors.joining("\n"));
            }
        }

    }

    @Override
    public String extractQuestionFilePDF(MultipartFile file) throws IOException
    {
        File pdfFile = toTempFile(file);
        try (PDDocument pdf = Loader.loadPDF(pdfFile))
        {

            //Valid pdf type
            validPdfType(pdf);

            //Passed
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf);

            return text;

        } finally {
            //Clean up temp file
            if (!pdfFile.delete()) {
                pdfFile.deleteOnExit();
            }
        }
    }

    @Override
    public List<Map<String, Object>> extractQuestionFileExcel(MultipartFile file) throws IOException
    {
        try (Workbook wb = WorkbookFactory.create(file.getInputStream()))
        {
            Sheet sheet = wb.getSheetAt(0);

            //Initial result
            List<Map<String,Object>> rows = new ArrayList<>();

            //Header row process, header row = 0
            Row headerRow = sheet.getRow(0);
            List<String> headers =
                    StreamSupport.stream(headerRow.spliterator(), false)
                            .map(cell -> cell.getStringCellValue())
                            .collect(Collectors.toList());

            //Data row process
            for (int i = 1; i <= sheet.getLastRowNum(); i++)
            {

                //Get each row first
                Row row = sheet.getRow(i);
                if (row == null) continue;

                //Get each cell in row
                Map<String,Object> map = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    Object val = (cell == null) ? null : switch (cell.getCellType()) {
                        case STRING -> cell.getStringCellValue();
                        case NUMERIC -> cell.getNumericCellValue();
                        case BOOLEAN -> cell.getBooleanCellValue();
                        default -> cell.toString();
                    };

                    //Put the value relevant to the header in each row
                    map.put(headers.get(j), val);
                }
                rows.add(map);
            }
            return rows;
        }
    }

    private File toTempFile(MultipartFile multipart) throws IOException
    {
        //Convert MultipartFile to File, for PDF process

        //Get suffix
        String originalName = multipart.getOriginalFilename();
        String suffix = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";

        //Create the temp file in the default tmpdir
        File tempFile = File.createTempFile("upload-", suffix);

        //Transfer and return
        multipart.transferTo(tempFile);
        return tempFile;
    }

    private void validPdfType(PDDocument pdf) throws IOException
    {

        //Secured/Encrypted PDF
        if (pdf.isEncrypted()) {
            throw new InvalidFileException("Secured/Encrypted PDF is not supported");
        }

        //Interactive / Form PDF
        PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();
        if (form != null && !form.getFields().isEmpty()) {
            throw new InvalidFileException("Interactive/Form PDF is not supported");
        }

        //Quick scan first page to check for text vs images (maybe more checked page in future if needed)
        PDFTextStripper peekStripper = new PDFTextStripper();
        peekStripper.setStartPage(1);
        peekStripper.setEndPage(1);
        String sampleText = peekStripper.getText(pdf);
        boolean hasText = sampleText != null && !sampleText.isBlank();

        boolean hasImage = false;
        PDPage first = pdf.getPage(0);
        for (var xobjName : first.getResources().getXObjectNames()) {
            PDXObject xo = first.getResources().getXObject(xobjName);
            if (xo instanceof PDImageXObject) {
                hasImage = true;
                break;
            }
        }

        // Img-based PDF
        if (!hasText && hasImage) {
            throw new InvalidFileException("Image-based PDF is not supported");
        }

        // Hybrid PDF
        if (hasText && hasImage) {
            throw new InvalidFileException("Hybrid PDF is not supported");
        }

        //Rich PDF
        for (PDPage page : pdf.getPages()) {
            for (PDAnnotation ann : page.getAnnotations()) {
                String subtype = ann.getSubtype();
                if ("RichMedia".equals(subtype)) {
                    throw new InvalidFileException("Rich PDF is not supported");
                }
            }
        }

        //Tagged / Accessible PDF
        PDStructureTreeRoot structure = pdf.getDocumentCatalog().getStructureTreeRoot();
        if (structure != null) {
            throw new InvalidFileException("Tagged/Accessible PDF is not supported");
        }

    }

    private void validSyntaxPdf(PDDocument pdf)
    {

    }

    private void validSyntaxWordDocx(XWPFDocument docx)
    {

    }

    private void validSyntaxWordDoc(HWPFDocument doc)
    {

    }

    private void validSyntaxExcel(Workbook wb)
    {

    }
}
