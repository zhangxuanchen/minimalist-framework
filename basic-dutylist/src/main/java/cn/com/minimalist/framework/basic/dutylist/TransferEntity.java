package cn.com.minimalist.framework.basic.dutylist;

import cn.com.minimalist.framework.common.ResponseResult;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TransferEntity {

    private String index;

    private String event;

    private Map<String, Object> map = new HashMap<>();

    private ResponseResult returnResult;

    public void put(String key, Object o){
        map.put(key,o);
    }

    public Object get(String key){
        return map.get(key);
    }

    public ResponseResult getReturnResult() {
        return returnResult;
    }

    public void setReturnResult(ResponseResult returnResult) {
        this.returnResult = returnResult;
    }


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map.putAll(map);
    }

}
