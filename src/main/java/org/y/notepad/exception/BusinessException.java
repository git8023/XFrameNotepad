package org.y.notepad.exception;

import lombok.Data;
import org.y.notepad.model.enu.ErrorCode;

@Data
public class BusinessException extends RuntimeException {

  private static final long serialVersionUID = -4976200449348437208L;
  private ErrorCode errorCode = ErrorCode.SYSTEM_ERROR;

  public BusinessException() {}

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessException(Throwable cause) {
    super(cause);
  }

  public BusinessException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, String message) {
    this(message);
    this.errorCode = errorCode;
  }
}
