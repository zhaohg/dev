package com.zhaohg.chapter2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class MemoryController {

    private List<User>     userList  = new ArrayList<User>();
    private List<Class<?>> classList = new ArrayList<Class<?>>();

    /**
     * 构建内存溢出
     * -Xmx32M -Xms32M
     */
    @GetMapping("/heap")
    public String heap() {
        int i = 0;
        while (true) {
            userList.add(new User(i++, UUID.randomUUID().toString()));
        }
    }


    /**
     * 构建栈溢出
     * -XX:MetaspaceSize=32M -XX:MaxMetaspaceSize=32M
     */
    @GetMapping("/nonheap")
    public String nonheap() {
        while (true) {
            classList.addAll(Metaspace.createClasses());
        }
    }

}
