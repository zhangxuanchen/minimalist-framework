package cn.com.minimalist.framework.basic.dutylist;

import cn.com.minimalist.framework.common.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
 class ExecutorContainer {

    @Autowired
    private Executor lifeCycleExecutor;

     ResponseResult executeTask(TransferEntity entity, List<Object> builderList)
            throws Exception {
        List<ExecuteTask> executeTaskList = createExecuteTask(builderList);
        ExecuteResult taskExecutorResult = null;
        for (ExecuteTask lifeCycleExecuteTask : executeTaskList) {
            boolean isTransaction = lifeCycleExecuteTask.isTransaction();
            if (!isTransaction) {
                taskExecutorResult = lifeCycleExecutor.run(entity, lifeCycleExecuteTask.getBuilderList());
            } else {
                taskExecutorResult = lifeCycleExecutor.runT(entity, lifeCycleExecuteTask.getBuilderList());
            }
            if (taskExecutorResult.isPass()) {
                continue;
            }
            if(taskExecutorResult.isSuccess()){
                successRoomLogParams(entity);
                if(entity.getReturnResult() == null){
                    return ResponseResult.success();
                }
                return entity.getReturnResult();
            }

            failRoomLogParams(entity, taskExecutorResult.getErrorMsg());
            return ResponseResult.fail(
                    taskExecutorResult.getErrorCode(),
                    taskExecutorResult.getErrorMsg());
        }
        successRoomLogParams(entity);
        if(entity.getReturnResult() == null){
            return ResponseResult.success();
        }
        return entity.getReturnResult();
    }

    private void failRoomLogParams(TransferEntity entity, String errorMsg){
        saveRoomLogParams(false, entity, errorMsg);
    }

    private void successRoomLogParams(TransferEntity entity){
        saveRoomLogParams(true, entity, null);
    }

    private void saveRoomLogParams(boolean isSuccess, TransferEntity entity, String errorMsg) {
       //TODO
    }

    private List<ExecuteTask> createExecuteTask(List<Object> lifeCycleBuilderList) {
        List<ExecuteTask> executeTaskList = new ArrayList<>();
        boolean isTransaction = false;
        ExecuteTask executeTask = null;
        List<Object> builderList = null;

        for (Object builder : lifeCycleBuilderList) {

            if (builder instanceof TransactionPoint) {
                builderList = new ArrayList<>();
                executeTask = new ExecuteTask();
                executeTask.setBuilderList(builderList);
                executeTaskList.add(executeTask);
                isTransaction = !isTransaction;
                executeTask.setTransaction(isTransaction);
                continue;
            }

            if (executeTask == null) {
                builderList = new ArrayList<>();
                executeTask = new ExecuteTask();
                executeTask.setBuilderList(builderList);
                executeTaskList.add(executeTask);
                executeTask.setTransaction(isTransaction);
            }

            builderList.add(builder);
        }

        return executeTaskList;
    }

}
