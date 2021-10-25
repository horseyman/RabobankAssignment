package nl.rabobank.api;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RabobankExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errorMessages = (List)ex.getAllErrors().stream().map((it) -> {
            return it.getDefaultMessage();
        }).collect(Collectors.toList());
        return new ResponseEntity(StringUtils.join(errorMessages, ","), HttpStatus.BAD_REQUEST);
    }
}
