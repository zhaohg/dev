package com.zhaohg.concurrency.example.publish;

import com.zhaohg.concurrency.annoations.NotRecommend;
import com.zhaohg.concurrency.annoations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
@NotRecommend
public class Escape {

    private int thisCanBeEscape = 0;

    public Escape() {
        new InnerClass();
    }

    public static void main(String[] args) {
        new Escape();
    }

    private class InnerClass {

        public InnerClass() {
            log.info("{}", Escape.this.thisCanBeEscape);
        }
    }
}
