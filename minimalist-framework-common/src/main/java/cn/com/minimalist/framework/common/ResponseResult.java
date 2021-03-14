package cn.com.minimalist.framework.common;


import java.io.Serializable;

public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = -5442102975340584077L;
    private boolean success = false;
    private String errorCode;
    private String errorMsg;
    private T result;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
        this.setSuccess(true);
    }

    public ResponseResult() {
    }

    public ResponseResult(T result) {
        this.result = result;
        this.setSuccess(true);
    }

    public void setError(BaseEnum error) {
        this.setErrorCode(error.getCode());
        this.setErrorMsg(error.getMsg());
        this.setSuccess(false);
    }

    public static ResponseResult success() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setResult("succ");
        responseResult.setSuccess(true);
        return responseResult;
    }

    public static <T> ResponseResult<T> success(T result) {
        ResponseResult<T> responseResult = new ResponseResult();
        responseResult.setResult(result);
        responseResult.setSuccess(true);
        return responseResult;
    }

    public static <T> ResponseResult<T> fail(BaseEnum error) {
        ResponseResult<T> responseResult = new ResponseResult();
        responseResult.setSuccess(false);
        responseResult.setError(error);
        return responseResult;
    }

    public static <T> ResponseResult<T> fail(String errorCode, String errorMsg) {
        ResponseResult<T> responseResult = new ResponseResult();
        responseResult.setSuccess(false);
        responseResult.setErrorCode(errorCode);
        responseResult.setErrorMsg(errorMsg);
        return responseResult;
    }

    public static Boolean isSuccess(ResponseResult result) {
        return result != null && result.isSuccess();
    }

    public static Boolean isAvailable(ResponseResult result) {
        return result != null && result.isSuccess() && result.getResult() != null;
    }

    public static String printError(ResponseResult result) {
        return null == result ? "NULL" : result.getErrorCode() + "|" + result.getErrorMsg();
    }

}

