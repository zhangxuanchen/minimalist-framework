package cn.com.minimalist.framework.simple.dutylist;

import java.util.List;

public class ExecuteTask {

    private List<Object> builderList;
    private boolean transaction;

    public List<Object> getBuilderList() {
        return builderList;
    }

    public void setBuilderList(List<Object> builderList) {
        this.builderList = builderList;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }
}
