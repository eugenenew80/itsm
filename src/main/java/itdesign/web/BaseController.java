package itdesign.web;

import itdesign.web.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseController {
    protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler( { Throwable.class } )
    public ResponseEntity<ErrorDto> handleException(Throwable exc) {
        ErrorDto errorDto = new ErrorDto(exc);
        logger.error( errorDto.getErrType() + ": " + errorDto.getErrDetails());
        logger.trace("view stack trace for details:", exc);
        return new ResponseEntity<>(errorDto,  errorDto.getErrStatus());
    }
}
