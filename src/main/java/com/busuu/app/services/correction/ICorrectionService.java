package com.busuu.app.services.correction;

import com.busuu.app.dtos.requests.correction.CorrectionDTO;
import com.busuu.app.dtos.responses.CorrectionResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ICorrectionService
{

    CorrectionResponse insertCorrection(String requestId, CorrectionDTO correctionDTO);

    CorrectionResponse getCorrection(String requestId, String correctionId);

    List<CorrectionResponse> getByUserId(String requestId, String userId);

    List<CorrectionResponse> getByPostId(String requestId, String postId);

    Page<CorrectionResponse> getSelfCorrection(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String language);

    CorrectionResponse reaction(String requestId, String correctionId, String reaction);

}
