package com.busuu.app.services.correction;

import com.busuu.app.dtos.requests.correction.CorrectionDTO;
import com.busuu.app.dtos.requests.post.PostDTO;
import com.busuu.app.dtos.responses.CorrectionResponse;
import com.busuu.app.dtos.responses.PostResponse;
import com.busuu.app.entities.Correction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICorrectionService
{

    CorrectionResponse insertCorrection(String requestId, CorrectionDTO correctionDTO);

    CorrectionResponse getCorrection(String requestId, String correctionId);

    List<CorrectionResponse> getByUserId(String requestId, String userId);

    List<CorrectionResponse> getByPostId(String requestId, String postId);

    Page<CorrectionResponse> getSelfCorrection(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String language);


}
