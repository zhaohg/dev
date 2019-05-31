package com.zhaohg.lambda.lambda2;

import java.lang.annotation.Repeatable;


@interface Hints {
    Hint[] value();
}

@Repeatable(Hints.class)
@interface Hint {
    String value();
}