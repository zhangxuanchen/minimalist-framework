package cn.com.minimalist.framework.simple.dutylist;

import cn.com.minimalist.framework.common.BaseEnum;
import java.io.Serializable;

public class ExecuteResult<T,O> implements Serializable {

    private static final long serialVersionUID = -5442102975340584077L;
    private TransferParam[] entityParam;
    private boolean success = false;
    private boolean pass = false;
    private String errorCode;
    private String errorMsg;
    private T result;

    public TransferParam[] getResultParams() {
        return entityParam;
    }

    public void setResultParams(TransferParam[] entityParam) {
        this.entityParam = entityParam;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

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

    public ExecuteResult<T, O> setResult(T result) {
        this.result = result;
        return this;
    }

    public ExecuteResult() {
    }

    public ExecuteResult(T result) {
        this.result = result;
    }

    public void setError(BaseEnum error) {
        this.setErrorCode(error.getCode());
        this.setErrorMsg(error.getMsg());
        this.setSuccess(false);
        this.setPass(false);
    }

    /**
     * 直接到下个责任者
     * @param <T>
     * @return
     */
    public static <T, O> ExecuteResult<T, O> pass(TransferParam... buildEntityParam) {
        ExecuteResult<T, O> responseResult = new ExecuteResult<>();
        responseResult.setResultParams(buildEntityParam);
        responseResult.setPass(true);
        return responseResult;
    }


    /**
     * 处理成功
     * @return
     */
    public static ExecuteResult success() {
        ExecuteResult responseResult = new ExecuteResult();
        responseResult.setSuccess(true);
        return responseResult;
    }

    /**
     * 携带参数处理成功
     * @param result
     * @param <T>
     * @return
     */
    public static <T, O> ExecuteResult<T, O> success(T result) {
        ExecuteResult<T, O> responseResult = new ExecuteResult<>();
        responseResult.setResult(result);
        responseResult.setSuccess(true);
        return responseResult;
    }

    /**
     * 处理失败
     * @param error
     * @param <T>
     * @return
     */
    public static <T, O> ExecuteResult<T, O> fail(BaseEnum error) {
        ExecuteResult<T, O> responseResult = new ExecuteResult<>();
        responseResult.setPass(false);
        responseResult.setSuccess(false);
        responseResult.setError(error);
        return responseResult;
    }

    public static <T, O> ExecuteResult<T, O> fail(String errorCode, String errorMsg) {
        ExecuteResult<T, O> responseResult = new ExecuteResult();
        responseResult.setPass(false);
        responseResult.setSuccess(false);
        responseResult.setErrorCode(errorCode);
        responseResult.setErrorMsg(errorMsg);
        return responseResult;
    }


    public static Boolean isSuccess(ExecuteResult result) {
        return result != null && result.isSuccess();
    }

    public static Boolean isAvailable(ExecuteResult result) {
        return result != null && result.isSuccess() && result.getResult() != null;
    }

    public static String printError(ExecuteResult result) {
        return null == result ? "NULL" : result.getErrorCode() + "|" + result.getErrorMsg();
    }
}