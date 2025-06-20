package com.busuu.app.services.question;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.question.QuestionFillBlankResponse;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.busuu.app.dtos.responses.question.QuestionTrueFalseResponse;
import com.busuu.app.dtos.responses.question.matching.MatchingPairResponse;
import com.busuu.app.dtos.responses.question.matching.QuestionMatchingResponse;
import com.busuu.app.dtos.responses.question.multiple_choice.MultipleChoiceOptionResponse;
import com.busuu.app.dtos.responses.question.multiple_choice.QuestionMultipleChoiceResponse;
import com.busuu.app.dtos.responses.question.ordering.OrderingPartResponse;
import com.busuu.app.dtos.responses.question.ordering.QuestionOrderingResponse;
import com.busuu.app.entities.questions.Question;
import com.busuu.app.entities.questions.QuestionFillBlank;
import com.busuu.app.entities.questions.QuestionTrueFalse;
import com.busuu.app.entities.questions.QuestionType;
import com.busuu.app.entities.questions.ShowType;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
    public List<QuestionResponse> extractQuestionFilePDF(MultipartFile file) throws IOException
    {
        File pdfFile = toTempFile(file);
        try (PDDocument pdf = Loader.loadPDF(pdfFile))
        {

            //Valid pdf type
            validPdfType(pdf);

            //Valid syntax PDF
            validSyntaxPdf(pdf);

            //Passed
            return getQuestionFromFilePdf(pdf);

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
                            .toList();

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

    private void validSyntaxPdf(PDDocument pdf) throws IOException
    {

        final int MAX_CONTENT_LENGTH = 200000;
        final int MAX_QUESTIONS = 100;
        final int MAX_FIELD_LENGTH = 300;

        PDFTextStripper stripper = new PDFTextStripper();
        String content = stripper.getText(pdf);

        //Spam check
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidFileException("File is empty or contains only whitespace/newlines");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new InvalidFileException("File too long (exceeds " + MAX_CONTENT_LENGTH + " chars)");
        }


        //Split the whole file content to each line and collect to a list
        List<String> lines = Arrays.stream(content.split("\\r?\\n\\s*\\r?\\n"))
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .toList();

        if (lines.size() > MAX_QUESTIONS + 1) {
            throw new InvalidFileException("Too many questions in file (max " + MAX_QUESTIONS + ")");
        }

        if (lines.size() <= 1) {
            throw new InvalidFileException("No questions found (missing content after header)");
        }

        //Field name for checking field length
        String[] fieldNames = {
                "Question type", "Show type", "Question request",
                "Question text", "Explanation", "Script audio", "Answer"
        };
        List<String> showType = Arrays.asList("TIP","VOCAB","NORMAL");
        List<String> trueFalseAnswer = Arrays.asList("true","false","TRUE","FALSE");

        //Validate syntax for each line/question
        //Ignore first line for language
        List<String> questionLines = lines.subList(1, lines.size());
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < questionLines.size(); i++)
        {
            String line = questionLines.get(i);
            try {

                //Split by ];[
                String[] parts = line.split("\\]\\s*;\\s*\\[", -1);
                if (parts.length != 7) throw new IllegalArgumentException("Expected 7 fields but found " + parts.length);


                //Trim and remove the [ of the question type and the ] of answer to validate answer in next step, trim other part
                //This part code is duplicate with reading data for whole question, can be remove in the future
                for (int j = 0; j < 7; j++)
                {
                    String p = parts[j].trim();
                    if (j == 0 && p.startsWith("[")) p = p.substring(1);
                    if (j == 6 && p.endsWith("]")) p = p.substring(0, p.length() - 1);
                    p = p.trim();

                    if (p.length() > MAX_FIELD_LENGTH) {
                        throw new IllegalArgumentException(
                                fieldNames[j] + " exceeds max length of " + MAX_FIELD_LENGTH + " chars"
                        );
                    }

                    if (j == 1 && !showType.contains(p)) throw new InvalidFileException(" Show type must be TIP, VOCAB or NORMAL");

                    parts[j] = p;

                }

                String questionType = parts[0];
                String answer = parts[6];
                if (questionType == "TRUE_FALSE" && !trueFalseAnswer.contains(answer)) throw new InvalidFileException(" Answer for TRUE_FALSE question must be [true] or [false] (case-insensitive)");


                //Validate answer's syntax
                validSyntaxAnswer(answer, questionType);

            } catch (Exception ex) {
                errors.add("Question " + (i+1) + ": " + ex.getMessage());
            }
        }

        //Return all error
        if (!errors.isEmpty())
        {
            String errorList = errors.stream().collect(Collectors.joining("\n"));
            throw new InvalidFileException("Invalid syntax in file:\n" + errorList);
        }

    }

    private List<QuestionResponse> getQuestionFromFilePdf(PDDocument pdf) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        String content = stripper.getText(pdf);

        List<String> lines = Arrays.stream(content.split("\\r?\\n\\s*\\r?\\n"))
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .toList();


        //Ignore language field
        List<String> questionLines = lines.subList(1, lines.size());
        List<QuestionResponse> result = new ArrayList<>();

        //Dummy data
        Question dummyQuestion = new Question();
        String dummyAnswer = null;

        for (int i = 0; i < questionLines.size(); i++)
        {
            String line = questionLines.get(i);

            try {

                //Split by ];[
                String[] parts = line.split("\\]\\s*;\\s*\\[", -1);


                //Pass each field
                for (int j = 0; j < 7; j++) {
                    String p = parts[j].trim();
                    if (j == 0 && p.startsWith("[")) p = p.substring(1);
                    else if (j == 6 && p.endsWith("]")) p = p.substring(0, p.length() - 1);
                    p = p.trim();

                    //Add data to dummy
                    switch (j)
                    {
                        case 0:
                        {
                            dummyQuestion.setQuestionType(QuestionType.valueOf(p));
                            break;
                        }
                        case 1:
                        {
                            dummyQuestion.setShowType(ShowType.valueOf(p));
                            break;
                        }
                        case 2:
                        {
                            dummyQuestion.setRequest(p);
                            break;
                        }
                        case 3:
                        {
                            dummyQuestion.setQuestionText(p);
                            break;
                        }
                        case 4:
                        {
                            dummyQuestion.setExplanation(p);
                            break;
                        }
                        case 5:
                        {
                            dummyQuestion.setScriptAudio(p);
                            break;
                        }
                        case 6:
                        {
                            dummyAnswer = p;
                            break;
                        }
                        default: break;

                    }

                }

                switch (dummyQuestion.getQuestionType()) {
                    case FILL_BLANK:
                    {
                        QuestionFillBlankResponse response = modelMapper.map(dummyQuestion, QuestionFillBlankResponse.class);
                        Set<String> answer = Arrays.stream(dummyAnswer.split(","))
                                        .map(String::trim)
                                        .collect(Collectors.toSet());
                        response.setCorrectAnswer(answer);
                        result.add(response);
                        break;
                    }
                    case TRUE_FALSE:
                    {
                        QuestionTrueFalseResponse response = modelMapper.map(dummyQuestion, QuestionTrueFalseResponse.class);
                        response.setCorrectAnswer(Boolean.parseBoolean(dummyAnswer));
                        result.add(response);
                        break;
                    }
                    case ORDERING:
                    {
                        QuestionOrderingResponse response = modelMapper.map(dummyQuestion, QuestionOrderingResponse.class);

                        List<OrderingPartResponse> part = new ArrayList<>();


                        //Split the answer
                        String[] answerSplit = dummyAnswer.split("\\]\\s*,\\s*\\[", 2);

                        //Remove [ and ]
                        String listPart = answerSplit[0].trim();
                        if (listPart.startsWith("[")) {
                            listPart = listPart.substring(1).trim();
                        }

                        String answerPart = answerSplit[1].trim();
                        if (answerPart.endsWith("]")) {
                            answerPart = answerPart.substring(0, answerPart.length() - 1).trim();
                        }

                        //Part process
                        List<String> items = Arrays.stream(listPart.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());

                        for (String item : items)
                        {
                            OrderingPartResponse partResponse = new OrderingPartResponse();
                            partResponse.setSentencePart(item);
                            part.add(partResponse);
                        }

                        response.setCorrectAnswer(answerPart);
                        response.setParts(part);
                        result.add(response);
                        break;
                    }
                    case MULTIPLE_CHOICE:
                    {
                        QuestionMultipleChoiceResponse response = modelMapper.map(dummyQuestion, QuestionMultipleChoiceResponse.class);

                        List<MultipleChoiceOptionResponse> optionResponseList = new ArrayList<>();

                        //Split the answer
                        String[] answerSplit = dummyAnswer.split("\\]\\s*,\\s*\\[", 2);

                        //Remove [ and ]
                        String option = answerSplit[0].trim();
                        if (option.startsWith("[")) {
                            option = option.substring(1).trim();
                        }

                        String answer = answerSplit[1].trim();
                        if (answer.endsWith("]")) {
                            answer = answer.substring(0, answer.length() - 1).trim();
                        }

                        //Option - Answer process
                        List<String> optionList = Arrays.stream(option.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());

                        List<String> answerList = Arrays.stream(answer.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());

                        if (optionList.size() != answerList.size()) throw new InvalidFileException(" Invalid number of pair in question " + (i+1) +": Both side must be equal!");



                        for (int j = 0; j < optionList.size(); j++)
                        {
                            MultipleChoiceOptionResponse optionResponse = new MultipleChoiceOptionResponse();
                            optionResponse.setOptionText(optionList.get(j));
                            optionResponse.setIsCorrect(Boolean.parseBoolean(answerList.get(j)));
                            optionResponseList.add(optionResponse);
                        }

                        response.setOptions(optionResponseList);
                        result.add(response);
                        break;
                    }
                    case MATCHING:
                    {
                        QuestionMatchingResponse response = modelMapper.map(dummyQuestion, QuestionMatchingResponse.class);

                        List<MatchingPairResponse> pairsList = new ArrayList<>();

                        //Split the answer
                        String[] answerSplit = dummyAnswer.split("\\]\\s*,\\s*\\[", 2);

                        //Remove [ and ]
                        String option = answerSplit[0].trim();
                        if (option.startsWith("[")) {
                            option = option.substring(1).trim();
                        }

                        String answer = answerSplit[1].trim();
                        if (answer.endsWith("]")) {
                            answer = answer.substring(0, answer.length() - 1).trim();
                        }

                        //Option - Answer process
                        List<String> optionListLeft = Arrays.stream(option.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());

                        List<String> optionListRight = Arrays.stream(answer.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());

                        if (optionListLeft.size() != optionListRight.size()) throw new InvalidFileException(" Invalid number of pair in question " + (i+1) +": Both side must be equal!");

                        for (int j = 0; j < optionListLeft.size(); j++)
                        {
                            UUID uuid = UUID.randomUUID();

                            MatchingPairResponse pairResponse1 = new MatchingPairResponse();
                            pairResponse1.setPairText(optionListLeft.get(j));
                            pairResponse1.setPairOrder(1);
                            pairResponse1.setPairKey(uuid.toString());
                            pairsList.add(pairResponse1);

                            MatchingPairResponse pairResponse2 = new MatchingPairResponse();
                            pairResponse2.setPairText(optionListRight.get(j));
                            pairResponse2.setPairOrder(2);
                            pairResponse2.setPairKey(uuid.toString());
                            pairsList.add(pairResponse2);
                        }

                        response.setPairs(pairsList);
                        result.add(response);
                        break;
                    }
                    case KNOWLEDGE:
                    {
                        QuestionResponse response = modelMapper.map(dummyQuestion, QuestionResponse.class);
                        result.add(response);
                        break;
                    }
                    default:
                    {
                        throw new InvalidFileException("There are errors in getting question type");
                    }

                }

            } catch (Exception ex) {
                throw new InvalidFileException("There are errors in reading file process: " + ex.getMessage());
            }
        }

        return result;

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

    private void validSyntaxAnswer(String answer, String questionType)
    {
        switch (questionType) {
            case "FILL_BLANK":
            case "KNOWLEDGE":
            case "TRUE_FALSE":
                //Only one bracketed value
                if (!answer.matches("^\\s*[^\\]]+\\s*$")) {
                    throw new InvalidFileException("Answer for " + questionType + " must be [true] or [false] or [nan] with KNOWLEDGE type");
                }
                break;

            case "MULTIPLE_CHOICE":
            case "ORDERING":
            case "MATCHING":
                //Two bracketed items  [value1],[value2]
                if (!answer.matches("^\\s*\\s*\\[[^\\]]+\\]\\s*,\\s*\\[[^\\]]+\\]\\s*\\s*$")) {
                    throw new InvalidFileException("Answer for " + questionType + " must be [[part1],[part2]]");
                }
                break;

            default:
                throw new InvalidFileException("Unknown question type: " + questionType);
        }
    }
}
