package com.codeit.findex.global.error;

import com.codeit.findex.global.error.exception.FileDownloadException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handlerIllegalArgumentException(IllegalArgumentException e) {
    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("잘못된 요청입니다.")
            .details(e.getMessage())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlerEntityNotFoundException(EntityNotFoundException e) {
    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message("잘못된 요청입니다.") // API 명세서에 맞게 수정했습니다.
            .details(e.getMessage())
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String details =
        e.getBindingResult().getFieldErrors().stream()
            .findFirst() // field Errors 먼저 확인
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElseGet(
                () ->
                    e.getBindingResult().getGlobalErrors().stream()
                        .findFirst() // 없다면, globalErrors 확인
                        .map(error -> error.getDefaultMessage())
                        .orElse("요청값이 올바르지 않습니다.")); // 둘 다 없다면, 기본 문구 사용

    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("잘못된 요청입니다.")
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // @ModelAttribute 전용
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handlerBindException(BindException e) {
    String details =
        e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("요청 파라미터가 올바르지 않습니다.");

    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("잘못된 요청입니다.")
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // 파일 다운로드 전용 커스텀 예외 생성
  @ExceptionHandler(FileDownloadException.class)
  public ResponseEntity<ErrorResponse> handlerFileDownloadException(FileDownloadException e) {
    log.error("CSV 다운로드 실패: ", e);

    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("파일 다운로드 중 오류가 발생했습니다.")
            .details(e.getMessage())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  // 500 에러
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handlerGeneralException(Exception e) {
    log.error("Internal Server Error Occurred: ", e); // 클라이언트에게 서버 오류의 내역을 보여주지 않도록 처리

    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("서버 내부 오류가 발생했습니다.")
            .details("관리자에게 문의해주세요.")
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
