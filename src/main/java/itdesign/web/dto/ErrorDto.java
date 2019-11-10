package itdesign.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RequiredArgsConstructor
@JsonPropertyOrder({ "errType", "errStatus", "errMsg", "errDetails" })
public class ErrorDto {
    private final Throwable exc;

    public String getErrType() {
        return exc.getClass().getSimpleName();
    };

    public String getErrDetails() {
        if (exc instanceof DataAccessException) {
            if (exc instanceof DataIntegrityViolationException && exc.getCause().getCause() !=null && exc.getCause().getCause().getCause() != null)
                return exc.getCause().getCause().getCause().getMessage();

            if (exc instanceof DataIntegrityViolationException && exc.getCause().getCause() !=null)
                return exc.getCause().getCause().getMessage();

            if (exc instanceof DataIntegrityViolationException && exc.getCause() !=null)
                return exc.getCause().getMessage();
        }
        return exc.getMessage() != null ? exc.getMessage() : exc.getClass().getSimpleName();
    };

    public String getErrMsg() {
        if (exc instanceof HttpMessageNotReadableException)
            return "JSON parse error";

        if (exc instanceof DataAccessException)
            return "Database access exception";

        return "Unknown application exception";
    };

    public HttpStatus getErrStatus() {
        if (exc instanceof HttpMessageNotReadableException)
            return HttpStatus.BAD_REQUEST;

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
